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
import com.jme3.asset.max3ds.ChunkID;
import com.jme3.light.SpotLight;
import com.jme3.math.Vector3f;

/**
 * SpotLights to be placed in a scene.
 *
 * All the default parameters other than 
 * position and direction are used and
 * not loaded from the 3ds file.
 */
public class SpotLightChunk extends Chunk
{


    /**
     * This is called by the chunk chopper before any of the chunk's 
     * subchunks  are loaded.  Any data loaded that may need to be 
     * used later by superchunks should be stored in
     * the chunk chopper via {@link ChunkChopper#popData}
     *
     * @param chopper the ChunkChopper that will have the light placed in it.  
     */
    public void loadData(ChunkChopper chopper)
    {
    	Vector3f target = chopper.getVector3f();
        float beam = chopper.getFloat();
        float falloff = chopper.getFloat();

        Vector3f position = (Vector3f)chopper.popData(ChunkID.LIGHT);
        Vector3f direction = target.subtract(position);
        
        SpotLight light = new SpotLight();
        light.setPosition(position);
        light.setDirection(direction);
        
        // TODO Im not sure if this is right. Need test
        light.setSpotInnerAngle(beam);
        light.setSpotOuterAngle(falloff);
        
        chopper.pushData(chopper.getID(), light);
    }
}
