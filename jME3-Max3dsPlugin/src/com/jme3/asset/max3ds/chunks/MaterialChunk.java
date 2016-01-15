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
import com.jme3.asset.max3ds.ChunkID;
import com.jme3.asset.max3ds.data.EditorMaterial;
import com.jme3.math.ColorRGBA;

/**
 * Loads material chunks with ambient, diffuse and specular colors, shininess,
 * transparency, two sidedness and texture.
 */
public class MaterialChunk extends Chunk {

	/**
	 * This will set the ambient, diffuse and specular colors as well as the
	 * textures, two sidedness and transparency of the material.
	 * 
	 * @param chopper
	 *            the chopper containing the data needed to set the attributes.
	 */
	public void initialize(ChunkChopper chopper) {
		EditorMaterial mat = new EditorMaterial();
		mat.ambientColor = (ColorRGBA) chopper.popData(ChunkID.AMBIENT_COLOR);
		mat.diffuseColor = (ColorRGBA) chopper.popData(ChunkID.DIFFUSE_COLOR);
		mat.specularColor = (ColorRGBA) chopper.popData(ChunkID.SPECULAR_COLOR);
		mat.diffuseMap = (String) chopper.popData(ChunkID.TEXTURE);
		mat.twoSided = (Boolean) chopper.popData(ChunkID.TWO_SIDED);
		mat.transparency = (Float) chopper.popData(ChunkID.TRANSPARENCY);
		mat.name = (String) chopper.popData(ChunkID.MATERIAL_NAME);
		mat.shininess = (Float) chopper.popData(ChunkID.SHININESS);
		mat.illuminated = (Boolean) chopper.popData(ChunkID.SELF_ILLUMINATED);
		chopper.scene.materials.add(mat);
	}
}
