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

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.asset.max3ds.ChunkMap;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;

/**
 * This chunk describes all the triangles that make up a mesh.
 * Each triangle is defined in terms of three indexes each of which
 * is a point reference to a vertex in the vertex list loaded
 * by the triangular mesh chunk.
 * After loading the Smoothing chunk the normals for the mesh
 * are generated accordingly.
 */
public class FacesDescriptionChunk extends Chunk
{
    private Vector3f[] currentVertices;
    private Vector2f[] textureTriangles;
    private PointMapper shareMap;

    /**
     * Maintains a two way mapping between coordinates
     * and vertices.  A coordinate to vertex is one to many 
     * Vertex to coordinate is one to one.
     * In this class we maintain the definition that a coordinate
     * is a point in 3D space and a vertex is a coordinate serving
     * as one of three defining a face.
     */
    @SuppressWarnings("serial")
	private class PointMapper extends HashMap
    {
		private Set[] coordinateSet;
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
            Set sharedCoordinates = (Set)get(coordinate); 
            if(sharedCoordinates == null)
            {
                sharedCoordinates = new HashSet();
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
        public Set getSharedCoordinates(int coordinateNum, int[] smoothGroups)
        {
            Set returnSet = new HashSet();
            Set sharingVertices = coordinateSet[coordinateNum];
            Iterator vertices = sharingVertices.iterator();
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

    /**
     * Reads the number of faces from the ChunkChopper.
     * For each face read three shorts representing
     * indices of vertices loaded by the TriangularMeshChunk
     *
     * @param chopper chopper the has the data  
     */
    public void loadData(ChunkChopper chopper)
    {
        int numFaces = chopper.getUnsignedShort();
        shareMap = new PointMapper(numFaces*3);
        Vector3f[] coordinates = (Vector3f[])chopper.popData(ChunkMap.VERTEX_LIST);
        Vector2f[] texturePoints = (Vector2f[])chopper.popData(ChunkMap.TEXTURE_COORDINATES);

        currentVertices = new Vector3f[numFaces * 3];
        chopper.pushData(chopper.getID(), currentVertices);
        if (texturePoints != null) 
        {
            textureTriangles = new Vector2f[numFaces * 3];
        }

        for (int i = 0; i < numFaces; i++) {
            int vertexIndex = i * 3;
            int index0 = chopper.getUnsignedShort();
            int index1 = chopper.getUnsignedShort();
            int index2 = chopper.getUnsignedShort();

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

            //This is a bit masked value that is used to determine which edges are visible... not needed.
            chopper.getUnsignedShort(); 
        }
    }

    /**
     * Loads a mesh onto the scene graph with the specified data
     * from subchunks.
     * If there is no material, this will put a default
     * material on the shape.
     */
    public void initialize(ChunkChopper chopper)
    {
        final String materialName = (String)chopper.popData(ChunkMap.FACES_MATERIAL);
        final int[]  smoothGroups = (int[])chopper.popData(ChunkMap.SMOOTH);
        Geometry     shape        = new Geometry();
        Mesh         mesh         = new Mesh();

        Node parent = chopper.getGroup();
        shape.setName(parent.getName());
        parent.attachChild(shape);
        
        // Vertex 顶点
        float v[] = new float[currentVertices.length * 3];
		for (int i = 0; i < currentVertices.length; i++) {
			Vector3f vert = currentVertices[i];
			
			int j = i * 3;
			v[j] = vert.x;
			v[j + 1] = vert.y;
			v[j + 2] = vert.z;
		}
		mesh.setBuffer(Type.Position, 3, v);
		
		// Faces Index
        float f[] = new float[currentVertices.length * 3];
		for (int i = 0; i < currentVertices.length; i++) {
			int j = i * 3;
			f[j] = j;
			f[j + 1] = j + 1;
			f[j + 2] = j + 2;
		}
		mesh.setBuffer(Type.Index, 3, f);
		
        // TextureCoord 纹理坐标
        if (textureTriangles != null) 
        {
    		float tv[] = new float[textureTriangles.length * 2];
    		for (int i = 0; i < textureTriangles.length; i++) {
    			Vector2f texCoord = textureTriangles[i];
    			
    			int j = i * 2;
    			tv[j] = texCoord.x;
    			tv[j + 1] = texCoord.y;
    		}
            mesh.setBuffer(Type.TexCoord, 2, tv);
        }
        
        // Normals 法线
        if(smoothGroups == null)
        {
        	Vector3f[] normals = generateNormals(currentVertices);
            float[] n = new float[normals.length * 3];
            for(int i=0; i<normals.length; i++) {
            	Vector3f normal = normals[i];
            	int j = i * 3;
            	n[j] = normal.x;
            	n[j + 1] = normal.y;
            	n[j + 2] = normal.z;
            }
            mesh.setBuffer(Type.Normal, 3, n);
        }
        else
        {
        	Vector3f[] normals = generateNormals(currentVertices);
            Vector3f[] smoothNormals = smoothNormals(normals, shareMap, smoothGroups);
            float[] n = new float[smoothNormals.length * 3];
            for(int i=0; i<smoothNormals.length; i++) {
            	Vector3f normal = smoothNormals[i];
            	int j = i * 3;
            	n[j] = normal.x;
            	n[j + 1] = normal.y;
            	n[j + 2] = normal.z;
            }
            mesh.setBuffer(Type.Normal, 3, n);
        }

        // Stripifier 切线
//        new Stripifier().stripify(geometryInfo);
//        shape.setGeometry(geometryInfo.getGeometryArray());
//        shape.setCapability(Geometry.ALLOW_INTERSECT);
//        com.sun.j3d.utils.picking.PickTool.setCapabilities(shape, com.sun.j3d.utils.picking.PickTool.INTERSECT_FULL);

        currentVertices=null;
        textureTriangles=null;
        
        mesh.setStatic();
		mesh.updateBound();
		mesh.updateCounts();
		
        shape.setMesh(mesh);
        

        // Materials 材质
        if(materialName != null)
        {
        	Material mat = (Material)chopper.getNamedObject(materialName);
        	shape.setMaterial(mat);
        }
        else
        {
        	shape.setMaterial(chopper.getDefaultMaterial());
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
    public Vector3f[] smoothNormals(Vector3f[] currentNormals, PointMapper sharedPoints, int[] smoothGroups)
    {
        Vector3f[] smoothNormals = new Vector3f[currentNormals.length];
        for(int i=0; i < currentNormals.length; i++)
        {
            Set otherPoints = sharedPoints.getSharedCoordinates(i, smoothGroups);
            if(otherPoints != null)
            {
                Vector3f[] sharedNormals = new Vector3f[otherPoints.size()]; 
                Iterator pointIterator = otherPoints.iterator();
                for(int j = 0; j < sharedNormals.length; j++)
                {
                    sharedNormals[j] = currentNormals[((Integer)pointIterator.next()).intValue()];
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
    public Vector3f averageNormals(Vector3f[] normals)
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
}
