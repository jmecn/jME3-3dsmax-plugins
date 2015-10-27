/*8
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

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.jme3.asset.max3ds.Max3dsLoader;

/**
 * Extracts the rotation information from the 3ds file.
 * Rotations occur about a pivot at a position within
 * a local coordinate system. The rotation information
 * is provided to the KeyFramerInfoChunk which then converts
 * it to a global coordinate system and applies animation
 * information.
 * {@see KeyFramerInfoChunk} for more information about using
 * animations from a 3ds file
 */
public class RotationChunk extends Chunk
{
    /** 
     *  String that will be used as a data object in the transform that the 
     *  RotationInterpolator will be a child of so it may be look up later.
     **/
    public static String ROTATION_TAG = "ROTATION_INTERPOLATOR";

    /**
     * Loads the quaternion for a rotation of a shape
     * and notifies mesh info chunk.
     *
     * @param chopper the ChunkChopper containing the state of the parser.  
     */
    public void loadData(Max3dsLoader chopper)
    {
        int flags = chopper.getUnsignedShort();
        chopper.getLong();
        int numKeys = chopper.getUnsignedInt();

        Quat4f previousQuat = null;

        List quats = new ArrayList();
        for(int i =0; i < numKeys; i++)
        {
            long         frameNumber = chopper.getUnsignedInt();//Part of the track header
            int   accelerationData = chopper.getUnsignedShort();//Part of the track header
            getSplineTerms(accelerationData, chopper);//Part of the track header

            float            angle = chopper.getFloat();
            Vector3f        vector = chopper.getVector(); 

            Quat4f quat = getQuaternion(vector, angle);
            if(previousQuat != null) {
                quat.mul(previousQuat, quat);
            }
            previousQuat = quat; 

            quats.add(quat); 
            if(i==0)
            {
                chopper.getKeyFramer().setRotation(quat);
            }
        }
        chopper.getKeyFramer().setOrientationKeys(quats);
    }

    /**
     * This only reads the spline data and should be part
     * of the track header when it gets invented.
     * @param chopper an integer representing the bits that 
     * determine which of the five possible spline terms are present in the 
     * data and should be read.
     * @param chopper what to read the data from
     * The possible spline values are are 
     * <ol>
     * <li> Tension
     * <li> Continuity
     * <li> Bias
     * <li> EaseTo
     * <li> EaseFrom
     * </ol>
     */
    private void getSplineTerms(final int accelerationData, Max3dsLoader chopper)
    {
        int bits = accelerationData;
        for(int i=0; i < 5; i++)
        {
            bits = bits >>> i;
            if((bits & 1) == 1)
            {
                chopper.getFloat();
            }
        }
    }

    /**
     * This converts a 3ds angle and axis to 
     * a quaternion rotation.  Successive
     * rotations are relative to the first so each
     * rotation must be made absolute by multiplying
     * it with its predecessor
     */
    public Quat4f getQuaternion(Vector3f axis, float angle)
    {
        float sinA  = (float)(java.lang.Math.sin(angle/2.0f));
        float cosA  = (float)(java.lang.Math.cos(angle/2.0f));
        return new Quat4f(axis.x * sinA, axis.y * sinA, axis.z * sinA, cosA);
    }
}
