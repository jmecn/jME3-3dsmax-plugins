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
 * Loads percentage values from binary data representing them.
 */
public class PercentageChunk extends Chunk
{
    /** Represents an int percentage */
    public static final int INT = 0x30;

    /** Represents a float percentage */
    public static final int FLOAT = 0x31;
    private float percentage;

    /**
     * Gets tye type of percentage, reads it
     * and sets the value on the chopper using
     * the id of the current chunk as the key.
     *
     */
    public void loadData(Max3dsLoader chopper)
    {
        int percentageType = chopper.getUnsignedShort();
        int percentageLength = chopper.getUnsignedInt();
        if (percentageType == INT) {
            percentage = (chopper.getUnsignedShort()) / 100f;
            chopper.pushData(chopper.getID(), new Float(percentage));
        } else if (percentageType == FLOAT) {
            percentage = chopper.getFloat() / 100f;
            chopper.pushData(chopper.getID(), new Float(percentage));
        } else {
            throw new IllegalArgumentException("Only float and int percentages are enabled.");
        }

    }
}
