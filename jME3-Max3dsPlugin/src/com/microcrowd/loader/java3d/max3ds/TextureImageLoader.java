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

/**
 * Interface specifying a lookup for images to be loaded
 * in textures.  Implementations using this loader may need
 * to use different methods to resolve images.  In these
 * cases a class should be created that implements
 * this interface. For general purpose applications
 * DefaultTextureImageLoader should be used.
 */
public interface TextureImageLoader
{

    /**
     * Gets the image to be loaded as a texture.
     * @param imageName the name of the image with which it will be
     * looked up.
     */
    public Image getTextureImage(String imageaName) throws Exception;
}
