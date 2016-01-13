package com.jme3.asset.max3ds.anim;

import java.util.List;
import java.util.ArrayList;

import com.jme.animation.SpatialTransformer.PointInTime;
import com.jme3.animation.BoneTrack;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Quaternion;
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
		
		for(int i=0; i<size; i++) {
			KeyFrame keyframe = tracks.get(i);
			
			times[i] = keyframe.frame/speed;
			translations[i] = keyframe.position;
			rotations[i] = keyframe.rotation;
			scales[i] = keyframe.scale;
		}
		
		BoneTrack track = new BoneTrack(boneIndex, times, translations, rotations, scales);
		
		return track;
	}
	
    /**
     * This must be called one time, once all translations/rotations/scales have
     * been set. It will interpolate unset values to make the animation look
     * correct. Tail and head values are assumed to be the identity.
     */
    public void interpolateMissing() {
        if (tracks.size() != 1) {
            fillTrans();
            fillRots();
            fillScales();
        }
        for (int objIndex = 0; objIndex < numObjects; objIndex++)
            pivots[objIndex].applyToSpatial(toChange[objIndex]);
    }

    /**
     * Called by interpolateMissing(), it will interpolate missing scale values.
     */
    private void fillScales() {
        for (int objIndex = 0; objIndex < numObjects; objIndex++) {
            // 1) Find first non-null scale of objIndex <code>objIndex</code>
            int start;
            for (start = 0; start < tracks.size(); start++) {
                if (tracks.get(start).usedScale.get(objIndex)) break;
            }
            if (start == tracks.size()) { // if they are all null then fill
                // with identity
                for ( KeyFrame keyframe : tracks ) {
                    pivots[objIndex].getScale( // pull original translation
                            keyframe.look[objIndex]
                                    .getScale() ); // ...into object translation.
                }
                continue; // we're done so lets break
            }

            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null
                tracks.get(start).look[objIndex]
                        .getScale(unSyncbeginPos);
                for (int i = 0; i < start; i++)
                    tracks.get(i).look[objIndex]
                            .setScale(unSyncbeginPos);
            }
            int lastgood = start;
            for (int i = start + 1; i < tracks.size(); i++) {
                if (tracks.get(i).usedScale.get(objIndex)) {
                    fillScale(objIndex, lastgood, i); // fills gaps
                    lastgood = i;
                }
            }
            if (lastgood != tracks.size() - 1) { // Make last ones equal to
                // last good
                tracks.get(tracks.size() - 1).look[objIndex]
                        .setScale(tracks.get(lastgood).look[objIndex]
                                .getScale(null));
            }
            tracks.get(lastgood).look[objIndex].getScale(unSyncbeginPos);

            for (int i = lastgood + 1; i < tracks.size(); i++) {
                tracks.get(i).look[objIndex].setScale(unSyncbeginPos);
            }
        }
    }

    /**
     * Interpolates unspecified scale values for objectIndex from start to end.
     *
     * @param objectIndex
     *            Index to interpolate.
     * @param startScaleIndex
     *            Starting scale index.
     * @param endScaleIndex
     *            Ending scale index.
     */
    private void fillScale(int objectIndex, int startScaleIndex,
            int endScaleIndex) {
        tracks.get(startScaleIndex).look[objectIndex].getScale(unSyncbeginPos);
        tracks.get(endScaleIndex).look[objectIndex].getScale(unSyncendPos);
        float startTime = tracks.get(startScaleIndex).time;
        float endTime = tracks.get(endScaleIndex).time;
        float delta = endTime - startTime;
        Vector3f tempVec = new Vector3f();

        for (int i = startScaleIndex + 1; i < endScaleIndex; i++) {
            float thisTime = tracks.get(i).time;
            tempVec.interpolate(unSyncbeginPos, unSyncendPos,
                    (thisTime - startTime) / delta);
            tracks.get(i).look[objectIndex]
                    .setScale(tempVec);
        }
    }

    /**
     * Called by interpolateMissing(), it will interpolate missing rotation
     * values.
     */
    private void fillRots() {
        for (int joint = 0; joint < numObjects; joint++) {
            // 1) Find first non-null rotation of joint <code>joint</code>
            int start;
            for (start = 0; start < tracks.size(); start++) {
                if (tracks.get(start).usedRot.get(joint))
                        break;
            }
            if (start == tracks.size()) { // if they are all null then fill
                // with identity
                for ( PointInTime keyframe : tracks ) {
                    pivots[joint].getRotation( // pull original rotation
                            keyframe.look[joint]
                                    .getRotation() ); // ...into object rotation.
                }

                continue; // we're done so lets break
            }
            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null

                tracks.get(start).look[joint]
                        .getRotation(unSyncbeginRot);
                for (int i = 0; i < start; i++)
                    tracks.get(i).look[joint]
                            .setRotationQuaternion(unSyncbeginRot);
            }
            int lastgood = start;
            for (int i = start + 1; i < tracks.size(); i++) {
                if (tracks.get(i).usedRot.get(joint)) {
                    fillQuats(joint, lastgood, i); // fills gaps
                    lastgood = i;
                }
            }
            //            fillQuats(joint,lastgood,keyframes.size()-1); // fills tail
            tracks.get(lastgood).look[joint]
                    .getRotation(unSyncbeginRot);

            for (int i = lastgood + 1; i < tracks.size(); i++) {
                tracks.get(i).look[joint]
                        .setRotationQuaternion(unSyncbeginRot);
            }
        }
    }

    /**
     * Interpolates unspecified rot values for objectIndex from start to end.
     *
     * @param objectIndex
     *            Index to interpolate.
     * @param startRotIndex
     *            Starting rot index.
     * @param endRotIndex
     *            Ending rot index.
     */
    private void fillQuats(int objectIndex, int startRotIndex, int endRotIndex) {
        tracks.get(startRotIndex).look[objectIndex]
                .getRotation(unSyncbeginRot);
        tracks.get(endRotIndex).look[objectIndex]
                .getRotation(unSyncendRot);
        float startTime = tracks.get(startRotIndex).time;
        float endTime = tracks.get(endRotIndex).time;
        float delta = endTime - startTime;
        Quaternion tempQuat = new Quaternion();

        for (int i = startRotIndex + 1; i < endRotIndex; i++) {
            float thisTime = tracks.get(i).time;
            tempQuat.slerp(unSyncbeginRot, unSyncendRot, (thisTime - startTime)
                    / delta);
            tracks.get(i).look[objectIndex]
                    .setRotationQuaternion(tempQuat);
        }
    }

    /**
     * Called by interpolateMissing(), it will interpolate missing translation
     * values.
     */
    private void fillTrans() {
        for (int objIndex = 0; objIndex < numObjects; objIndex++) {
            // 1) Find first non-null translation of objIndex
            // <code>objIndex</code>
            int start;
            for (start = 0; start < tracks.size(); start++) {
                if (tracks.get(start).usedTrans
                        .get(objIndex)) break;
            }
            if (start == tracks.size()) { // if they are all null then fill
                // with identity
                for ( PointInTime keyframe : tracks ) {
                    pivots[objIndex].getTranslation( // pull original translation
                            keyframe.look[objIndex]
                                    .getTranslation() ); // ...into object translation.
                }
                continue; // we're done so lets break
            }

            if (start != 0) { // if there -are- null elements at the begining,
                // then fill with first non-null
                tracks.get(start).look[objIndex]
                        .getTranslation(unSyncbeginPos);
                for (int i = 0; i < start; i++)
                    tracks.get(i).look[objIndex]
                            .setTranslation(unSyncbeginPos);
            }
            int lastgood = start;
            for (int i = start + 1; i < tracks.size(); i++) {
                if (tracks.get(i).usedTrans.get(objIndex)) {
                    fillVecs(objIndex, lastgood, i); // fills gaps
                    lastgood = i;
                }
            }
            if (lastgood != tracks.size() - 1) { // Make last ones equal to
                // last good
                tracks.get(tracks.size() - 1).look[objIndex]
                        .setTranslation(tracks.get(lastgood).look[objIndex]
                                .getTranslation(null));
            }
            tracks.get(lastgood).look[objIndex]
                    .getTranslation(unSyncbeginPos);

            for (int i = lastgood + 1; i < tracks.size(); i++) {
                tracks.get(i).look[objIndex]
                        .setTranslation(unSyncbeginPos);
            }
        }
    }
}
