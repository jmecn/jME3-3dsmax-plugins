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

import javax.media.j3d.BoundingBox;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import com.jme3.asset.max3ds.Max3dsLoader;

/**
 * Loads the bounding box for keyframer of mesh. The pivot
 * is relative to it.
 * {@see KeyFramerInfoChunk} for more information about using
 * animations from a 3ds file
 */
public class BoundingBoxChunk extends Chunk
{
    /**
     * Gets the bounding box and associates it with the current mes.
     * @param chopper the ChunkChopper containing the state of the parser.  
     */
    public void loadData(Max3dsLoader chopper)
    {
        Point3f min = chopper.getPoint();
        Point3f max = chopper.getPoint();
        BoundingBox box = new BoundingBox(new Point3d(min), new Point3d(max));

        Point3f center = new Point3f(max.x - min.x, 
                max.y - min.y, 
                max.z - min.z);

            //chopper.getKeyFramer().setBoundingBox(box);
            chopper.getKeyFramer().setPivotCenter(center);
    }

}
