package com.jme3.asset.max3ds.chunks;


import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;

/**
 * Extracts the local coordinate that will act 
 * as a shape's axis that will be used by the mesh 
 * info chunk.
 */
public class AxisChunk extends Chunk
{
    /**
     * Loads the local coordinate system for the current mesh.
     *
     * @param chopper the ChunkChopper containing the state of the parser. 
     *
     * The location of the local coordinate system is defined relative to 
     * positions and rotations at frame 0.  But the orientation is absolutely
     * defined.
     *
     * With either 3x3 or 4x4 rotation, translation, there
     * is a simple relationship between each matrix and the resulting coordinate
     * system. The first three columns of the matrix define the direction vector of the
     * X, Y and Z axis respectively.
     * If a 4x4 matrix is defined as:
     * <code>
     *     | A B C D |
     * M = | E F G H |
     *     | I J K L |
     *     | M N O P |
     * </code>
     * Then the direction vector for each axis is as follows:
     *
     * <code>
     * X-axis = [ A E I ]
     * Y-axis = [ B F J ]
     * Z-axis = [ C G K ]
     * </code>
     *
     * @return the actual number of bytes read.  
     */
    public void loadData(ChunkChopper chopper)
    {
        Vector3f xAxis  = chopper.getVector3f();
        Vector3f zAxis  = chopper.getVector3f();
        Vector3f yAxis  = chopper.getVector3f();
        Vector3f origin = chopper.getVector3f();
        
        Matrix4f transform = new Matrix4f(
        	xAxis.x,  xAxis.y,  xAxis.z, origin.x, 
            yAxis.x,  yAxis.y,  yAxis.z, origin.y,  
            -zAxis.x,  -zAxis.y,  -zAxis.z, origin.z,
            0,0,0,1);
        
        chopper.pushData(chopper.getID(), transform);
    }
}
