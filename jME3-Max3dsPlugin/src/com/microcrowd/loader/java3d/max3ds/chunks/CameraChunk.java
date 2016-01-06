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

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.jme3.asset.max3ds.Max3dsLoader;
import com.jme3.renderer.Camera;
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
    public void loadData(Max3dsLoader chopper) 
    {
        Vector3d yVector  = new Vector3d(0,1,0);
        Point3d position = new Point3d(chopper.getPoint());
        Point3d target   = new Point3d(chopper.getPoint());
        float    bank     = chopper.getFloat();
        float    lens     = chopper.getFloat();

        //This is the vector for the direction
        //of the camera. Represented as a line 
        //from target to position.  We'll use it
        //as an axis when we bank.
        //Vector3d cameraDirection = new Vector3d();
        //cameraDirection.sub(target,position);
        //AxisAngle4f bankAxisAngle = new AxisAngle4f(cameraDirection, bankAngle);

        Transform3D transform = new Transform3D();
        transform.lookAt(position, target, yVector);
        transform.invert();
        ((TransformGroup)chopper.getGroup()).setTransform(transform);
        chopper.addViewGroup(chopper.getGroup());
    }  
}
