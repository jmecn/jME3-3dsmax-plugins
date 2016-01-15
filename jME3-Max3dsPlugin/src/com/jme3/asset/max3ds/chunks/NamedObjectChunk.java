package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;

/**
 * Loads information about a named object: Cameras, meshes and lights
 */
public class NamedObjectChunk extends Chunk {
	/**
	 * Adds a Node the the chopper's branch group to which meshes will be added.
	 * 
	 * @param chopper
	 *            The chopper containing the state of parsing.
	 */
	public void loadData(ChunkChopper chopper) {
		final String name = chopper.getString();
		chopper.pushData(chopper.getID(), name);
	}
}
