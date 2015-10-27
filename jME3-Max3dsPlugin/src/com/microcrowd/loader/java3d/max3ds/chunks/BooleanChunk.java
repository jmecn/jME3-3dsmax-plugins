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

import com.jme3.asset.max3ds.Max3dsLoader;

/**
 * A boolean chunk is true if it is present otherwise
 * there is no chunk and that represents false. This chunk
 * will set chopper data to Boolean true with a key that is the id of the chunk.
 * These have no subchunks. Only String data.
 */
public class BooleanChunk extends Chunk
{
    /**
     * If this method is being called then 
     * a boolean true will be set on the chunk chopper
     * with a key that is the id of this chunk.
     *
     * @param chopper the chopper on which the boolean true data is to be set 
     * 
     */
    public void loadData(Max3dsLoader chopper)
    {
        chopper.pushData(chopper.getID(), new Boolean(true));
    }
}
