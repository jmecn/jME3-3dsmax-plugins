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

import javax.media.j3d.Behavior;

import com.jme3.asset.max3ds.Max3dsLoader;

/**
 * A KeyFramerInfoChunk stores information about things
 * that happen to meshes: Position information, rotation
 * information, scale information, pivot information 
 * and frame information.
 * Together with the frames chunk thes are used
 * display animation behaviors.
 *
 * @author Josh DeFord 
 */
public class KeyFramerInfoChunk extends Chunk
{

    /**
     * Retrieves the named object for the current key framer
     * inserts the rotation, position and pivot transformations for frame 0
     * and assigns the coordinate system to it.
     *
     * The inverse of the local coordinate system converts from 3ds 
     * semi-absolute coordinates (what is in the file) to local coordinates.
     *
     * Then these local coordinates are converted with matrix 
     * that will instantiate them to absolute coordinates:
     * Xabs = sx a1 (Xl-Px) + sy a2 (Yl-Py) + sz a3 (Zl-Pz) + Tx
     * Yabs = sx b1 (Xl-Px) + sy b2 (Yl-Py) + sz b3 (Zl-Pz) + Ty
     * Zabs = sx c1 (Xl-Px) + sy c2 (Yl-Py) + sz c3 (Zl-Pz) + Tz
     * Where:
     * (Xabs,Yabs,Zabs) = absolute coordinate
     * (Px,Py,Pz) = mesh pivot (constant)
     * (X1,Y1,Z1) = local coordinates
     *
     * @param chopper the ChunkChopper containing the current state of the parser. 
     */
    public void initialize(Max3dsLoader chopper) 
    {
        String meshName = (String)chopper.getObjectName();
        Behavior frameBehavior = chopper.getKeyFramer().createBehavior(meshName,
                                                        chopper.getNamedTransformGroup(meshName),
                                                        chopper.getNamedObject(meshName));
        if(frameBehavior != null)
            chopper.addBehaviorNode(frameBehavior);
    }
}
