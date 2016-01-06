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

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3f;

import com.jme3.asset.max3ds.Max3dsLoader;

/**
 * Extracts scale information from the 3ds file which
 * is then used by the mesh info chunk to construct a 
 * animation.
 */
public class ScaleChunk extends Chunk
{
    /**
     * Loads the scale for a shape
     * and notifies the KeyFramerInfoChunk
     *
     * @param chopper the ChunkChopper containing the state of the parser.  
     */
    public void loadData(Max3dsLoader chopper)
    {
        int flags = chopper.getUnsignedShort();
        chopper.getLong();
        int numKeys = chopper.getUnsignedInt();

        List scaleKeys = new ArrayList();

        for(int i =0; i < numKeys; i++)
        {
            long keyNumber = chopper.getUnsignedInt();
            int  accelerationData = chopper.getUnsignedShort(); 

            float scaleX = chopper.getFloat();
            float scaleZ = chopper.getFloat();
            float scaleY = chopper.getFloat();
            Vector3f scale = new Vector3f(scaleX, scaleY, scaleZ);
            if(i==0)
            {
                chopper.getKeyFramer().setScale(scale);
            }
            scaleKeys.add(scale);
        }
        chopper.getKeyFramer().setScaleKeys(scaleKeys);
    }
}
