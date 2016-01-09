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
import com.jme3.math.Vector3f;

/**
 * Reads and store x,y,z vertex coordinates.
 * The coordinates will be accessed with indexes to construct 
 * the mesh out of triangles.
 * @author  jdeford
 */
public class Vertex3ListChunk extends Chunk
{
    /**
     * Reads all the point data from the chopper
     * and stores it using this chunk's id as the key.
     *
     * @param chopper the chopper that will read and
     * store the data. 
     */
    public void loadData(ChunkChopper chopper)
    {
        int numVertices = chopper.getUnsignedShort();
        Vector3f[] points = new Vector3f[numVertices];
        for (int i = 0; i < numVertices; i++) {
            points[i] = new Vector3f(chopper.getVector3f());
        }

        chopper.pushData(chopper.getID(), points);
    }
}
