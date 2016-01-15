package com.jme3.asset.max3ds.data;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * ID = 0x4600
 * @author yanmaoyuan
 *
 */
public class EditorLight {

	public String name;// read by parent Chunk
	
	public Vector3f position;
	public ColorRGBA color;
	public Float rangeStart;
	public Float rangeEnd;
	public Float multiplier = 1f;
	public Boolean attenuateOn;
	
	public SpotLightInfo spotinfo;

}
