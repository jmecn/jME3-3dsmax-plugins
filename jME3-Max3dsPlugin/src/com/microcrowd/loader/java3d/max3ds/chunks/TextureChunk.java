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

package com.microcrowd.loader.java3d.max3ds.chunks;


import com.microcrowd.loader.java3d.max3ds.ChunkChopper;
import com.microcrowd.loader.java3d.max3ds.ChunkMap;


/**
 * Loads percentage values from binary data representing them.
 */
public class TextureChunk extends Chunk
{

    /**
     * Gets the current texture image from the chopper
     * creates a texture with it and sets that texture
     * on the chopper.
     *
     * @param chopper  the parser containing the state of parsing
     */
    public void initialize(ChunkChopper chopper)
    {
        String textureName = (String)chopper.popData(ChunkMap.TEXTURE_NAME);
        chopper.pushData(ChunkMap.TEXTURE, chopper.createTexture(textureName));
    }
}
