package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;

/**
 * A HierarchyInfoChunk stores information about where an object belong in a
 * hierarchy of object that have animations which may or may not be related.
 * Each object, including dummy objects, have an identifier used to specify them
 * as hierarchical parents of other objects for the purpose of key framing.
 * 
 * @author yanmaoyuan
 */
public class HierarchyInfoChunk extends Chunk {
	/**
	 * Loads a word of data that describes the parent.
	 */
	public void loadData(ChunkChopper chopper) {
		int ID = chopper.getShort();
		chopper.scene.getCurrentTrack().ID = ID;
	}

}
