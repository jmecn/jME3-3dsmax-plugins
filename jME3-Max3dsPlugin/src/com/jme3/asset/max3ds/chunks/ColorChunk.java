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
import com.jme3.math.ColorRGBA;

/**
 * Loads colors from binary data representing them.
 */
public class ColorChunk extends Chunk
{
    protected int currentColorType;

    private static final int FLOAT_COLOR = 0x10;
    private static final int BYTE_COLOR = 0x11;
    private static final int BYTE_COLOR_GAMMA = 0x12;
    private static final int FLOAT_COLOR_GAMMA = 0x13;

    /**
     * Based on the color type retrieved
     * from {@link #getColorType} loads
     * an rgb or float color and pushes
     * it onto the chunk chopper.
     *
     * @param chopper the chopper that will store the color data.  
     */
    public void loadData(ChunkChopper chopper)
    {
        int colorType = getColorType(chopper);
        if (colorType == BYTE_COLOR) 
        {
            float r = (chopper.getUnsignedByte()) / 255f;
            float g = (chopper.getUnsignedByte()) / 255f;
            float b = (chopper.getUnsignedByte()) / 255f;
            ColorRGBA color = new ColorRGBA(r, g, b, 1);
            chopper.pushData(chopper.getID(), color);
        } 
        else if (colorType == FLOAT_COLOR) 
        {
        	ColorRGBA color = new ColorRGBA(chopper.getFloat(), chopper.getFloat(), chopper.getFloat(), 1);
            chopper.pushData(chopper.getID(), color);
        }
        else 
        {
            throw new IllegalArgumentException("Only RGB colors are enabled. ChunkID=" 
                    + Integer.toHexString((byte)chopper.getID().intValue()) 
                    + " Color type = " + Integer.toHexString((byte)colorType));
        }
    }

    /**
     * Gets the color type for this chunk.
     * @param chopper with the information the
     * chunk may use to determine color type
     * @return the color type for the chunk retrieved
     * from the chopper using this chunks id.
     */
    protected int getColorType(ChunkChopper chopper)
    {
        return chopper.getID().intValue();
    }
}
