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


import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.math.Vector3f;
/**
 * Loads the pivot for a mesh. 
 * {@see KeyFramerInfoChunk} for more information about using
 * animations from a 3ds file
 */
public class PivotChunk extends Chunk
{
    /**
     * Gets the pivot and associates it with the current mesh.
     * @param chopper the ChunkChopper containing the state of the parser.  
     */
    public void loadData(ChunkChopper chopper)
    {
        Vector3f pivot = chopper.getVector3f();
		chopper.scene.getCurrentTrack().pivot = pivot;
    }

}
