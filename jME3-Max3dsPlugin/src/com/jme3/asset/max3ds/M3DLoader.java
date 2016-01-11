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
 * 
 * Rewrite by Maoyuan Yan. Migrating from java3d to jME3.
 */
package com.jme3.asset.max3ds;

import java.io.IOException;
import java.io.InputStream;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/**
 * Used to load a 3ds studio max file. This will sequentially read a 3ds file,
 * load or skip chunks and subchunks and initialize the data for the chunks.
 * A {@link ChunkChopper} is a singleton flyweight factory responsible for
 * chopping the data up and sending it to the  corresponding chunks(which are
 * flyweights ala the flyweight pattern) for processing.
 * 
 * <p>
 * Features not supported; unknown chunks are skipped.
 * </p>
 * 
 * @author Josh DeFord jdeford@microcrowd.com
 * @author yan maoyuan
 *
 */
public class M3DLoader implements AssetLoader {
	private AssetManager manager = null;
	private AssetKey<?> key = null;
	
	private Material defaultMaterial;
	
	public M3DLoader() {
		ChunkChopper.debug = false;
	}
	
	/**
	 * Loads asset from the given input stream, parsing it into an application-usable object.
	 * @see com.jme3.asset.AssetLoader#load(com.jme3.asset.AssetInfo)
	 */
	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		key = assetInfo.getKey();
		manager = assetInfo.getManager();
		
		return parseChunks(assetInfo.openStream());
	}
	
    /**
     * Gets a chunk chopper to do all the dirty work.
     *
     * @param inputStream the stream containing the model.
     * @param modelSize size of the model file. 
     *
     * @return a java3d scene built from input. 
     * @throws IOException 
     */
    protected Node parseChunks(InputStream inputStream)
    {
        ChunkChopper chopper = new ChunkChopper();
        Node model = null;
		try {
			model = chopper.loadSceneBase(inputStream, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return model;
    }

	/**
	 * Loads the image to server as a texture.
	 * 
	 * @param textureImageName
	 *            name of the image that is going to be set to be the texture.
	 */
	public Texture createTexture(String textureImageName) {
		Texture texture = null;
		try {
			texture = manager.loadTexture(key.getFolder() + textureImageName);
			texture.setWrap(WrapMode.Repeat);
		} catch (Exception ex) {
			System.err.println("Cannot load texture image "
							+ textureImageName
							+ ". Make sure it is in the directory with the model file. "
							+ "If its a bmp make sure JAI is installed.");

			texture = manager.loadTexture("Common/Textures/MissingTexture.png");
			texture.setWrap(WrapMode.Clamp);
		}
		return texture;
	}
	
	/**
	 * load a default material
	 * @return
	 */
	public Material getDefaultMaterial() {
		if (defaultMaterial == null) {
			defaultMaterial = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
			defaultMaterial.setColor("Color", ColorRGBA.Cyan);
		}
		
		return defaultMaterial;
	}
	
	/**
	 * load a light material
	 * @return
	 */
	public Material getLightMaterial() {
		Material material = new Material(manager, "Common/MatDefs/Light/Lighting.j3md");
		material.setColor("Ambient", ColorRGBA.White);
		material.setColor("Diffuse", ColorRGBA.White);
		material.setColor("Specular", ColorRGBA.White);
		material.setColor("GlowColor", ColorRGBA.Black);
		material.setFloat("Shininess", 25f);

		RenderState rs = material.getAdditionalRenderState();
		rs.setAlphaTest(true);
		rs.setAlphaFallOff(0.01f);
		
		return material;
	}
}
