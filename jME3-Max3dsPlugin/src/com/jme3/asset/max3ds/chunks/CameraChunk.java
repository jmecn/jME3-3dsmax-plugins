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
 * Loads percentage values from binary data representing them.
 */
public class CameraChunk extends Chunk
{
    /**
     * Reads the position and target vectors and subtracts them to get 
     * an axis of rotation. Translate a transform to position and rotate
     * on the axis of rotation to point at the target.   The angle 
     * between the z axis and the axis of rotation is the angle used to
     * rotate.  The translated and rotated vector is stored it the
     * chopper as a named object since camera chunks are named. 
     */
    public void loadData(ChunkChopper chopper) 
    {
    	Vector3f yVector  = new Vector3f(0,1,0);
        Vector3f position = chopper.getVector3f();
        Vector3f target   = chopper.getVector3f();
        float    bank     = chopper.getFloat();
        float    lens     = chopper.getFloat();

        //This is the vector for the direction
        //of the camera. Represented as a line 
        //from target to position.  We'll use it
        //as an axis when we bank.
        //Vector3d cameraDirection = new Vector3d();
        //cameraDirection.sub(target,position);
        //AxisAngle4f bankAxisAngle = new AxisAngle4f(cameraDirection, bankAngle);
        
        // Create a jME3 Camera
        // Camera camera = new Camera(800, 600);
        // camera.setLocation(position);
        // camera.lookAt(target, yVector);

    }  
}
