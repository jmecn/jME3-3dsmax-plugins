package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.asset.max3ds.ChunkID;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class MeshChunk extends Chunk {
    
    /**
     * Loads a mesh onto the scene graph with the specified data
     * from subchunks.
     * If there is no material, this will put a default
     * material on the shape.
     */
    public void initialize(ChunkChopper chopper)
    {
    	final int numFaces = (Integer)chopper.getNamedObject(ChunkID.FACES_DESCRIPTION);
    	
    	final Vector3f[] coordinates = (Vector3f[])chopper.popData(ChunkID.VERTEX_LIST);
    	final Vector2f[] texturePoints = (Vector2f[])chopper.popData(ChunkID.TEXTURE_COORDINATES);
    	final int[] indices = (int[])chopper.popData(ChunkID.FACES_DESCRIPTION);
        final int[] smoothGroups = (int[])chopper.popData(ChunkID.SMOOTH);
        
        Node parent = chopper.getGroup();
        String name= parent.getName();
        Mesh mesh = new Mesh();
        Geometry geom = new Geometry(name, mesh);
        parent.attachChild(geom);
        
        // Vertex
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coordinates));
        
        if (indices != null) {
        	// Faces
        	mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indices));
        	// Vertex Normals
        	Vector3f[] normals = generateNormals(coordinates, indices);
        	mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        } else {
        	System.out.println(parent.getName() + " has no Index");
        }
        
        if (texturePoints != null) {
	        // Texture Coord
	        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texturePoints));
        } else {
        	System.out.println(parent.getName() + " has no TexCoord");
        }
        mesh.setStatic();
		mesh.updateBound();
		mesh.updateCounts();
        
        // Materials ²ÄÖÊ
        final String materialName = (String)chopper.popData(ChunkID.FACES_MATERIAL);
        if(materialName != null) {
        	Material mat = (Material)chopper.getNamedObject(materialName);
        	geom.setMaterial(mat);
        } else {
        	geom.setMaterial(chopper.getDefaultMaterial());
        }
    }
    
    /**
     * Generates normals for each vertex of each
     * face that are absolutely normal to the face.
     * @param point0 The first point of the face
     * @param point1 The second point of the face
     * @param point2 The third point of the face
     * @return the three normals that should be 
     * used for the triangle represented by the parameters.
     */
    private Vector3f[] generateNormals(Vector3f points[], int[] indices)
    {
        Vector3f[] normals = new Vector3f[points.length];
        for(int i=0; i < indices.length; i+=3)
        {
        	int index0 = indices[i];
            int index1 = indices[i+1];
            int index2 = indices[i+2];
            
            Vector3f v1 = points[index1].subtract(points[index0]);
            Vector3f v2 = points[index2].subtract(points[index0]);
            Vector3f normal = v1.cross(v2);
            normal.normalize();

            if (normals[index0] == null) {
            	normals[index0] = normal;
            } else {
        		normals[index0].addLocal(normal);
        	}
            
            if (normals[index1] == null) {
            	normals[index1] = normal;
            } else {
        		normals[index1].addLocal(normal);
        	}
            
            if (normals[index2] == null) {
            	normals[index2] = normal;
            } else {
        		normals[index2].addLocal(normal);
        	}
        }
        
        for(int i=0; i<normals.length; i++) {
        	if (normals[i] != null) {
        		normals[i].normalize();
        	} else {
        		System.out.println("MeshChunk vector " + i + " has no normals");
        	}
        }

        return normals;
    }
    
}
