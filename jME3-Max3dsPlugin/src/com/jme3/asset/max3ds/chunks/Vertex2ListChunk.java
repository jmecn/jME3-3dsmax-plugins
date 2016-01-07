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
import com.jme3.asset.max3ds.M3DLoader;
import com.jme3.math.Vector2f;

/**
 * Reads a list of x,y points that will be used
 * later for texture mapping.
 *
 * @author jdeford 
 */
public class Vertex2ListChunk extends Chunk
{
    private static final int POINT_2F_SIZE = 8;

    /**
     * Reads all the point data from the chopper and stores
     * teh points in the chopper.
     *
     * @param chopper the chopper that will parse and store
     * the data using this chunks id as the key. 
     */
    public void loadData(ChunkChopper chopper)
    {
        int numVertices = chopper.getUnsignedShort();
        Vector2f[] points = new Vector2f[numVertices];
        for (int i = 0; i < numVertices; i++) {
            float point0 = chopper.getFloat();
            float point1 = chopper.getFloat();
            points[i] = new Vector2f(point0, point1);
        }
        chopper.pushData(chopper.getID(), points);
    }
}
