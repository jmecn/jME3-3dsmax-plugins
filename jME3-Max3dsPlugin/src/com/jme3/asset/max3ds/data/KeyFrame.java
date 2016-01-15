package com.jme3.asset.max3ds.data;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class KeyFrame {
	// acc data ignored
	public int frame;
	public Vector3f position;
	public Quaternion rotation;
	public Vector3f scale;
	public float FOV;
	public float roll;
	public String morphName;
	public float hotSpot;
	public float fallOff;
	public ColorRGBA colorTrack;
	
	public String toString() {
		return "Frame:" + frame + " Position:" + position + " Rotation:" + rotation + " Scale:" + scale;
	}
}