/**
 * Make a donation http://sourceforge.net/donate/index.php?group_id=98797
 * Microcrowd
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
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 * Extracts the local coordinate that will act 
 * as a shape's axis that will be used by the mesh 
 * info chunk.
 */
public class AxisChunk extends Chunk
{
    /**
     * Loads the local coordinate system for the current mesh.
     *
     * @param chopper the ChunkChopper containing the state of the parser. 
     *
     * The location of the local coordinate system is defined relative to 
     * positions and rotations at frame 0.  But the orientation is absolutely
     * defined.
     *
     * With either 3x3 or 4x4 rotation, translation, there
     * is a simple relationship between each matrix and the resulting coordinate
     * system. The first three columns of the matrix define the direction vector of the
     * X, Y and Z axis respectively.
     * If a 4x4 matrix is defined as:
     * <code>
     *     | A B C D |
     * M = | E F G H |
     *     | I J K L |
     *     | M N O P |
     * </code>
     * Then the direction vector for each axis is as follows:
     *
     * <code>
     * X-axis = [ A E I ]
     * Y-axis = [ B F J ]
     * Z-axis = [ C G K ]
     * </code>
     *
     * @return the actual number of bytes read.  
     */
    public void loadData(ChunkChopper chopper)
    {
        Vector3f xAxis  = chopper.getVector3f();
        Vector3f zAxis  = chopper.getVector3f();
        Vector3f yAxis  = chopper.getVector3f();
        Vector3f origin = chopper.getVector3f();
        
        Matrix3f rotation = new Matrix3f(
        		xAxis.x, yAxis.x, zAxis.x,
        		xAxis.y, yAxis.y, zAxis.y,
        		xAxis.z, yAxis.z, zAxis.z
        );
        Quaternion rot = new Quaternion();
        rot.fromRotationMatrix(rotation);
        
        Transform transform = new Transform();
        transform.setRotation(rot);
        transform.setTranslation(origin);

        String meshName = chopper.getObjectName();
        chopper.setCoordinateSystem(meshName, transform);
    }
}
