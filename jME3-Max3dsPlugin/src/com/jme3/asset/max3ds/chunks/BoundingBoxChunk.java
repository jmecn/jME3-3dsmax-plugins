package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;

/**
 * Loads the bounding box for keyframer of mesh. The pivot
 * is relative to it.
 * {@see KeyFramerInfoChunk} for more information about using
 * animations from a 3ds file
 */
public class BoundingBoxChunk extends Chunk
{
    /**
     * Gets the bounding box and associates it with the current mesh.
     * @param chopper the ChunkChopper containing the state of the parser.  
     */
    public void loadData(ChunkChopper chopper)
    {
        Vector3f min = chopper.getVector3f();
        Vector3f max = chopper.getVector3f();
        BoundingBox box = new BoundingBox(min, max);
        
        chopper.scene.getCurrentTrack().box = box;
    }

}
