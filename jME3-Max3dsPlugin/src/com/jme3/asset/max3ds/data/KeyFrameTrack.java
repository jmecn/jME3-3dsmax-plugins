package com.jme3.asset.max3ds.data;

import java.util.List;
import java.util.ArrayList;

import com.jme3.animation.BoneTrack;
import com.jme3.animation.SpatialTrack;
import com.jme3.bounding.BoundingBox;
import com.jme3.math.Matrix4f;
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

	public List<KeyFrame> tracks;

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
	
	public boolean hasAnim() {
		return tracks.size() > 0;
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
	
	public SpatialTrack toSpatialTrack(float fps) {
		int size = tracks.size();

		float[] times = new float[size];
		Vector3f[] translations = new Vector3f[size];
		Quaternion[] rotations = new Quaternion[size];
		Vector3f[] scales = new Vector3f[size];

		// TODO fill empty data before doing anything!
		interpolateMissing();
		
		for (int i = 0; i < size; i++) {
			KeyFrame keyframe = tracks.get(i);

			times[i] = keyframe.frame / fps;
			translations[i] = keyframe.position;
			rotations[i] = keyframe.rotation;
			scales[i] = keyframe.scale;
		}
		
		SpatialTrack track = new SpatialTrack(times, translations, rotations, scales);
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

	
	
	
	
	
	
	
	
    /**
     * Retrieves the named object for the each key framer
     * inserts the rotation, position and pivot transformations for frame 0
     * and assigns the coordinate system to it.
     *
     * The inverse of the local coordinate system converts from 3ds 
     * semi-absolute coordinates (what is in the file) to local coordinates.
     *
     * Then these local coordinates are converted with matrix 
     * that will instantiate them to absolute coordinates:
     * Xabs = sx a1 (Xl-Px) + sy a2 (Yl-Py) + sz a3 (Zl-Pz) + Tx
     * Yabs = sx b1 (Xl-Px) + sy b2 (Yl-Py) + sz b3 (Zl-Pz) + Ty
     * Zabs = sx c1 (Xl-Px) + sy c2 (Yl-Py) + sz c3 (Zl-Pz) + Tz
     * Where:
     * (Xabs,Yabs,Zabs) = absolute coordinate
     * (Px,Py,Pz) = mesh pivot (constant)
     * (X1,Y1,Z1) = local coordinates
     *
     */
	public Matrix4f calculateTransform(Matrix4f coordinateSystem) {
		// 1
		Matrix4f localTransform = hasAnim()?buildLocalCoordinates(coordinateSystem):new Matrix4f();
		// 2
		Matrix4f targetTransform = new Matrix4f();
		// 3 buildPivotGroup(coordinateTransform, pivot)
		Matrix4f pivotTransform = buildPivotGroup(coordinateSystem, pivot);
		// 4 buildKeyGroup()
		Matrix4f keyTransform = buildKeyGroup();
		
		Matrix4f rVal = new Matrix4f();
		rVal.multLocal(localTransform)
			.multLocal(targetTransform)
			.multLocal(pivotTransform)
			.multLocal(keyTransform);
		
		return rVal;
	}
	
	
	
	
	
	/**
	 * Builds a transform group from the zeroth key of the 
	 * position and rotation tracks.
	 * @return transform group with position and rotation information
	 */
	private Matrix4f buildKeyGroup() {
		KeyFrame key = tracks.get(0);
		
        Matrix4f keyTransform   = new Matrix4f();
        keyTransform.m03 = key.position.x;
        keyTransform.m13 = key.position.y;
        keyTransform.m23 = key.position.z;

        keyTransform.multLocal(key.rotation);

        keyTransform.scale(key.scale);

        return keyTransform;
	}

	private Matrix4f buildPivotGroup(Matrix4f coordinateSystem, Vector3f pivot) {
		
		Matrix4f pivotTransform = new Matrix4f();
        pivotTransform.mult(coordinateSystem.invert());
        Vector3f nPivot = new Vector3f(pivot);
        nPivot.negateLocal();
        
        // TODO calculate by BoundingBox
        Vector3f pivotCenter = null;
        
        translatePivot(pivotTransform, nPivot, pivotCenter);
        return pivotTransform;
	}
	
    /**
     * Does a pre rotational translation of the pivot.
     * @param transform the matrix that will have a translation concatenated to it.
     * @param vector the vector which will be used to translate the matrix.
     * @param offset the offset used to offset the pivot. 
     */
    private void translatePivot(Matrix4f transform, Vector3f vector, Vector3f offset)
    {
        if(offset != null)
        {
            pivot.subtract(offset);
        }
        Matrix4f matrix = new Matrix4f(transform);

        matrix.m03 += (matrix.m00*vector.x + matrix.m01*vector.y + matrix.m02*vector.z);
        matrix.m13 += (matrix.m10*vector.x + matrix.m11*vector.y + matrix.m12*vector.z);
        matrix.m23 += (matrix.m20*vector.x + matrix.m21*vector.y + matrix.m22*vector.z);

        transform.set(matrix);
    }

	private Matrix4f buildLocalCoordinates(Matrix4f coordinateSystem) {
        Matrix4f coordMatrix = new Matrix4f();
        Vector3f translation = new Vector3f();

        translation.x = coordinateSystem.m03;
        translation.y = coordinateSystem.m13;
        translation.z = coordinateSystem.m23;
        coordinateSystem.invertLocal();

        coordMatrix.set(coordinateSystem);
        coordMatrix.m03 = translation.x;
        coordMatrix.m13 = translation.y;
        coordMatrix.m23 = translation.z;
        coordinateSystem.set(coordMatrix);
        coordinateSystem.invertLocal();
        Matrix4f systemGroup = new Matrix4f(coordinateSystem);
        coordinateSystem.invertLocal();
        return systemGroup;
	}
}
