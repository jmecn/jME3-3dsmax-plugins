package com.jme3.asset.max3ds.data;

import com.jme3.math.ColorRGBA;

/**
 * ChunkID = AFFF
 * 
 * @author yanmaoyuan
 *
 */
public class EditorMaterial {

	public String name;
	
	public ColorRGBA ambientColor;
	public ColorRGBA diffuseColor;
	public ColorRGBA specularColor;
	public ColorRGBA emissiveColor;
	
	public Float shininess;// * 128
	public Float shinStrength;// ignored / Unknown use
	
	public Float transparency;
	public Float transFalloff;// ignored / Unknown use
	
	public Float reflectBlur;// Reflective ignored
	
	public int materialType;// 1=flat 2=gour. 3=phong 4=metal
	
	public Boolean illuminated;// Self illumination ignored
	
	public Boolean twoSided;
	
	public Boolean wireOn;
	public Boolean wireThicknessOn;
	public Float wireThickness;
	
	// The maps
	public String diffuseMap;
	public String diffuseMap2;// ignored
	public String opacityMap;
	public String bumpMap;
	public String specularMap;
	public String shininessMap;
	public String selfillumMap;
	public String reflectionMap;
	
	public Float uScale;// 1/U Scale
	public Float vScale;// 1/V Scale
	public Float uOffset;
	public Float vOffset;
}
