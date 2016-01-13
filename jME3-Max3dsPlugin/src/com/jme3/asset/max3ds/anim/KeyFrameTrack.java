package com.jme3.asset.max3ds.anim;

import java.util.List;
import java.util.ArrayList;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;

/**
 * Keyframe Track used to store data loaded by each KeyFrameInfoChunk.<br>
 * It will be used later to build BoneTracks.
 * @author yanmaoyuan
 *
 */
public class KeyFrameTrack {

	public int ID;
	
	// if the named object not in the scene, means it is a dummy object
	public String name;
	
	public int fatherID;

	/** pivot location relative to object origin */
	public Vector3f pivot;
	public BoundingBox box;

	private List<KeyFrame> tracks;

	public KeyFrameTrack() {
		ID = 0;
		name = "$$$dummy";
		fatherID = -1;
		pivot = new Vector3f();
		box = new BoundingBox(pivot, pivot);
		
		tracks = new ArrayList<KeyFrame>();
	}

	public KeyFrame locateTrack(int trackPosition) {
		if (tracks.size() == 0) {
			KeyFrame temp = new KeyFrame();
			temp.frame = trackPosition;
			tracks.add(temp);
			return temp;
		}
		Object[] parts = tracks.toArray();
		int i;
		for (i = 0; i < parts.length; i++) {
			if (((KeyFrame) parts[i]).frame > trackPosition) {
				KeyFrame temp = new KeyFrame();
				temp.frame = trackPosition;
				tracks.add(i, temp);
				return temp;
			} else if (((KeyFrame) parts[i]).frame == trackPosition) {
				return tracks.get(i);
			}
		}
		KeyFrame temp = new KeyFrame();
		temp.frame = trackPosition;
		tracks.add(temp);
		return temp;
	}
	
	@Override
	public String toString() {
		String rVal = "[Name:%s\tID:%d\tFatherID:%d]";
		rVal = String.format(rVal, name, ID, fatherID);
		return rVal;
	}
}
