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

import javax.media.j3d.TransformGroup;
import com.microcrowd.loader.java3d.max3ds.ChunkChopper;

/**
 * Loads information about a named object: Cameras, meshes and lights
 */
public class NamedObjectChunk extends Chunk
{

    /**
     * Adds a TransformGroup the the chopper's branch
     * group to which meshes will be added.
     *
     * @param chopper The chopper containing the state of parsing.  
     */
    public void loadData(ChunkChopper chopper)
    {
        final String name = chopper.getString();
        TransformGroup transformGroup = new TransformGroup();

        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        transformGroup.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        chopper.addObject(name, transformGroup);
    }
}
