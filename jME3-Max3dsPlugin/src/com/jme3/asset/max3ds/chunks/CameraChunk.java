package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.asset.max3ds.ChunkID;
import com.jme3.asset.max3ds.data.EditorCamera;
/**
 * Loads percentage values from binary data representing them.
 */
public class CameraChunk extends Chunk
{
    /**
     * Reads the position and target vectors and subtracts them to get 
     * an axis of rotation. Translate a transform to position and rotate
     * on the axis of rotation to point at the target.   The angle 
     * between the z axis and the axis of rotation is the angle used to
     * rotate.  The translated and rotated vector is stored it the
     * chopper as a named object since camera chunks are named. 
     */
    public void loadData(ChunkChopper chopper) 
    {
    	EditorCamera camera = new EditorCamera();
    	camera.name = (String)chopper.popData(ChunkID.NAMED_OBJECT);
    	camera.position  = chopper.getVector3f();
        camera.target    = chopper.getVector3f();
        camera.bankAngle = chopper.getFloat();
        camera.focus     = chopper.getFloat();
        
        chopper.scene.add(camera);
    }  
}
