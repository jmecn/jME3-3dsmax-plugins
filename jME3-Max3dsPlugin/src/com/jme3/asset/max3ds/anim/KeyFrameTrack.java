package com.jme3.asset.max3ds.anim;

import java.util.List;
import java.util.ArrayList;

import com.jme3.animation.BoneTrack;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * Keyframe Track used to store data loaded by each KeyFrameInfoChunk.<br>
 * It will be used later to build BoneTracks.
 * 
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
		String rVal = "[ID:%d FatherID:%d Name:\"%s\" Pivod:%s]";
		rVal = String.format(rVal, ID, fatherID, name, pivot);
		return rVal;
	}

	public BoneTrack toBoneTrack(int boneIndex, float speed) {
		int size = tracks.size();

		float[] times = new float[size];
		Vector3f[] translations = new Vector3f[size];
		Quaternion[] rotations = new Quaternion[size];
		Vector3f[] scales = new Vector3f[size];

		// TODO fill empty data before doing anything!
		interpolateMissing();

		for (int i = 0; i < size; i++) {
			KeyFrame keyframe = tracks.get(i);

			times[i] = keyframe.frame / speed;
			translations[i] = keyframe.position;
			rotations[i] = keyframe.rotation;
			scales[i] = keyframe.scale;
		}

		BoneTrack track = new BoneTrack(boneIndex, times, translations,
				rotations, scales);

		return track;
	}

	/**
	 * This must be called one time, once all translations/rotations/scales have
	 * been set. It will interpolate unset values to make the animation look
	 * correct. Tail and head values are assumed to be the identity.
	 */
	public void interpolateMissing() {
		if (tracks.size() > 1) {
			KeyFrame lastgood = tracks.get(0);
			for (int i = 1; i < tracks.size(); i++) {
				KeyFrame keyframe = tracks.get(i);
				if (keyframe.scale == null) {
					keyframe.scale = lastgood.scale;
				}
				if (keyframe.position == null) {
					keyframe.position = lastgood.position;
				}
				if (keyframe.rotation == null) {
					keyframe.rotation = lastgood.rotation;
				}
				lastgood = keyframe;
			}
		}
	}

}
