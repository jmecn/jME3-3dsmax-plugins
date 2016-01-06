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

import javax.media.j3d.Appearance;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Texture;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import com.jme3.asset.max3ds.Max3dsLoader;
import com.microcrowd.loader.java3d.max3ds.ChunkMap;



/**
 * Loads material chunks with ambient, diffuse and specular colors,
 * shininess, transparency, two sidedness and texture.
 */
public class MaterialChunk extends Chunk
{

    //public static final Integer SELF_ILLUMINATED = new Integer((short)0xA084);


    /**
     * This will set the ambient, diffuse and specular
     * colors as well as the textures, two sidedness
     * and transparency of the material.
     *
     * @param chopper the chopper containing the data
     * needed to set the attributes.
     */
    public void initialize(Max3dsLoader chopper)
    {
        Appearance appearance = new Appearance();
        Material material = new Material();

        Color3f ambientColor = (Color3f)chopper.popData(ChunkMap.AMBIENT_COLOR);
        if (ambientColor != null) {
            material.setAmbientColor(ambientColor);
        }

        Color3f color = (Color3f)chopper.popData(ChunkMap.DIFFUSE_COLOR);
        if (color != null) {
            material.setDiffuseColor(color);
        }

        color = (Color3f)chopper.popData(ChunkMap.SPECULAR_COLOR);
        if (color != null) {
            material.setSpecularColor(color);
        }

        Texture texture = (Texture)chopper.popData(ChunkMap.TEXTURE);
        if(texture != null)
        {
            appearance.setTexture(texture);
        }

        Boolean twoSided = (Boolean)chopper.popData(ChunkMap.TWO_SIDED);
        if (twoSided != null) //Just being there is equivalent to a boolean true.
        {

            PolygonAttributes polyAttributes = appearance.getPolygonAttributes(); 
            if(polyAttributes == null)
            {
                polyAttributes = new PolygonAttributes();
            }

            polyAttributes.setCullFace(PolygonAttributes.CULL_NONE);
            appearance.setPolygonAttributes(polyAttributes);
        }

        Float transparency = (Float)chopper.popData(ChunkMap.TRANSPARENCY);
        if (transparency != null) {
            if (transparency.floatValue() > 0.01f) {

                TransparencyAttributes transparencyAttributes = new TransparencyAttributes(TransparencyAttributes.FASTEST, transparency.floatValue());
                appearance.setTransparencyAttributes(transparencyAttributes);
            }
        }

        String name = (String)chopper.popData(ChunkMap.MATERIAL_NAME);
        Float shininess = (Float)chopper.popData(ChunkMap.SHININESS);
        if (shininess != null) 
        {
            float shine = shininess.floatValue() * 1024f;
            material.setShininess(shine);
        }

        /*
           Boolean illuminated = (Boolean)chopper.popData(SELF_ILLUMINATED);
           if(illuminated != null && illuminated.booleanValue() == true)
           {
           material.setEmissiveColor(ambientColor);
           }
           */

        appearance.setMaterial(material);
        chopper.setNamedObject(name, appearance);
    }
}
