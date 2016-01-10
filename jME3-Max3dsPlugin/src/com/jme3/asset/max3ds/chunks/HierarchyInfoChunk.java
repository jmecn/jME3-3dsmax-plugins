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

/**
 * A HierarchyInfoChunk stores information about 
 * where an object belong in a hierarchy of object that 
 * have animations which may or may not be related.
 * Each object, including dummy objects, have an identifier
 * used to specify them as hierarchical parents of other 
 * objects for the purpose of key framing.
 *
 * @author Josh DeFord 
 */
public class HierarchyInfoChunk extends Chunk
{
    /**
     * Loads a word of data that describes the parent. 
     */
    public void loadData(ChunkChopper chopper)
    {
        int hierarchyIdentifier = chopper.getShort();
        chopper.getKeyFramer().setID(hierarchyIdentifier);
        
        System.out.println("  HierarchyInfoChunk hierarchyID=" + hierarchyIdentifier);
    }

}
