package com.jme3.asset.max3ds.data;

import java.util.ArrayList;

public class Max3dsScene {

	/*======== 3DS Data structure ========*/
	// Data of Editor Chunk
	public float scale;
	public ArrayList<EditorMaterial> materials;
	public ArrayList<EditorObject> objects;
	public ArrayList<EditorCamera> cameras;
	public ArrayList<EditorLight> lights;
	
	// Data of KeyFrame Chunks
	public float length;
	public int startFrame;
	public int stopFrame;
	public ArrayList<KeyFrameTrack> frames;
	public ArrayList<KeyFrameTrack> cameraFrames;
	public ArrayList<KeyFrameTrack> lightFrames;

	/**
	 * Initialize 3DS data structure
	 */
	public Max3dsScene() {
		// Editor Chunk
		materials = new ArrayList<EditorMaterial>();
		objects = new ArrayList<EditorObject>();
		cameras = new ArrayList<EditorCamera>();
		lights = new ArrayList<EditorLight>();
		
		// KeyFrame Chunk
		length = 0f;
		startFrame = -1;
		stopFrame = -1;
		frames = new ArrayList<KeyFrameTrack>();
		cameraFrames = new ArrayList<KeyFrameTrack>();
		lightFrames = new ArrayList<KeyFrameTrack>();
	}
	
	EditorObject currentObject;
	KeyFrameTrack currentTrack;
	
	public void add(EditorObject object) {
		objects.add(object);
		currentObject = object;
	}
	/**
     * Add an objectTrack to scene.
     * @param track
     */
	public void add(KeyFrameTrack track) {
		frames.add(track);
		currentTrack = track;
	}
	public EditorObject getCurrentObject() {
		return currentObject;
	}
	/**
	 * Gets the key framer track These should be their own objects instead of
	 * chunks.
	 */
	public KeyFrameTrack getCurrentTrack() {
		return currentTrack;
	}
	
	public EditorObject findObject(String name) {
		int len = objects.size();
		EditorObject obj = null;
		for(int i=0; i<len; i++) {
			obj = objects.get(i);
			if (obj.name.equals(name)) {
				return obj;
			}
		}
		return null;
	}
	
	public KeyFrameTrack findTrack(String name) {
		int len = frames.size();
		KeyFrameTrack track = null;
		for(int i=0; i<len; i++) {
			track = frames.get(i);
			if (track.name.equals(name)) {
				return track;
			}
		}
		return null;
	}
}
