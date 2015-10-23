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

/**
 * Loads colors from binary data representing them.
 * Its a global in the manner that it reads its own
 * header information.
 */
public class GlobalColorChunk extends ColorChunk
{
    /**
     * Gets the color type for this chunk.
     * @param chopper with the information the
     * chunk may use to determine color type
     * @return the color type for the chunk.
     */
    protected int getColorType(ChunkChopper chopper)
    {
        int type = chopper.getUnsignedShort();
        int colorLength = chopper.getUnsignedInt();
        return type;
    }
}
