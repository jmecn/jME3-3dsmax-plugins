package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.asset.max3ds.data.SpotLightInfo;

/**
 * SpotLights to be placed in a scene.
 * 
 * All the default parameters other than position and direction are used and not
 * loaded from the 3ds file.
 */
public class SpotLightChunk extends Chunk {

	/**
	 * This is called by the chunk chopper before any of the chunk's subchunks
	 * are loaded. Any data loaded that may need to be used later by superchunks
	 * should be stored in the chunk chopper via {@link ChunkChopper#popData}
	 * 
	 * @param chopper
	 *            the ChunkChopper that will have the light placed in it.
	 */
	public void loadData(ChunkChopper chopper) {
		SpotLightInfo spotinfo = new SpotLightInfo();
		spotinfo.target = chopper.getVector3f();
		spotinfo.hotSpot = chopper.getFloat();
		spotinfo.falloff = chopper.getFloat();

		chopper.pushData(chopper.getID(), spotinfo);
	}
	
}
