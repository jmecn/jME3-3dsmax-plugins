/**
 * Make a donation http://sourceforge.net/donate/index.php?group_id=98797
 *
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * General purpose implementation of TextureImageLoader.
 * Gets the base path from loader and prepends it to the
 * file name to load an image.
 */
public class DefaultTextureImageLoader implements TextureImageLoader
{

    private Loader3DS loader;

    /**
     * Constructs an image loader that will resolve image
     * locations to the base path of the loader provided.
     * @param the loader that will specify the base path
     * used to retrieve images.
     */
    public DefaultTextureImageLoader(Loader3DS loader)
    {
        this.loader = loader;
    }


    /**
     * Gets the image to be loaded as a texture.
     * @param imageName the name of the image to load.
     * @return image to be used.
     */
    public Image getTextureImage(String imageName)
    {
        File file = null;
        InputStream in = null;
        if(loader.fromUrl())
        {
            try 
            {
                in = new URL(loader.getBaseUrl() + imageName).openStream();
                return ImageIO.read(in);
            }
            catch (FileNotFoundException e)
            {
                throw new IllegalArgumentException(" Can't load texture: " + imageName + 
                        " Make sure it is located in the " +
                        " same server and directory with the model file."+
                        " the loader's base path is: " + loader.getBaseUrl());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new IllegalArgumentException(" Can't load texture: " + imageName + 
                        " Make sure it is located in the " +
                        " same server and directory with the model file."+
                        " the loader's base path is: " + loader.getBaseUrl());
            }
        }
        else
        {
            try 
            {
                in = new FileInputStream(new File(loader.getBasePath() + imageName));
                return ImageIO.read(in);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new IllegalArgumentException(" Can't load texture: " + imageName + 
                        " Make sure it is located in the " +
                        " same server and directory with the model file."+
                        " the loader's base path is: " + loader.getBasePath());
            }
        }
    }
}
