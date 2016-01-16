package com.jme3.asset.max3ds.data;

import java.util.ArrayList;

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class EditorObject {

	public Matrix4f coordinateSystem;
	
	public String name;
	public int numFaces = 0;
	public Vector3f[] vertexs;
	public Vector2f[] texCoord;
	public int[] indices;// faces
	public int[] smoothGroups;
	
	public ArrayList<String> matNames = new ArrayList<String>();
	public ArrayList<int[]> appliedFacesIndexes = new ArrayList<int[]>();
	
	public void addFaceMaterial(String matName, int[] appliedFacesIndexes) {
		this.matNames.add(matName);
		this.appliedFacesIndexes.add(appliedFacesIndexes);
	}
}
