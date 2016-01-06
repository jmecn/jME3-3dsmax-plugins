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

import com.jme3.asset.max3ds.Max3dsLoader;

/**
 * These have no subchunks. Only data.
 * Any objects that have a float chunk
 * as a sub chunk may retrieve the float
 * value from {@link ChunkChopper.popData()}
 * using the current chunk id as an argument.
 */
public class FloatChunk extends Chunk
{
    /**
     * Loads a Float value into the chopper
     * for use later when parent chunks are 
     * initializing
     *
     * @param chopper the chopper in which the float
     * chunk will be stored by the id of this chunk. 
     */
    public void loadData(Max3dsLoader chopper)
    {
        chopper.pushData(chopper.getID(), new Float(chopper.getFloat()));
    }
}
