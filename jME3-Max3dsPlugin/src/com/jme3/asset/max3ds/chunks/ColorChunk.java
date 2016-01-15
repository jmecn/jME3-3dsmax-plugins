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
