package com.jme3.asset.max3ds.data;

import java.util.HashMap;

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class EditorObject {

	public Matrix4f coordinateSystem;
	
	public String name;
	public int numFaces;
	public Vector3f[] vertexs;
	public Vector2f[] texCoord;
	public int[] indices;// faces
	public int[] smoothGroups;
	public HashMap<String, int[]> faceMaterials;
	
	public void addFaceMaterial(String matName, int[] appliedFacesIndexes) {
		if (faceMaterials == null) {
			faceMaterials = new HashMap<String, int[]>();
		}
		faceMaterials.put(matName, appliedFacesIndexes);
	}
}
