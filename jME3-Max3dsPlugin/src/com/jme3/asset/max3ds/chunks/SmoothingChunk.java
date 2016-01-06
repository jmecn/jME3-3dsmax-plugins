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

import javax.vecmath.Point3f;

import com.jme3.asset.max3ds.Max3dsLoader;
import com.microcrowd.loader.java3d.max3ds.ChunkMap;

/**
 * This chunk is used to generate normals for a mesh according
 * to the data in the smoothing groups chunk.
 * Vertices that share coordinates will all use the same, averaged
 * normal if they also belong to the same smoothing groups.
 * @author jdeford
 */
public class SmoothingChunk extends Chunk
{
    /**
     * Loads the vertices smoothing groups for 
     * a mesh and stores it in chopper
     *
     * @param chopper the ChunkChopper containing the state of the parser.  
     */
    public void loadData(Max3dsLoader chopper)
    {
        Point3f[] vertices = (Point3f[])chopper.popData(ChunkMap.FACES_DESCRIPTION);
        int[] smoothGroups = new int[vertices.length];
        int numFaces = vertices.length/3;
        for(int i=0; i < numFaces; i++)
        {
            int groupMask = chopper.getInt();
            smoothGroups[i*3]=groupMask;
            smoothGroups[(i*3)+1]=groupMask;
            smoothGroups[(i*3)+2]=groupMask;

            java.util.ArrayList list = new java.util.ArrayList();
            for(int j=0; j < 32; j++)
            {
                if(((0x1l << j) & groupMask) > 0)
                {
                    list.add(new Integer(j));
                }
            }
        }
        chopper.pushData(chopper.getID(), smoothGroups);
    }
}
