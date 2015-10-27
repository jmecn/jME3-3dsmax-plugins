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

import javax.media.j3d.SpotLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.jme3.asset.max3ds.Max3dsLoader;
import com.microcrowd.loader.java3d.max3ds.ChunkMap;

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
    public void loadData(Max3dsLoader chopper)
    {
        Point3f target = chopper.getPoint();
        float beam = chopper.getFloat();
        float falloff = chopper.getFloat();
        SpotLight light = new SpotLight();

        Vector3f direction = new Vector3f(0,0,-1); 

        Vector3f position = (Vector3f)chopper.popData(ChunkMap.LIGHT);
        TransformGroup group = chopper.getGroup();
        Transform3D transform = new Transform3D();
        group.getTransform(transform);
        transform.lookAt(new Point3d(position), new Point3d(target), new Vector3d(0,1,0));
        transform.invert();
        transform.setTranslation(position);
        group.setTransform(transform);

        chopper.pushData(chopper.getID(), light);
        chopper.addLightNode(light);
    }
}
