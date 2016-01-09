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

import com.jme3.asset.max3ds.ChunkChopper;

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
        chopper.setNamedObject(chopper.getID(), numFaces);

        int[] indices = new int[numFaces * 3];

        for (int i = 0; i < numFaces; i++) {
            int vertexIndex = i * 3;
            int index0 = chopper.getUnsignedShort();
            int index1 = chopper.getUnsignedShort();
            int index2 = chopper.getUnsignedShort();
            
            indices[vertexIndex] = index0;
            indices[vertexIndex + 1] = index1;
            indices[vertexIndex + 2] = index2;

            //This is a bit masked value that is used to determine which edges are visible... not needed.
            chopper.getUnsignedShort(); 
        }
        
        chopper.pushData(chopper.getID(), indices);
    }
}
