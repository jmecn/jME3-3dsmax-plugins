/**
 * Make a donation http://sourceforge.net/donate/index.php?group_id=98797
 * Microcrowd.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Contact Josh DeFord jdeford@realvue.com
 */

package com.microcrowd.loader.java3d.max3ds;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Light;
import javax.media.j3d.Texture;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import com.microcrowd.loader.java3d.max3ds.chunks.Chunk;
import com.microcrowd.loader.java3d.max3ds.data.KeyFramer;
import com.sun.j3d.loaders.SceneBase;
import com.sun.j3d.utils.image.TextureLoader;

/**
 * A singleton flyweight factory responsible for chopping the 
 * data up and sending it to the corresponding 
 * chunks(which are flyweights ala the flyweight pattern)
 * for processing.
 * This will sequentially read a 3ds file, load or
 * skip chunks and subchunks and initialize the data
 * for the chunks.  
 * <p>
 * Retrieved data may be stored as state in the ChunkChopper
 * via {@link #pushData} for use by other chunks.
 * <p> 
 * Features not supported; unknown chunks are skipped.
 */
public class ChunkChopper
{
    private Logger logger = Logger.getLogger(ChunkChopper.class.getName());

    private Loader3DS      loader;
    private BranchGroup    sceneGroup;
    private SceneBase      base;
    private HashMap        dataMap;
    private ByteBuffer     chunkBuffer;
    private Integer        chunkID;

    private TransformGroup currentGroup;
    private String         currentObjectName;
    private ChunkTester chunkTester = new ChunkTester();
    private Chunk mainChunk = new Chunk("MainChunk");
    private ChunkMap chunkMap = new ChunkMap(mainChunk);

    private KeyFramer keyFramer = new KeyFramer();

    /** This should be turned on by Loader3DS to view debugging information. */
    public static boolean debug;

    /** Current chunk for which debugging info is viewed if debug == true */
    public static Chunk debugChunk;

    /**
     * private singleton constructor.
     */
    public ChunkChopper(){}

    /**
     * This sequentially parses the chunks out of the input stream and
     * constructs the 3D entities represented within.
     * A Chunk is a little endian data structure consists of a 
     * 6 byte header followed by subchunks and or data.  
     * The first short int(little endian) represent the id 
     * of the chunk.  The next int represent the total 
     * length of the chunk(total of data, subchunks and chunk header).
     * <p> 
     * The first chunk is the main chunk (id=4D4D) and its length
     * is always the length of the file. It only contains sub chunks.
     * Other chunks may contain data, subchunks or both.  If the format
     * of a chunk is unknown skipped.
     * <p>
     * Subclasses of chunk will all automagically load the subchunks.  
     * It is the programmers responsibility to ensure that the data 
     * preceeding the subchunks is loaded or skipped as 
     * required and that something useful is done with the data.  If data from the
     * subchunks is needed in order to initialize components then that code should be
     * placed in {@link Chunk#initialize}.  Otherwise the data may be dealt with in
     * {@link Chunk#loadData}.  Also, if a chunk has data preceeding its subchunks
     * it communicates how many bytes long that data is by returning it from loadData.
     * <p>
     * This chopper reads a file in order from beginning to end
     * @param inputStream the stream with the data to be parsed.
     * @param loader the loader that will be configured from the data.
     * @param modelName name of the model file for display purposes.
     * @param modelSize size in bytes of the file to read.
     */
    public synchronized SceneBase loadSceneBase(InputStream inputStream, Loader3DS loader, int modelSize)
    {
        this.loader = loader;
        this.sceneGroup = new BranchGroup();
        this.base = new SceneBase();
        this.dataMap = new HashMap();
        base.setSceneGroup(sceneGroup);

        //FileChannel channel = null; 
        ReadableByteChannel channel = null; 
        try {
            channel = Channels.newChannel(inputStream);
            chunkBuffer = getByteBuffer(channel, modelSize);
            //chunkBuffer = getDirectByteBuffer(channel, modelSize);

            int mainChunkID     = chunkBuffer.getShort();
            long mainChunkLength = chunkBuffer.getInt();

            long begin = System.currentTimeMillis();     
            logger.finest("\n\n\n STARTING SUBCUNKS " + (mainChunkLength - 61));
            try {
                loadSubChunks(mainChunk, 0);
            }
            catch(CannotChopException e){
                
            }
            logger.finest("FINISHED WITH THE SUBCHUNKS");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            try {
                if(channel != null) {
                    channel.close();
                }
            } catch (Exception e){
                //Just closing file.. don't care.
            }
        }
        return base;
    }

    /**
     * Allocates and loads a byte buffer from the channel
     * @param channel the file channel to load the data from
     * @return a direct byte buffer containing all the data of the channel at position 0 
     */
    public ByteBuffer getByteBuffer(ReadableByteChannel channel, int channelSize) throws IOException
    {
        ByteBuffer chunkBuffer = ByteBuffer.allocate(channelSize);
        chunkBuffer.order(ByteOrder.LITTLE_ENDIAN);
        channel.read(chunkBuffer);
        chunkBuffer.position(0);
        return chunkBuffer;
    }


    /**
     * The base class Chunk takes care of loading subchunks for
     * all chunks types.  It occurs as follows:
     * <ol>
     *     <li>The chunk id (short) is read
     *     <li>The chunk length(int) is read
     *     <li>A subchunk is looked up from the map of publish 
     *         subchunk types of the current chunk.
     *     <li>If it isn't found during the lookup it is skipped.
     *     <li>Otherwise it is requested to {@link #pushData} 
     *     <li>The return value, if there is one, is used to determine 
     *         where its next subchunk is. A return value of 0 signifies
     *         that the next subchunk is nigh.
     *     <li>The chunk's subchunks are then loaded.
     *     <li>The chunks initialize method is called.
     * </ol>
     */
    protected void loadSubChunks(Chunk parentChunk, int level) throws CannotChopException
    {
        level++;
        while(chunkBuffer.hasRemaining())//hasRemaining() evaluates limit - position.
        {
            chunkID         = new Integer(chunkBuffer.getShort());
            Chunk chunk     = parentChunk.getSubChunk(chunkID);

            int currentChunkLength     = chunkBuffer.getInt() - 6;  //length includes this 6 byte header.
            int finishedPosition       = chunkBuffer.position() + currentChunkLength;
            int previousLimit          = chunkBuffer.limit();
            chunkBuffer.limit(chunkBuffer.position() + currentChunkLength);

            if(debug) {
                debug(parentChunk, level, chunkID, currentChunkLength, chunkBuffer.position(), chunkBuffer.limit());
            }
            if(chunk != null && currentChunkLength != 0) {
                try {
                    chunk.loadData(this);
                }
                catch(BufferUnderflowException e){
                    chunkBuffer.position(finishedPosition);
                    chunkBuffer.limit(previousLimit);
                    throw new CannotChopException(" tried to read too much data from the buffer. Trying to recover.", e);
                }
                try {
                    if(chunkBuffer.hasRemaining()) {
                        loadSubChunks(chunk, level);
                    }
                    chunk.initialize(this);
                }
                catch(CannotChopException e){
                    logger.log(Level.SEVERE, chunk.toString() + "Trying to continue");
                }
            } 

            chunkBuffer.position(finishedPosition);
            chunkBuffer.limit(previousLimit);
        }
    }

    /**
     * Gets the key framer chunk
     * These should be their own objects instead of chunks.
     */
    public KeyFramer getKeyFramer()
    {
        return keyFramer;
    }

    /**
     * Adds a group to the choppers scene group
     * and sets the current name and group.
     * @param the name of the object to add which
     * will also be the current name of the object
     * the chopper is working with.
     * @param group the current group that the chopper
     * will be adding things too.
     */
    public void addObject(String name, TransformGroup group)
    {
        sceneGroup.addChild(group);
        currentGroup = group;
        currentObjectName = name;
        base.addNamedObject(name, group);
    }



    /**
     * Gets the name of the current object
     * the chopper is working with.  The value returned
     * from this generally is either set by a NamedObjectChunk 
     * and is the name of the name of the last object added
     * or is the name set by setObjectName.
     * @return the name of the current object being 
     * constructed.
     */
    public String getObjectName()
    {
        return currentObjectName;
    }


    /**
     * Sets the name of the current object.
     * The name of the current object can also be set
     * with {@link #addObject}
     * @param name the name that the current object should be set to.
     */
    public void setObjectName(String name)
    {
        currentObjectName = name;
    }

    /**
     * Gets the group for the current object 
     * the chopper is working with.  The value returned
     * from this generally gets set by a NamedObjectChunk 
     * and is the name of the last object added.
     * @return the group for the current object being 
     * constructed.
     */
    public TransformGroup getGroup()
    {
        return currentGroup;
    }

    /**
     * Used to store data that may later be used
     * by another chunk.
     * @param key the look up key.
     * @param data the data to store.
     */
    public void pushData(Object key, Object data)
    {
        dataMap.put(key, data);
    }

    /**
     * Gets a datum that had been retrieved and stored
     * via {@link #pushData} earlier and removes it.
     * @param key the key used to store the datum earlier.
     */
    public Object popData(Object key)
    {
        Object retVal = dataMap.remove(key);
        return retVal;
    }

    /**
     * Sets a named object in the loader.
     * @param key the key name of the object
     * @param value the named Object.
     */
    public void setNamedObject(String key, Object value)
    {
        base.addNamedObject(key, value);
    }

    /**
     * Returns true if there have been lights loaded.
     * @return true if there are lights.
     */
    public boolean hasLights()
    {
        return (base.getLightNodes() != null && base.getLightNodes().length > 0);
    }

    /**
     * Adds a behavior to the scene base.
     * @param behavior the behavior to add to the scene base.
     */
    public void addBehaviorNode(Behavior behavior)
    {
        base.addBehaviorNode(behavior);
    }


    /**
     * Adds a light to the scene base.
     * @param light the light to add to the scene base.
     */
    public void addLightNode(Light light)
    {
        base.addLightNode(light);
    }


    /**
     * Adds a camera transform to the scene base.
     * @param viewGroup the transform group to add as a view.
     */
    public void addViewGroup(TransformGroup viewGroup)
    {
        base.addViewGroup(viewGroup);
    }

    /**
     * Sets a named Object in the loader.
     * @param key the key used as the name for which the object will be returned
     */
    public Object getNamedObject(String key)
    {
        if(key == null)
            return null;
        return base.getNamedObjects().get(key);
    }

    /**
     * Gets and cast the named object for the
     * key provided.  Its an error if its not
     * a transform group.
     */
    public TransformGroup getNamedTransformGroup(String key)
    {
        Object object = getNamedObject(key);
        if(object instanceof TransformGroup)
        {
            return (TransformGroup)object;
        }
        else if (object != null)
        {
            logger.log(Level.INFO, "Retrieving " + key + " which is a named object but not useable because "+
                               " its not a transform group. Its a " + object.getClass().getName());
        }
        return null;
    }


    /**
     * Gets a long from the chunk Buffer
     */
    public long getLong()
    {
        return chunkBuffer.getLong();
    }

    /**
     * Reads a short and returns it as a signed
     * int.
     */
    public int getShort()
    {
        return chunkBuffer.getShort();
    }

    /**
     * Reads a short and returns it as an unsigned
     * int.
     */
    public int getUnsignedShort()
    {
        return chunkBuffer.getShort()&0xFFFF;
    }

    /**
     * reads a float from the chunkBuffer.
     */
    public float getFloat()
    {
        return chunkBuffer.getFloat();
    }

    /**
     * Reads 3 floats x,z,y from the chunkbuffer.
     * Since 3ds has z as up and y as pointing in whereas
     * java3d has z as pointing forward and y as pointing up;
     * this returns new Vector3f(x,-z,y)
     *
     */
    public Vector3f getVector()
    {
        return new Vector3f(getPoint());
    }
    /**
     * Reads 3 floats x,z,y from the chunkbuffer.
     * Since 3ds has z as up and y as pointing in whereas
     * java3d has z as pointing forward and y as pointing up;
     * this returns new Point3f(x,-z,y)
     */
    public Point3f getPoint()
    {
        float x = chunkBuffer.getFloat();
        float z = -chunkBuffer.getFloat();
        float y = chunkBuffer.getFloat();
        return new Point3f(x,y,z);
    }

    /**
     * Reads an int and returns it 
     * @return the int read
     */
    public int getInt()
    {
        return chunkBuffer.getInt();
    }

    /**
     * Reads an int and returns it 
     * unsigned, any ints greater than MAX_INT
     * will break.
     */
    public int getUnsignedInt()
    {
        return chunkBuffer.getInt()&0xFFFFFFFF;
    }

    /**
     * Reads a byte, unsigns it, returns the corresponding int.
     * @return the unsigned int corresponding to the read byte.
     */
    public int getUnsignedByte()
    {
        return chunkBuffer.get()&0xFF;
    }

    /**
     * Reads a number of bytes corresponding to the
     * number of bytes left in the current chunk and returns an array
     * containing them.
     * @return an array containing all the bytes for the current chunk. 
     */
    public byte[] getChunkBytes()
    {
        byte[] retVal = new byte[chunkBuffer.limit() - chunkBuffer.position()];
        get(retVal);
        return retVal;
    }

    /**
     * Fills bytes with data from the chunk buffer.
     * @param bytes the array to fill with data.
     */
    public void get(byte[] bytes)
    {
        chunkBuffer.get(bytes);
    }


    /**
     * Sets the data map used to store values
     * that chunks may need to retrieve later.
     * @param dataMap the hashmap that will be used to store
     * and retrieve values for use by chunks.
     */
    public void setDataMap(HashMap dataMap)
    {
        this.dataMap = dataMap;
    }


    /**
     * This reads bytes until it gets 0x00 and returns
     * the corresponding string.
     */
    public String getString()
    {
        StringBuffer stringBuffer = new StringBuffer();
        char charIn = (char)chunkBuffer.get();
        while(charIn != 0x00) 
        {
            stringBuffer.append(charIn);
            charIn = (char)chunkBuffer.get();
        }
        return stringBuffer.toString();
    }

    /**
     * Gets the id of the current chunk.
     * @return id of the current chunk as read
     * from the chunkBuffer.  It will be a signed <code>short</code>.
     */
    public Integer getID()
    {
        return chunkID;
    }

    /**
     * Loads the image to server as a texture.
     * @param textureImageName name of the image that 
     * is going to be set to be the texture.
     */
    public Texture createTexture(String textureImageName)
    {
        Image image = loader.getTextureImage(textureImageName);
        if(image == null)
        {
            System.err.println("Cannot load texture image " + textureImageName +
                               ". Make sure it is in the directory with the model file. " +
                               "If its a bmp make sure JAI is installed.");
            return null;
        }
        try 
        {
            TextureLoader textureLoader = new TextureLoader(image, null);
            return textureLoader.getTexture(); 
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * prints some handy information... the chunk hierarchy.
     */
    protected void debug(Chunk parentChunk, int level, Integer chunkID, long chunkLength, int position, long limit)
    {
        try {
       for(int i=0; i<level; i++)
       {
           System.out.print("  ");
       }
       Object child = parentChunk.getSubChunk(chunkID);
       int id = ((short)chunkID.intValue()) & 0xFFFF;
       System.out.println(parentChunk + " is " +
                         (child==null?"skipping":"LOADING")+
                         ": [id=" + Integer.toHexString(id) + 
                         ", object= <" + parentChunk.getSubChunk(chunkID) +
                         ">, chunkLength=" + chunkLength + 
                         ", position=" + position + 
                         " limit=" + limit + "]");
        }
        catch(Exception e){
            //We're debugging.. its ok
            e.printStackTrace();
        }
    }

    /**
     * Prints an exception and exits.
     */
    private void exceptAndExit(Throwable exception)
    {
        logger.log(Level.SEVERE, "\nThe chunk for loadData method read too much or not enough data from the stream." +
                            " It needs be skipped or adjusted to read more or less data.");
        exception.printStackTrace();
        System.exit(3);
    }

    /**
     * Convert the integer to an unsigned number.
     * @param i the integer to convert.
     */
    private static String byteString(int i) 
    {
        final char[] digits = {
	    '0' , '1' , '2' , '3' , '4' , '5' ,
	    '6' , '7' , '8' , '9' , 'a' , 'b' ,
	    'c' , 'd' , 'e' , 'f' };

        char[] buf = new char[2];
        buf[1] = digits[i & 0xF];
        i >>>= 4;
        buf[0] = digits[i & 0xF];

        return "0x" + new String(buf).toUpperCase();
    }
}
