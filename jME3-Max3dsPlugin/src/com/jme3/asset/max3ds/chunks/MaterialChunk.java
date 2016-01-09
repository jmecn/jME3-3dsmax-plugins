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
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

/**
 * Loads material chunks with ambient, diffuse and specular colors,
 * shininess, transparency, two sidedness and texture.
 */
public class MaterialChunk extends Chunk
{

    /**
     * This will set the ambient, diffuse and specular
     * colors as well as the textures, two sidedness
     * and transparency of the material.
     *
     * @param chopper the chopper containing the data
     * needed to set the attributes.
     */
    public void initialize(ChunkChopper chopper)
    {
        Material material = chopper.getLightMaterial();
        material.setBoolean("UseMaterialColors", true);

        ColorRGBA ambientColor = (ColorRGBA)chopper.popData(ChunkID.AMBIENT_COLOR);
        if (ambientColor != null) {
    		material.setColor("Ambient", ambientColor);
        }

        ColorRGBA color = (ColorRGBA)chopper.popData(ChunkID.DIFFUSE_COLOR);
        if (color != null) {
        	material.setColor("Diffuse", color);
        }

        color = (ColorRGBA)chopper.popData(ChunkID.SPECULAR_COLOR);
        if (color != null) {
        	material.setColor("Specular", color);
        }

        Texture texture = (Texture)chopper.popData(ChunkID.TEXTURE);
        if(texture != null)
        {
        	material.setTexture("DiffuseMap", texture);
        }

        Boolean twoSided = (Boolean)chopper.popData(ChunkID.TWO_SIDED);
        if (twoSided != null) //Just being there is equivalent to a boolean true.
        {
        	RenderState rs = material.getAdditionalRenderState();
        	rs.setFaceCullMode(RenderState.FaceCullMode.Off);// twoside
        }

        Float transparency = (Float)chopper.popData(ChunkID.TRANSPARENCY);
        if (transparency != null) {
            if (transparency.floatValue() > 0.01f) {
            	material.setFloat("AlphaDiscardThreshold", transparency);
            }
        }

        String name = (String)chopper.popData(ChunkID.MATERIAL_NAME);
        Float shininess = (Float)chopper.popData(ChunkID.SHININESS);
        if (shininess != null) 
        {
            float shine = shininess.floatValue() * 1024f;
            material.setFloat("Shininess", shine);
        }

       Boolean illuminated = (Boolean)chopper.popData(ChunkID.SELF_ILLUMINATED);
       if(illuminated != null && illuminated.booleanValue() == true)
       {
    	   material.setColor("Emissive", ambientColor);
       }
        
        material.setName(name);
        chopper.setNamedObject(name, material);
    }
}
