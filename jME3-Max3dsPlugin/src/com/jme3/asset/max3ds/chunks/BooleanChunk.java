package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;

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
    public void loadData(ChunkChopper chopper)
    {
        chopper.pushData(chopper.getID(), new Boolean(true));
    }
}
