package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.asset.max3ds.data.KeyFrameTrack;

/**
 * A KeyFramerInfoChunk stores information about things that happen to meshes:
 * Position information, rotation information, scale information, pivot
 * information and frame information. Together with the frames chunk thes are
 * used display animation behaviors.
 * 
 * @author yanmaoyuan
 */
public class KeyFramerInfoChunk extends Chunk {

	/**
	 * Create a new KeyFrameTrack to store animation data
	 */
	@Override
	public void loadData(ChunkChopper chopper) {
		KeyFrameTrack track = new KeyFrameTrack();
		chopper.scene.add(track);
	}
}
