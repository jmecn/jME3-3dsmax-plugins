/**
 * 
 */
package com.jme3.asset.max3ds;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.max3ds.chunks.Chunk;
import com.jme3.asset.max3ds.data.KeyFramer;
import com.jme3.light.Light;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/**
 * @author yanmaoyuan
 *
 */
public class M3DLoader implements AssetLoader {
	private AssetManager manager = null;
	private AssetKey<?> key = null;
	
	private Logger logger = Logger.getLogger(M3DLoader.class.getName());

	private Node rootNode;
	
	private HashMap<Object, Object> dataMap;
	private ByteBuffer chunkBuffer;
	private Integer chunkID;
	
	private Node currentNode;
	private String currentObjectName;
	private Chunk mainChunk = new Chunk("MainChunk");
	private ChunkMap chunkMap = new ChunkMap(mainChunk);
	
	private KeyFramer keyFramer = new KeyFramer();

	/** This should be turned on by Loader3DS to view debugging information. */
	public static boolean debug;

	/** Current chunk for which debugging info is viewed if debug == true */
	public static Chunk debugChunk;
	
	/* (non-Javadoc)
	 * @see com.jme3.asset.AssetLoader#load(com.jme3.asset.AssetInfo)
	 */
	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		key = assetInfo.getKey();
		manager = assetInfo.getManager();
		
		this.rootNode = new Node();
		
		// FileChannel channel = null;
		ReadableByteChannel channel = null;
		channel = Channels.newChannel(assetInfo.openStream());
		// Read main chunk
		ByteBuffer mainChunkBuffer = ByteBuffer.allocate(6);
		mainChunkBuffer.order(ByteOrder.LITTLE_ENDIAN);
		channel.read(mainChunkBuffer);
		mainChunkBuffer.flip();

		int mainChunkID = mainChunkBuffer.getShort();
		int mainChunkLength = mainChunkBuffer.getInt();

		// confirm that it's a 3ds
		assert mainChunkID == 0x4D4D;

		// Allocates and loads a byte buffer from the channel
		chunkBuffer = ByteBuffer.allocate(mainChunkLength);
		chunkBuffer.order(ByteOrder.LITTLE_ENDIAN);
		chunkBuffer.put(mainChunkBuffer);
		channel.read(chunkBuffer);

		// Ready
		chunkBuffer.flip();
		
		long begin = System.currentTimeMillis();
		logger.finest("\n\n\n STARTING SUBCUNKS " + (mainChunkLength - 61));
		try {
			loadSubChunks(mainChunk, 0);
		} catch (CannotChopException e) {

		}
		System.out.println(System.currentTimeMillis() - begin);
		logger.finest("FINISHED WITH THE SUBCHUNKS");
		
		// close channel
		channel.close();
		
		return rootNode;
	}

	/**
	 * The base class Chunk takes care of loading subchunks for all chunks
	 * types. It occurs as follows:
	 * <ol>
	 * <li>The chunk id (short) is read
	 * <li>The chunk length(int) is read
	 * <li>A subchunk is looked up from the map of publish subchunk types of the
	 * current chunk.
	 * <li>If it isn't found during the lookup it is skipped.
	 * <li>Otherwise it is requested to {@link #pushData}
	 * <li>The return value, if there is one, is used to determine where its
	 * next subchunk is. A return value of 0 signifies that the next subchunk is
	 * nigh.
	 * <li>The chunk's subchunks are then loaded.
	 * <li>The chunks initialize method is called.
	 * </ol>
	 */
	protected void loadSubChunks(Chunk parentChunk, int level)
			throws CannotChopException {
		level++;
		while (chunkBuffer.hasRemaining())// hasRemaining() evaluates limit -
											// position.
		{
			chunkID = new Integer(chunkBuffer.getShort());
			Chunk chunk = parentChunk.getSubChunk(chunkID);

			int currentChunkLength = chunkBuffer.getInt() - 6; // length
																// includes this
																// 6 byte
																// header.
			int finishedPosition = chunkBuffer.position() + currentChunkLength;
			int previousLimit = chunkBuffer.limit();
			chunkBuffer.limit(chunkBuffer.position() + currentChunkLength);

			if (debug) {
				Debug.debug(parentChunk, level, chunkID, currentChunkLength,
						chunkBuffer.position(), chunkBuffer.limit());
			}
			if (chunk != null && currentChunkLength != 0) {
				try {
					chunk.loadData(this);
				} catch (BufferUnderflowException e) {
					chunkBuffer.position(finishedPosition);
					chunkBuffer.limit(previousLimit);
					throw new CannotChopException(
							" tried to read too much data from the buffer. Trying to recover.",
							e);
				}
				try {
					if (chunkBuffer.hasRemaining()) {
						loadSubChunks(chunk, level);
					}
					chunk.initialize(this);
				} catch (CannotChopException e) {
					logger.log(Level.SEVERE, chunk.toString()
							+ "Trying to continue");
				}
			}

			chunkBuffer.position(finishedPosition);
			chunkBuffer.limit(previousLimit);
		}
	}
	
	/**
	 * Gets the key framer chunk These should be their own objects instead of
	 * chunks.
	 */
	public KeyFramer getKeyFramer() {
		return keyFramer;
	}
	
	/**
	 * Adds a group to the choppers scene group and sets the current name and
	 * group.
	 * 
	 * @param the
	 *            name of the object to add which will also be the current name
	 *            of the object the chopper is working with.
	 * @param group
	 *            the current group that the chopper will be adding things too.
	 */
	public void attachNode(Node node) {
		rootNode.attachChild(node);
		currentNode = node;
		currentObjectName = node.getName();
	}

	/**
	 * Gets the name of the current object the chopper is working with. The
	 * value returned from this generally is either set by a NamedObjectChunk
	 * and is the name of the name of the last object added or is the name set
	 * by setObjectName.
	 * 
	 * @return the name of the current object being constructed.
	 */
	public String getObjectName() {
		return currentObjectName;
	}

	/**
	 * Sets the name of the current object. The name of the current object can
	 * also be set with {@link #addObject}
	 * 
	 * @param name
	 *            the name that the current object should be set to.
	 */
	public void setObjectName(String name) {
		currentObjectName = name;
	}

	/**
	 * Gets the group for the current object the chopper is working with. The
	 * value returned from this generally gets set by a NamedObjectChunk and is
	 * the name of the last object added.
	 * 
	 * @return the group for the current object being constructed.
	 */
	public Node getGroup() {
		return currentNode;
	}

	/**
	 * Used to store data that may later be used by another chunk.
	 * 
	 * @param key
	 *            the look up key.
	 * @param data
	 *            the data to store.
	 */
	public void pushData(Object key, Object data) {
		dataMap.put(key, data);
	}

	/**
	 * Gets a datum that had been retrieved and stored via {@link #pushData}
	 * earlier and removes it.
	 * 
	 * @param key
	 *            the key used to store the datum earlier.
	 */
	public Object popData(Object key) {
		Object retVal = dataMap.remove(key);
		return retVal;
	}

	/**
	 * Sets a named object in the loader.
	 * 
	 * @param key
	 *            the key name of the object
	 * @param value
	 *            the named Object.
	 */
	private void setNamedObject(String key, Object value) {
		rootNode.setUserData(key, value);
	}
	
	/**
	 * Returns true if there have been lights loaded.
	 * 
	 * @return true if there are lights.
	 */
	public boolean hasLights() {
		// TODO 是否还要检查rootNode的子节点有没有灯光？
		return rootNode.getLocalLightList().size() > 0;
	}
	
	/**
	 * Adds a behavior to the scene base.
	 * 
	 * @param behavior
	 *            the behavior to add to the scene base.
	 */
//	private void addBehaviorNode(Behavior behavior) {
		// 不知道在JME3中这个有什么用
		// 也许是InputManager
//		base.addBehaviorNode(behavior);
//	}

	/**
	 * Adds a light to the scene.
	 * 
	 * @param light
	 *            the light to add to the scene.
	 */
	public void addLight(Light light) {
		rootNode.addLight(light);
	}
	
	/**
	 * Adds a camera transform to the scene base.
	 * 
	 * @param viewGroup
	 *            the transform group to add as a view.
	 */
	private void addViewGroup(Node viewGroup) {
		// jME3应该不需要3DS文件中的摄像机吧？
//		base.addViewGroup(viewGroup);
	}

	/**
	 * Sets a named Object in the loader.
	 * 
	 * @param key
	 *            the key used as the name for which the object will be returned
	 */
	public Object getNamedObject(String key) {
		if (key == null)
			return null;
		// TODO 是否应该getChild(key)?
		return rootNode.getUserData(key);
	}

	/**
	 * Gets and cast the named object for the key provided. Its an error if its
	 * not a transform group.
	 */
	public Node getNamedTransformGroup(String key) {
		Object object = getNamedObject(key);
		if (object instanceof Node) {
			return (Node) object;
		} else if (object != null) {
			logger.log(Level.INFO, "Retrieving " + key
					+ " which is a named object but not useable because "
					+ " its not a transform group. Its a "
					+ object.getClass().getName());
		}
		return null;
	}
	
	/**
	 * Gets a long from the chunk Buffer
	 */
	public long getLong() {
		return chunkBuffer.getLong();
	}

	/**
	 * Reads a short and returns it as a signed int.
	 */
	public int getShort() {
		return chunkBuffer.getShort();
	}

	/**
	 * Reads a short and returns it as an unsigned int.
	 */
	public int getUnsignedShort() {
		return chunkBuffer.getShort() & 0xFFFF;
	}

	/**
	 * reads a float from the chunkBuffer.
	 */
	public float getFloat() {
		return chunkBuffer.getFloat();
	}

	/**
	 * Reads 3 floats x,z,y from the chunkbuffer. Since 3ds has z as up and y as
	 * pointing in whereas java3d has z as pointing forward and y as pointing
	 * up; this returns new Point3f(x,-z,y)
	 */
	public Vector3f getVector3f() {
		float x = chunkBuffer.getFloat();
		float z = -chunkBuffer.getFloat();
		float y = chunkBuffer.getFloat();
		return new Vector3f(x, y, z);
	}

	/**
	 * Reads an int and returns it
	 * 
	 * @return the int read
	 */
	public int getInt() {
		return chunkBuffer.getInt();
	}

	/**
	 * Reads an int and returns it unsigned, any ints greater than MAX_INT will
	 * break.
	 */
	public int getUnsignedInt() {
		return chunkBuffer.getInt() & 0xFFFFFFFF;
	}

	/**
	 * Reads a byte, unsigns it, returns the corresponding int.
	 * 
	 * @return the unsigned int corresponding to the read byte.
	 */
	public int getUnsignedByte() {
		return chunkBuffer.get() & 0xFF;
	}

	/**
	 * Reads a number of bytes corresponding to the number of bytes left in the
	 * current chunk and returns an array containing them.
	 * 
	 * @return an array containing all the bytes for the current chunk.
	 */
	public byte[] getChunkBytes() {
		byte[] retVal = new byte[chunkBuffer.limit() - chunkBuffer.position()];
		get(retVal);
		return retVal;
	}

	/**
	 * Fills bytes with data from the chunk buffer.
	 * 
	 * @param bytes
	 *            the array to fill with data.
	 */
	public void get(byte[] bytes) {
		chunkBuffer.get(bytes);
	}

	/**
	 * Sets the data map used to store values that chunks may need to retrieve
	 * later.
	 * 
	 * @param dataMap
	 *            the hashmap that will be used to store and retrieve values for
	 *            use by chunks.
	 */
	public void setDataMap(HashMap dataMap) {
		this.dataMap = dataMap;
	}

	/**
	 * This reads bytes until it gets 0x00 and returns the corresponding string.
	 */
	public String getString() {
		StringBuffer stringBuffer = new StringBuffer();
		char charIn = (char) chunkBuffer.get();
		while (charIn != 0x00) {
			stringBuffer.append(charIn);
			charIn = (char) chunkBuffer.get();
		}
		return stringBuffer.toString();
	}

	/**
	 * Gets the id of the current chunk.
	 * 
	 * @return id of the current chunk as read from the chunkBuffer. It will be
	 *         a signed <code>short</code>.
	 */
	public Integer getID() {
		return chunkID;
	}
	
	/**
	 * Loads the image to server as a texture.
	 * 
	 * @param textureImageName
	 *            name of the image that is going to be set to be the texture.
	 */
	public Texture createTexture(String textureImageName) {
		Texture texture = null;
		try {
			System.out.println(key.getFolder() + textureImageName);
			texture = manager.loadTexture(key.getFolder() + textureImageName);
			texture.setWrap(WrapMode.Repeat);
		} catch (Exception ex) {

			System.err
					.println("Cannot load texture image "
							+ textureImageName
							+ ". Make sure it is in the directory with the model file. "
							+ "If its a bmp make sure JAI is installed.");

			texture = manager.loadTexture("Common/Textures/MissingTexture.png");
			texture.setWrap(WrapMode.Clamp);
		}
		return texture;
	}
}
