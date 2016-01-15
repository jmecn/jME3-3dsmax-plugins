package com.jme3.asset.max3ds.data;

import com.jme3.math.Vector3f;

/**
 * 0x4610
 * @author yanmaoyuan
 *
 */
public class SpotLightInfo {
	public Vector3f target;
	public Float hotSpot;
	public Float falloff;
	
	// spot subchunk
	// all ignored
	public Boolean lightOff;
	public Boolean rayTrace;
	public Boolean shadowed;
	public Boolean showCone;
	public Boolean rectangular;
	public Boolean shadowMap;
	public Boolean overShoot;
	public Boolean spotMap;
	public Boolean spotRoll;
	public Boolean rayTraceBias;
}