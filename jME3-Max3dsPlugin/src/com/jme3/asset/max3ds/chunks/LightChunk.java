package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;
import com.jme3.asset.max3ds.ChunkID;
import com.jme3.asset.max3ds.data.EditorLight;
import com.jme3.asset.max3ds.data.SpotLightInfo;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * Lights to be placed in a scene. Only point lights and target spot lights are
 * supported. All the default parameters are used for lights as well. Only
 * position is specified.
 */
public class LightChunk extends Chunk {
	/**
	 * This is called by the chunk chopper before any of the chunk's subchunks
	 * are loaded. Any data loaded that may need to be used later by superchunks
	 * should be stored in the chunk chopper via {@link ChunkChopper#pushData}
	 * 
	 * @param chopper
	 *            used to store the position of the light.
	 */
	public void loadData(ChunkChopper chopper) {
		Vector3f position = chopper.getVector3f();
		chopper.pushData(chopper.getID(), position);
	}

	/**
	 * Gets the data put into the chopper by the subchunks and creates a light,
	 * adding it to the scene as a named object.
	 * 
	 * @param chopper
	 *            the ChunkChopper containing sub chunk data.
	 */
	public void initialize(ChunkChopper chopper) {
		
		EditorLight light = new EditorLight();
		light.name = (String)chopper.popData(ChunkID.NAMED_OBJECT);
		light.position = (Vector3f)chopper.popData(ChunkID.LIGHT);
		light.color = (ColorRGBA) chopper.popData(ChunkID.COLOR);
		light.rangeStart = (Float) chopper.popData(ChunkID.RANGE_START);
		light.rangeEnd = (Float) chopper.popData(ChunkID.RANGE_END);
		light.multiplier = (Float)chopper.popData(ChunkID.MULTIPLIER);
		// light.attenuateOn ignored
		
		light.spotinfo = (SpotLightInfo) chopper.popData(ChunkID.SPOTLIGHT);
		
		chopper.scene.lights.add(light);
	}
}
