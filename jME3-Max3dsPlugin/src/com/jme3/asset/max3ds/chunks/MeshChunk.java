package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.asset.max3ds.ChunkID;
import com.jme3.asset.max3ds.data.EditorObject;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class MeshChunk extends Chunk {
    
	public void loadData(ChunkChopper chopper) {
		EditorObject object = new EditorObject();
		object.name = (String)chopper.popData(ChunkID.NAMED_OBJECT);
		chopper.scene.add(object);
		
		// add this object to scene for subchunks to use
	}
    /**
     * Loads a mesh onto the scene graph with the specified data
     * from subchunks.
     * If there is no material, this will put a default
     * material on the shape.
     */
    public void initialize(ChunkChopper chopper)
    {
		EditorObject object = chopper.scene.getCurrentObject();
        object.coordinateSystem = (Matrix4f)chopper.popData(ChunkID.COORDINATE_AXES);
        object.vertexs = (Vector3f[])chopper.popData(ChunkID.VERTEX_LIST);
        object.texCoord = (Vector2f[])chopper.popData(ChunkID.TEXTURE_COORDINATES);
        object.indices = (int[])chopper.popData(ChunkID.FACES_DESCRIPTION);
        object.smoothGroups = (int[])chopper.popData(ChunkID.SMOOTH);
    }
    
}
