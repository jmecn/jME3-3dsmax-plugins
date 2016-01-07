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
 * Contact Josh DeFord jdeford@microcrowd.com
 */
package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;

/**
 * FacesMaterialsChunk contains the materials information 
 * from the 3ds file. It contains information pertaining to
 * which faces of a mesh have materials on them and the
 * texture coordinates for texture mapping.
 * Right now, its just putting the name of the material
 * that needs to be applied to the mesh under construction.
 *
 * @author jdeford
 */
public class FacesMaterialChunk extends Chunk
{
    /**
     * Loads the texture coordinates for a mesh, 
     *
     * @param chopper the ChunkChopper containing the state of the parser.  
     */
    public void loadData(ChunkChopper chopper)
    {
        final String materialName = chopper.getString();
        int numFaces = chopper.getUnsignedShort();
        if (numFaces > 0) 
        {
            for (int j = 0; j < numFaces; j++) 
            {
                int index = j * 3;
                int position = chopper.getUnsignedShort() * 3;
            }
        }

        chopper.pushData(chopper.getID(), materialName);
    }
}

