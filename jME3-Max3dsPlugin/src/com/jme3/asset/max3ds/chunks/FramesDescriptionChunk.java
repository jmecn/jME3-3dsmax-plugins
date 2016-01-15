package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;

/**
 * This chunk contains the name of the object the frames belong to and the
 * parameters and hierarchy information for it.
 */
public class FramesDescriptionChunk extends Chunk {
	/**
	 * reads the name of the object for these frames and stores it in the
	 * chopper.
	 * 
	 * @param chopper
	 *            the chopper used to store the transient data for this chunk.
	 */
	public void loadData(ChunkChopper chopper) {
		String objectName = chopper.getString();
		chopper.getUnsignedShort();
		chopper.getUnsignedShort();
		int fatherID = chopper.getShort();

		chopper.scene.getCurrentTrack().name = objectName;
		chopper.scene.getCurrentTrack().fatherID = fatherID;
	}
}
