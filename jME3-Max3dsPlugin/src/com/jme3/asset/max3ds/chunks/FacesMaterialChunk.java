package com.jme3.asset.max3ds.chunks;

import com.jme3.asset.max3ds.ChunkChopper;

/**
 * FacesMaterialsChunk contains the materials information from the 3ds file. It
 * contains information pertaining to which faces of a mesh have materials on
 * them and the texture coordinates for texture mapping.
 * 
 * @author yanmaoyuan
 */
public class FacesMaterialChunk extends Chunk {
	/**
	 * Loads the texture coordinates for a mesh,
	 * 
	 * @param chopper
	 *            the ChunkChopper containing the state of the parser.
	 */
	public void loadData(ChunkChopper chopper) {
		final String materialName = chopper.getString();
		int numFaces = chopper.getUnsignedShort();
		int[] appliedFacesIndexes = new int[numFaces];
		if (numFaces > 0) {
			for (int j = 0; j < numFaces; j++) {
				appliedFacesIndexes[j] = chopper.getUnsignedShort();
			}
		}

		chopper.scene.getCurrentObject().addFaceMaterial(materialName, appliedFacesIndexes);
	}
}
