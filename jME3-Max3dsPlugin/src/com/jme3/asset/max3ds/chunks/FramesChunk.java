package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;

/**
 * This chunk specifies the beginning and end frames.
 */
public class FramesChunk extends Chunk {
	/**
	 * Reads two ints. Start frame and stop frame.
	 * 
	 * @param chopper
	 *            the chopper that may be used to store the starting and
	 *            stopping frames.
	 */
	public void loadData(ChunkChopper chopper) {
		int start = chopper.getUnsignedInt();
		int stop = chopper.getUnsignedInt();

		chopper.scene.startFrame = start;
		chopper.scene.stopFrame = stop;
	}
}
