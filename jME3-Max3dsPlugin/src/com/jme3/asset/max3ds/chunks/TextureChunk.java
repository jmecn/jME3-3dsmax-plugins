package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.asset.max3ds.ChunkID;

/**
 * Loads percentage values from binary data representing them.
 */
public class TextureChunk extends Chunk {

	/**
	 * Gets the current texture image from the chopper creates a texture with it
	 * and sets that texture on the chopper.
	 * 
	 * @param chopper
	 *            the parser containing the state of parsing
	 */
	public void initialize(ChunkChopper chopper) {
		String textureName = (String) chopper.popData(ChunkID.TEXTURE_NAME);
		chopper.pushData(ChunkID.TEXTURE, textureName);
	}
}
