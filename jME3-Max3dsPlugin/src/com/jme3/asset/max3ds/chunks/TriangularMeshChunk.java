package com.jme3.asset.max3ds.chunks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.asset.max3ds.ChunkID;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;

public class TriangularMeshChunk extends Chunk {
    
    PointMapper shareMap = null;
    Vector3f[] currentVertices = null;
    Vector2f[] textureTriangles = null;
    Vector3f[] smoothNormals = null;
    
    /**
     * Loads a mesh onto the scene graph with the specified data
     * from subchunks.
     * If there is no material, this will put a default
     * material on the shape.
     */
    public void initialize(ChunkChopper chopper)
    {
    	prehandle(chopper);
    	
    	int numFaces = (Integer)chopper.getNamedObject(ChunkID.FACES_DESCRIPTION);
        
        Geometry geom = new Geometry();

        Node parent = chopper.getGroup();
        geom.setName(parent.getName());
        parent.attachChild(geom);
        
        // Materials ²ÄÖÊ
        final String materialName = (String)chopper.popData(ChunkID.FACES_MATERIAL);
        if(materialName != null) {
        	Material mat = (Material)chopper.getNamedObject(materialName);
        	geom.setMaterial(mat);
        } else {
        	geom.setMaterial(chopper.getDefaultMaterial());
        }
        
        // Mesh
        Mesh mesh = new Mesh();
        float v[] = new float[numFaces * 9];
        int f[] = null;
        float tv[] = null;
        float[] n = new float[numFaces * 9];
        
        if (textureTriangles != null) 
        {
            f = new int[numFaces * 3];
            tv = new float[numFaces * 6];
        }
        
        for (int i = 0; i < numFaces; i++) {
            int vertexIndex = i * 3;
            // vertex
            v[i * 9 + 0] = currentVertices[vertexIndex].x;
			v[i * 9 + 1] = currentVertices[vertexIndex].y;
			v[i * 9 + 2] = currentVertices[vertexIndex].z;
			v[i * 9 + 3] = currentVertices[vertexIndex + 1].x;
			v[i * 9 + 4] = currentVertices[vertexIndex + 1].y;
			v[i * 9 + 5] = currentVertices[vertexIndex + 1].z;
			v[i * 9 + 6] = currentVertices[vertexIndex + 2].x;
			v[i * 9 + 7] = currentVertices[vertexIndex + 2].y;
			v[i * 9 + 8] = currentVertices[vertexIndex + 2].z;

            if (textureTriangles != null) {
                // index
     			f[vertexIndex] = vertexIndex;
     			f[vertexIndex + 1] = vertexIndex + 1;
     			f[vertexIndex + 2] = vertexIndex + 2;
     			
     			// uv
    			tv[i * 6] = textureTriangles[vertexIndex].x;
    			tv[i * 6 + 1] = textureTriangles[vertexIndex].y;
    			tv[i * 6 + 2] = textureTriangles[vertexIndex + 1].x;
    			tv[i * 6 + 3] = textureTriangles[vertexIndex + 1].y;
    			tv[i * 6 + 4] = textureTriangles[vertexIndex + 2].x;
    			tv[i * 6 + 5] = textureTriangles[vertexIndex + 2].y;
            }
        }
		
        // Normals
        Vector3f[] normals = null;
        if (smoothNormals == null) {
        	normals = generateNormals(currentVertices);
        } else {
        	normals = smoothNormals;
        }
    	for(int i=0; i<normals.length; i++) {
    		int j = i * 3;
    		n[j] = normals[i].x;
    		n[j+1] = normals[i].y;
    		n[j+2] = normals[i].z;
    	}
        mesh.setBuffer(Type.Normal, 3, n);
        
		mesh.setBuffer(Type.Position, 3, v);
		if (f != null) {
			mesh.setBuffer(Type.Index, 3, f);
		} else {
			System.out.println(parent.getName() + " has no Index");
		}
		if (tv != null) {
			mesh.setBuffer(Type.TexCoord, 2, tv);
		} else {
			System.out.println(parent.getName() + " has no TexCoord");
		}
        
        mesh.setStatic();
		mesh.updateBound();
		mesh.updateCounts();
		
        geom.setMesh(mesh);
    }
    
    private void prehandle(ChunkChopper chopper) {
    	final int numFaces = (Integer)chopper.getNamedObject(ChunkID.FACES_DESCRIPTION);
    	
    	final Vector3f[] coordinates = (Vector3f[])chopper.popData(ChunkID.VERTEX_LIST);
    	final Vector2f[] texturePoints = (Vector2f[])chopper.popData(ChunkID.TEXTURE_COORDINATES);
    	final int[] indices = (int[])chopper.popData(ChunkID.FACES_DESCRIPTION);
        final int[] smoothGroups = (int[])chopper.popData(ChunkID.SMOOTH);
        
    	shareMap = new PointMapper(numFaces*3);
    	currentVertices = new Vector3f[numFaces * 3];
        if (texturePoints != null) 
        {
            textureTriangles = new Vector2f[numFaces * 3];
        } else {
        	textureTriangles = null;
        }
        
        for (int i = 0; i < numFaces; i++) {
            int vertexIndex = i * 3;
            int index0 = indices[vertexIndex];
            int index1 = indices[vertexIndex + 1];
            int index2 = indices[vertexIndex + 2];

            currentVertices[vertexIndex] = coordinates[index0];
            currentVertices[vertexIndex + 1] = coordinates[index1];
            currentVertices[vertexIndex + 2] = coordinates[index2];

            shareMap.addCoordinate(coordinates[index0], vertexIndex);
            shareMap.addCoordinate(coordinates[index1], vertexIndex+1);
            shareMap.addCoordinate(coordinates[index2], vertexIndex+2);


            if (textureTriangles != null) {
                textureTriangles[vertexIndex] = texturePoints[index0];
                textureTriangles[vertexIndex + 1] = texturePoints[index1];
                textureTriangles[vertexIndex + 2] = texturePoints[index2];
            }
        }
        
        // Normals
        if (smoothGroups != null) {
        	Vector3f[] normals = generateNormals(currentVertices);
            smoothNormals = smoothNormals(normals, shareMap, smoothGroups);
        } else {
        	smoothNormals = null;
        }
    }

    /**
     * Takes all the normals for all the vertices and averages them with
     * normals with which they share a coordinate and at least one smooth group.
     * @param currentNormals the normals for each face.
     * @param sharedPoints the point mapper that will choose which points are 
     * and which are not shared.
     * @param smoothGroups the indexed list of group masks loaded by the smooth chunk.
     * @return normals averaged among the shared vertices in their smoothing groups.
     */
    private Vector3f[] smoothNormals(Vector3f[] currentNormals, PointMapper sharedPoints, int[] smoothGroups)
    {
        Vector3f[] smoothNormals = new Vector3f[currentNormals.length];
        for(int i=0; i < currentNormals.length; i++)
        {
            Set<Integer> otherPoints = sharedPoints.getSharedCoordinates(i, smoothGroups);
            if(otherPoints != null)
            {
                Vector3f[] sharedNormals = new Vector3f[otherPoints.size()]; 
                Iterator<Integer> pointIterator = otherPoints.iterator();
                for(int j = 0; j < sharedNormals.length; j++)
                {
                    sharedNormals[j] = currentNormals[pointIterator.next().intValue()];
                }
                smoothNormals[i] = averageNormals(sharedNormals);
            }
            else
            {
                smoothNormals[i] = currentNormals[i];
            }
        }
        return smoothNormals;
    }

    /**
     * Averages the normals provided in order to provide
     * smooth, noncreased appearances for meshes.
     * @param normals the normals that should be averaged
     * @return a normalized normal that can be used in place
     * of all the normals provided.
     */
    private Vector3f averageNormals(Vector3f[] normals)
    {
        Vector3f newNormal = new Vector3f();
        for(int i=0; i < normals.length; i++)
        {
            newNormal.add(normals[i]);
        }
        newNormal.normalize();
        return newNormal;
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
    private Vector3f[] generateNormals(Vector3f points[])
    {
        Vector3f[] normals = new Vector3f[points.length];
        for(int i=0; i < normals.length;)
        {
            Vector3f normal    = new Vector3f();
            Vector3f v1        = new Vector3f();
            Vector3f v2        = new Vector3f();
    
            v1 = points[i+1].subtract(points[i]);
            v2 = points[i+2].subtract(points[i]);
            v1.cross(v2, normal);
            normal.normalize();

    
            normals[i++] = new Vector3f(normal);
            normals[i++] = new Vector3f(normal);
            normals[i++] = new Vector3f(normal);
        }

        return normals;
    }
    
    /**
     * Maintains a two way mapping between coordinates
     * and vertices.  A coordinate to vertex is one to many 
     * Vertex to coordinate is one to one.
     * In this class we maintain the definition that a coordinate
     * is a point in 3D space and a vertex is a coordinate serving
     * as one of three defining a face.
     */
    @SuppressWarnings("serial")
	class PointMapper extends HashMap<Vector3f, Set<Integer>>
    {
		private Set<Integer>[] coordinateSet;
        /**
         * Constructs a PointMapper with a
         * the number of coordinates initialized to size.
         * @param size the number of coordinates in the set.
         */
        public PointMapper(int size)
        {
            coordinateSet = new Set[size];
        }

        /**
         * Adds an index for a coordinate to the set of vertices mapped
         * to that coordinate. All coordinates may have one or more vertices
         * that use them.  
         * @param coordinate the coordinate being mapped to the vertexNum 
         * @param vertexNum the number of the vertex using the coordinate
         */
        public void addCoordinate(Vector3f coordinate, int vertexNum)
        {
            Set<Integer> sharedCoordinates = (Set<Integer>)get(coordinate); 
            if(sharedCoordinates == null)
            {
                sharedCoordinates = new HashSet<Integer>();
                put(coordinate, sharedCoordinates);
            }
            sharedCoordinates.add(new Integer(vertexNum));
            coordinateSet[vertexNum] = sharedCoordinates;
        }

        /**
         * Gets all the coordinates for a particular vertex that
         * also share that vertex after the smoothing groups have been
         * accounted for.  Any coordinates that are not both shared
         * by the vertex and do not share a smoothing group with the coordinate
         * will not be returned.
         * @param coordinateNum the number of the coordinate to get the set
         * of vertices for that share it.
         * @param smoothGroups the group of coordinates used to filter out the 
         * non-shared vertices.
         */
        public Set<Integer> getSharedCoordinates(int coordinateNum, int[] smoothGroups)
        {
            Set<Integer> returnSet = new HashSet<Integer>();
            Set<Integer> sharingVertices = coordinateSet[coordinateNum];
            Iterator<Integer> vertices = sharingVertices.iterator();
            int coordinateMask = smoothGroups[coordinateNum];
            while(vertices.hasNext())
            {
                Integer vertex = (Integer)vertices.next();
                int nextMask = smoothGroups[vertex.intValue()];
                if((nextMask & coordinateMask) != 0)
                {
                    returnSet.add(vertex);
                }
            }
            return returnSet; 
        }
    }
}
