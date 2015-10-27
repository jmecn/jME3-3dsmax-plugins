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

package com.microcrowd.loader.java3d.max3ds.chunks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

import com.jme3.asset.max3ds.Max3dsLoader;
import com.microcrowd.loader.java3d.max3ds.ChunkMap;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;

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
    public static final Appearance DEFAULT_APPEARANCE;

    private Point3f[] currentVertices;
    private TexCoord2f[] textureTriangles;
    private PointMapper shareMap;

    static {
        DEFAULT_APPEARANCE= new Appearance();
        Material defaultMaterial = new Material();
        defaultMaterial.setAmbientColor(new Color3f(.5f, .5f, .5f));
        //defaultMaterial.setDiffuseColor(new Color3f(.5f, .5f, .5f));
        //defaultMaterial.setSpecularColor(new Color3f(.5f, .5f, .5f));
        DEFAULT_APPEARANCE.setMaterial(defaultMaterial);
    }



    /**
     * Maintains a two way mapping between coordinates
     * and vertices.  A coordinate to vertex is one to many 
     * Vertex to coordinate is one to one.
     * In this class we maintain the definition that a coordinate
     * is a point in 3D space and a vertex is a coordinate serving
     * as one of three defining a face.
     */
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
        public void addCoordinate(Point3f coordinate, int vertexNum)
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
    public void loadData(Max3dsLoader chopper)
    {
        int numFaces = chopper.getUnsignedShort();
        shareMap = new PointMapper(numFaces*3);
        Point3f[] coordinates = (Point3f[])chopper.popData(ChunkMap.VERTEX_LIST);
        TexCoord2f[] texturePoints = (TexCoord2f[])chopper.popData(ChunkMap.TEXTURE_COORDINATES);

        currentVertices = new Point3f[numFaces * 3];
        chopper.pushData(chopper.getID(), currentVertices);
        if (texturePoints != null) 
        {
            textureTriangles = new TexCoord2f[numFaces * 3];
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
    public void initialize(Max3dsLoader chopper)
    {
        final String materialName = (String)chopper.popData(ChunkMap.FACES_MATERIAL);
        final int[]  smoothGroups = (int[])chopper.popData(ChunkMap.SMOOTH);
        Shape3D      shape        = new Shape3D();
        GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);

        geometryInfo.setCoordinates(currentVertices);
        TransformGroup transformGroup = (TransformGroup)chopper.getGroup();
        transformGroup.addChild(shape);

        if (textureTriangles != null) 
        {
            geometryInfo.setTextureCoordinateParams(1, 2);
            geometryInfo.setTextureCoordinates(0, textureTriangles);
        }

        if(materialName != null)
        {
            shape.setAppearance((Appearance)chopper.getNamedObject(materialName));
        }
        else
        {
            shape.setAppearance(DEFAULT_APPEARANCE);
        }
        if(smoothGroups == null)
        {
            NormalGenerator normalGenerator = new NormalGenerator();
            geometryInfo.recomputeIndices();
            normalGenerator.generateNormals(geometryInfo);
        }
        else
        {
            Vector3f[] normals = generateNormals(currentVertices);
            Vector3f[] smoothNormals = smoothNormals(normals, shareMap, smoothGroups);
            geometryInfo.setNormals(smoothNormals);
        }

        new Stripifier().stripify(geometryInfo);
        shape.setGeometry(geometryInfo.getGeometryArray());
        shape.setCapability(Geometry.ALLOW_INTERSECT);
        com.sun.j3d.utils.picking.PickTool.setCapabilities(shape, com.sun.j3d.utils.picking.PickTool.INTERSECT_FULL);

        currentVertices=null;
        textureTriangles=null;
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
    private Vector3f[] generateNormals(Point3f points[])
    {
        Vector3f[] normals = new Vector3f[points.length];
        for(int i=0; i < normals.length;)
        {
            Vector3f normal    = new Vector3f();
            Vector3f v1        = new Vector3f();
            Vector3f v2        = new Vector3f();
    
            v1.sub(points[i+1], points[i]);
            v2.sub(points[i+2], points[i]);
            normal.cross(v1, v2);
            normal.normalize();

    
            normals[i++] = new Vector3f(normal);
            normals[i++] = new Vector3f(normal);
            normals[i++] = new Vector3f(normal);
        }

        return normals;
    }
}
