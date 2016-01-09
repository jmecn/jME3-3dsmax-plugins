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
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Lights to be placed in a scene.
 * Only point lights and target spot lights are supported.
 * All the default parameters are used for lights as well.
 * Only position is specified.
 */
public class LightChunk extends Chunk
{
    private Vector3f currentPosition;

    /**
     * This is called by the chunk chopper before any of the chunk's 
     * subchunks  are loaded.  Any data loaded that may need to be 
     * used later by superchunks should be stored in
     * the chunk chopper via {@link ChunkChopper#pushData}
     *
     * @param chopper used to store the position of the light. 
     */
    public void loadData(ChunkChopper chopper)
    {
        currentPosition = chopper.getVector3f();
        Node group = chopper.getGroup();
        group.setLocalTranslation(currentPosition);
        chopper.pushData(chopper.getID(), currentPosition);
    }

    /**
     * Gets the data put into the chopper by the subchunks
     * and creates a light, adding it to the scene as a named object.
     * @param chopper the ChunkChopper containing sub chunk data.
     */
    public void initialize(ChunkChopper chopper)
    {
        ColorRGBA color = (ColorRGBA)chopper.popData(ChunkID.COLOR);
        PointLight light = (PointLight)chopper.popData(ChunkID.SPOTLIGHT);
        if(light == null)
        {
            light = new PointLight();
            chopper.addLight(light);
        }

        light.setColor(color);
        light.setPosition(new Vector3f(0,0,0));
        light.setRadius(3000);
        chopper.getGroup().addLight(light);
    }
}
