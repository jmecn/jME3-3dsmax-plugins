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

/**
 * These have no subchunks. Only String data terminated with a null. For
 * strings with unknown length use {@link ChunkChopper#getString} This can
 * also be used for chunks that have data of a known length beginning with a
 * string with unnessecary(you don't want to use it) data following.
 */
public class StringChunk extends Chunk
{
    /**
     * Reads in all the data for this chunk and makes a string out of it.
     * This will set the data in the chopper with a key of this chunks id.
     *
     *
     * @param chopper the chopper that is doing the parsing.  
     */
    public void loadData(ChunkChopper chopper)
    {
        byte[] stringArray = chopper.getChunkBytes();

        String value = new String(stringArray, 0, stringArray.length - 1);
        if (value.indexOf((char)(0x0000)) > 0) {
            value = value.substring(0, value.indexOf((char)(0x0000)));
        }
        
        System.out.println("StringChunk: " + value);

        chopper.pushData(chopper.getID(), value);
    }
}
