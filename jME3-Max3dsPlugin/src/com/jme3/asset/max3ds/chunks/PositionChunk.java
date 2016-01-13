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

package com.jme3.asset.max3ds.chunks;

import java.util.ArrayList;




import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.math.Vector3f;

/**
 * Loads the position of a mesh as defined in the 3ds file.
 * This position may need to be converted to another coordinate
 * system by KeyFramerInfoChunk.
 *  * {@see KeyFramerInfoChunk} for more information about using
 * animations from a 3ds file
 */
public class PositionChunk extends Chunk
{
    /**
     * Loads the position for a shape and KeyFramerInfoChunk
     *
     * @param chopper the ChunkChopper containing the state of the parser.  
     */
    public void loadData(ChunkChopper chopper)
    {
        int flags = chopper.getUnsignedShort();
        chopper.getLong();
        int numKeys = chopper.getUnsignedInt();
        for(int i =0; i < numKeys; i++)
        {
            int frameNumber = chopper.getUnsignedInt();
            int  accelerationData = chopper.getUnsignedShort(); 

            Vector3f position = chopper.getVector3f();
            chopper.getCurrentTrack().locateTrack(frameNumber).position = position;
        }
    }
}
