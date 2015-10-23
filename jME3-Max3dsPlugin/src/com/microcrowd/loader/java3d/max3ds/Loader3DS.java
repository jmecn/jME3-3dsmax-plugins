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
 * Contact Josh DeFord jdeford@microcrowd.com
 */

package com.microcrowd.loader.java3d.max3ds;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Group;
import javax.media.j3d.PointLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import com.sun.j3d.loaders.LoaderBase;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.SceneBase;

/**
 * Used to load a 3ds studio max file. This will sequentially read a 3ds file,
 * load or skip chunks and subchunks and initialize the data for the chunks.
 * A {@link ChunkChopper} is a singleton flyweight factory responsible for
 * chopping the data up and sending it to the  corresponding chunks(which are
 * flyweights ala the flyweight pattern) for processing.
 * 
 * <p>
 * Features not supported; unknown chunks are skipped.
 * </p>
 */
public class Loader3DS extends LoaderBase
{
    private boolean dataMapInitialized;

    private TextureImageLoader textureImageLoader;
    private boolean fromUrl;

    public Loader3DS()
    {
        //turnOnDebug();
    }

    /**
     * Setting this will initialize a lot of debugging code that has lots of
     * overhead.
     */
    private boolean debugMode;

    /**
     * This is not supported
     *
     * @param reader loads a model from a reader 
     *
     * @return nothing, this isn't implemented. 
     *
     * @throws FileNotFoundException 
     * @throws UnsupportedOperationException 
     */
    public Scene load(Reader reader) throws FileNotFoundException
    {
        throw new UnsupportedOperationException("Not supported for 3DS");
    }

    /**
     * Loads the model by parsing the file, modelPath and  creating a 3D Scene.
     *
     * @param modelPath the path of the 3ds file.
     *
     * @return a loaded scene 
     *
     * @throws FileNotFoundException if the file can't be located. 
     */
    public Scene load(String modelPath) throws FileNotFoundException
    {
        InputStream fileIn = null;
        setBasePathFromFilename(modelPath);

        try {
            File modelFile = getFile(modelPath);
            fileIn = new FileInputStream(modelFile);
            return parseChunks(fileIn, (int)modelFile.length());
        } finally {
            try {
                fileIn.close();
            } catch (Exception e) {
                e.printStackTrace();
                //Don't care about exceptions at this point.
            }
        }
    }

    private void setBaseUrlFromUrl(URL url) throws FileNotFoundException
    {
        String u = url.toString();
        String s;
        if (u.lastIndexOf('/') == -1) {
            s = url.getProtocol() + ":";
        } else {
            s = u.substring(0, u.lastIndexOf('/') + 1);
        }
        try {
            setBaseUrl(new URL(s));
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    /*
     * Takes a file name and sets the base path to the directory
     * containing that file.
     */
    private void setBasePathFromFilename(String fileName)
    {
        if (fileName.lastIndexOf(java.io.File.separator) == -1) {
            // No path given - current directory
            setBasePath("." + java.io.File.separator);
        } else {
            setBasePath(fileName.substring(0, fileName.lastIndexOf(java.io.File.separator)));
        }
    }

    /**
     * Set the path where files associated with this .obj file are
     * located.
     * Only needs to be called to set it to a different directory
     * from that containing the .obj file.
     */
    public void setBasePath(String pathName)
    {
      String basePath = pathName;
      if (basePath == null || basePath == "")
	  basePath = "." + java.io.File.separator;
      basePath = basePath.replace('/', java.io.File.separatorChar);
      basePath = basePath.replace('\\', java.io.File.separatorChar);
      if (!basePath.endsWith(java.io.File.separator))
	  basePath = basePath + java.io.File.separator;
      super.setBasePath(basePath);
    }

    /**
     * Returns true if this loader is loading files
     * from a url.
     */
    public boolean fromUrl()
    {
        return fromUrl;
    }

    /**
     * gets an image with the specified name.
     * This uses a DefaultTextureImageLoader
     * to load the image if one hasn't been set for
     * this loader.
     * @param imageName name of image to load.
     * @return image corresponding to imageName
     */
    public Image getTextureImage(String imageName)
    {
        try {
            if(textureImageLoader == null)
            {
                textureImageLoader = new DefaultTextureImageLoader(this);
            }
            return textureImageLoader.getTextureImage(imageName);
        } 
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the TextureImageLoader to be used
     * when texture images are needed.
     * @param loader the TextureImageLoader that will be used to load images.
     */
    public void setTextureImageLoader(TextureImageLoader loader)
    {
        textureImageLoader = loader;
    }

    /**
     * Gets a chunk chopper to do all the dirty work.
     *
     * @param inputStream the stream containing the model.
     * @param modelSize size of the model file. 
     *
     * @return a java3d scene built from input. 
     */
    protected Scene parseChunks(InputStream inputStream, int modelSize)
    {
        ChunkChopper chopper = new ChunkChopper();
        SceneBase base = chopper.loadSceneBase(inputStream, this, modelSize);
        if(!chopper.hasLights())
        {
            addDefaultLights(base.getSceneGroup());
            addDefaultLights(chopper.getGroup());
        }
        return base;
    }

    /**
     * Adds defaultlights to the group provided 
     * similar to the ones 3ds max adds when there are none in the scene.
     * @param group to add the lighting to.
     */
    public static void addDefaultLights(Group group)
    {
        PointLight light1 = new PointLight();
        PointLight light2 = new PointLight();
        light1.setInfluencingBounds(new BoundingSphere(new Point3d(0,0,0), 3000));
        light2.setInfluencingBounds(new BoundingSphere(new Point3d(0,0,0), 3000));

        Transform3D t1 = new Transform3D(new float[]{1.0f, 0.0f, 0.0f, -900f, 
            0.0f, 1.0f, 0.0f, 1500f, 
            0.0f, 0.0f, 1.0f, 1000f, 
            0.0f, 0.0f, 0.0f, 1.0f});
        Transform3D t2 = new Transform3D(new float[]{1.0f, 0.0f, 0.0f, 900f, 
            0.0f, 1.0f, 0.0f, -1500f, 
            0.0f, 0.0f, 1.0f, -1000f, 
            0.0f, 0.0f, 0.0f, 1.0f});
        TransformGroup group1 = new TransformGroup(t1);
        TransformGroup group2 = new TransformGroup(t2);
        group1.addChild(light1);
        group2.addChild(light2);
        group.addChild(group1);
        group.addChild(group2);
    }

    /**
     * Retrieves a file with a given name. 
     *
     * @param fileName name of file to retrieve. 
     *
     * @return retrieved file. 
     */
    private File getFile(String fileName)
    {
        File file = null;
        try {
            file = new File(fileName);
            if (!file.exists()) {
                throw new IOException(fileName + " doesn't exist");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return file;
    }

    /**
     * throws UnsupportedOperationException
     *
     * @param url url of model to be loaded. 
     *
     * @return a java3d scene represented in url 
     *
     * @throws FileNotFoundException if file couldn't be found. 
     */
    public Scene load(URL url) throws FileNotFoundException
    {
        fromUrl = true;
        try {
            URLConnection connection = url.openConnection();
            if (baseUrl == null) 
                setBaseUrlFromUrl(url);

            return parseChunks(connection.getInputStream(), connection.getContentLength());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Url " + url + " cannot be loaded");

        }
    }

    /**
     * Turn on debug mode for all 3ds xml.
     */
    public void turnOnDebug()
    {
        if (!debugMode) {
            ChunkChopper.debug = true;
            debugMode = true;
        }
    }
}
