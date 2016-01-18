package com.jme3.asset.max3ds;

public interface ChunkID {
	
	int MAIN3DS                = (short)0x4D4D;
	
    int VERSION                = (short)0x0002;
    int COLOR                  = (short)0x0010;
    int SCALE                  = (short)0x0100;
    
    int EDITOR                 = (short)0x3D3D;
    
    int NAMED_OBJECT           = (short)0x4000;
    /** Represent a mesh object for shapes. */
    int MESH                   = (short)0x4100;
    /** The vertex list from which vertices of a face array will be used. */
    int VERTEX_LIST            = (short)0x4110;
    /** reference coordinates into the vertex list which represent shape vertex coordinates. */
    int FACES_DESCRIPTION      = (short)0x4120;
    /**  Key mapping faces material chunk as a child of this chunk */
    int FACES_MATERIAL         = (short)0x4130;
    int TEXTURE_COORDINATES    = (short)0x4140;
    /**  Key mapping smoothing chunk as a child of this chunk */
    int SMOOTH                 = (short)0x4150;
    /** Local coordinate system of the mesh. */
    int COORDINATE_AXES        = (short)0x4160;
    
    /** Represent a light */
    int LIGHT                  = (short)0x4600;
    int SPOTLIGHT              = (short)0x4610;
    /** Signifies that the light is off **/
    int LIGHT_OFF              = (short)0x4620;
    int ATTENUATED             = (short)0x4625;
    int RAYTRACE               = (short)0x4627;
    int SHADOWED               = (short)0x4630;
    int SHADOW_MAP             = (short)0x4641;
    int SHOW_CONE              = (short)0x4650;
    int RECTANGULAR            = (short)0x4651;
    int OVERSHOOT              = (short)0x4652;
    int SPOT_MAP               = (short)0x4653;
    int SPOT_ROLL              = (short)0x4656;
    int RAY_TRACE_BIAS         = (short)0x4658;
    int RANGE_START            = (short)0x4659;
    int RANGE_END              = (short)0x465A;
    int MULTIPLIER             = (short)0x465B;
    
    /** Represent a camera for viewing */
    int CAMERA                 = (short)0x4700;
    
    /** Represent a material */
    int MATERIAL               = (short)0xAFFF;
    int MATERIAL_NAME          = (short)0xA000;
    int AMBIENT_COLOR          = (short)0xA010;
    int DIFFUSE_COLOR          = (short)0xA020;
    int SPECULAR_COLOR         = (short)0xA030;
    int SHININESS              = (short)0xA040;
    int SHININESS_STRENGTH     = (short)0xA041;
    int TRANSPARENCY           = (short)0xA050;
    int TRANSPARENCY_FALLOUT   = (short)0xA052;
    int REFLECTION_BLUR        = (short)0xA053;
    int TWO_SIDED              = (short)0xA081;
    int SELF_ILLUMINATED       = (short)0xA084;
    int RENDER_TYPE            = (short)0xA100;
    int TEXTURE                = (short)0xA200;
    int BUMP_MAP               = (short)0xA230;
    int SHIN_MAP               = (short)0xA33C;
    int TEXTURE_NAME           = (short)0xA300;
    int TEXTURE_TILING         = (short)0xA351;
    int TEXBLUR                = (short)0xA353;
    
    /** Represent the key frame animation */
    int KEYFRAMER              = (short)0xB000;
	int AMBIENT_LIGHT_INFO     = (short)0xB001;
	int MESH_INFO              = (short)0xB002;
	int CAMERA_INFO            = (short)0xB003;
	int CAMERA_TARGET_INFO     = (short)0xB004;
	int OMNI_LIGHT_INFO        = (short)0xB005;
	int SPOT_LIGHT_TARGET_INFO = (short)0xB006;
	int SPOT_LIGHT_INFO        = (short)0xB007;
	int FRAMES_CHUNK           = (short)0xB008;
	int NAME_AND_FLAGS         = (short)0xB010;
	/** Key for the pivot chunk */
	int PIVOT                  = (short)0xB013;
	int BOUNDING_BOX           = (short)0xB014;
	/** Indicates a position track chunk **/
	int POSITION               = (short)0xB020;
	/** Indicates a scale track chunk */
	int SCALE_TRACK            = (short)0xB022;
	/** Indicates a rotation track chunk */
	int ROTATION               = (short)0xB021;
	/** Indicates a hierarchy info chunk **/
	int HIERARCHY_INFO         = (short)0xB030;
	
	
    /*
    put(0x0002, "Max 3DS Version");
    put(0x0010, "Rgb (float)");
    put(0x0011, "Rgb (byte)");
    put(0x0012, "Rgb (byte) gamma corrected");
    put(0x0013, "Rgb (float) gamma corrected");
    put(0x0030, "percent (int)");
    put(0x0031, "percent (float)");
    
    put(0x3D3D, "3D editor chunk");
    put(0x0100, "One unit");
    put(0x1100, "Background bitmap");
    put(0x1101, "Use background bitmap");
    put(0x1200, "Background color");
    put(0x1201, "Use background color");
    put(0x1300, "Gradient colors");
    put(0x1301, "Use gradient");
    put(0x1400, "Shadow map bias");
    put(0x1420, "Shadow map size");
    put(0x1450, "Shadow map sample range");
    put(0x1460, "Raytrace bias");
    put(0x1470, "Raytrace on");
    put(0x2100, "Ambient color");
    put(0x2200, "Fog");
    put(0x2210, "fog background");
    put(0x2201, "Use fog");
    put(0x2210, "Fog background");
    put(0x2300, "Distance queue");
    put(0x2310, "Dim background");
    put(0x2301, "Use distance queue");
    put(0x2302, "Layered fog options");
    put(0x2303, "Use layered fog");
    put(0x3D3E, "Mesh version");
    put(0x4000, "Object block");
    put(0x4010, "Object hidden");
    put(0x4012, "Object doesn't cast");
    put(0x4013, "Matte object");
    put(0x4015, "External process on");
    put(0x4017, "Object doesn't receive shadows");
    put(0x4100, "Triangular mesh");
    put(0x4110, "Vertices list");
    put(0x4120, "Faces description");
    put(0x4130, "Faces material list");
    put(0x4140, "Mapping coordinates list");
    put(0x4150, "Smoothing group list");
    put(0x4160, "Local coordinate system");
    put(0x4165, "Object color in editor");
    put(0x4181, "External process name");
    put(0x4182, "External process parameters");
    put(0x4600, "Light");
    put(0x4610, "Spotlight");
    put(0x4627, "Spot raytrace");
    put(0x4630, "Light shadowed");
    put(0x4641, "Spot shadow map");
    put(0x4650, "Spot show cone");
    put(0x4651, "Spot is rectangular");
    put(0x4652, "Spot overshoot");
    put(0x4653, "Spot map");
    put(0x4656, "Spot roll");
    put(0x4658, "Spot ray trace bias");
    put(0x4620, "Light off");
    put(0x4625, "Attenuation on");
    put(0x4659, "Range start");
    put(0x465A, "Range end");
    put(0x465B, "Multiplier");
    put(0x4700, "Camera");
    put(0x7001, "Window settings");
    put(0x7011, "Window description #2 ...");
    put(0x7012, "Window description #1 ...");
    put(0x7020, "Mesh windows ...");
    put(0xAFFF, "Material block");
    put(0xA000, "Material name");
    put(0xA010, "Ambient color");
    put(0xA020, "Diffuse color");
    put(0xA030, "Specular color");
    put(0xA040, "Shininess percent");
    put(0xA041, "Shininess strength percent");
    put(0xA050, "Transparency percent");
    put(0xA052, "Transparency falloff percent");
    put(0xA053, "Reflection blur percent");
    put(0xA081, "2 sided");
    put(0xA083, "Add trans");
    put(0xA084, "Self illum");
    put(0xA085, "Wire frame on");
    put(0xA087, "Wire thickness");
    put(0xA088, "Face map");
    put(0xA08A, "In tranc");
    put(0xA08C, "Soften");
    put(0xA08E, "Wire in units");
    put(0xA100, "Render type");
    put(0xA240, "Transparency falloff percent present");
    put(0xA250, "Reflection blur percent present");
    put(0xA252, "Bump map present (true percent)");
    put(0xA200, "Texture map 1");
    put(0xA33A, "Texture map 2");
    put(0xA210, "Opacity map");
    put(0xA230, "Bump map");
    put(0xA33C, "Shininess map");
    put(0xA204, "Specular map");
    put(0xA33D, "Self illum. map");
    put(0xA220, "Reflection map");
    put(0xA33E, "Mask for texture map 1");
    put(0xA340, "Mask for texture map 2");
    put(0xA342, "Mask for opacity map");
    put(0xA344, "Mask for bump map");
    put(0xA346, "Mask for shininess map");
    put(0xA348, "Mask for specular map");
    put(0xA34A, "Mask for self illum. map");
    put(0xA34C, "Mask for reflection map");
    put(0xA300, "Mapping filename");
    put(0xA351, "Mapping parameters");
    put(0xA353, "Blur percent");
    put(0xA354, "V scale");
    put(0xA356, "U scale");
    put(0xA358, "U offset");
    put(0xA35A, "V offset");
    put(0xA35C, "Rotation angle");
    put(0xA360, "RGB Luma/Alpha tint 1");
    put(0xA362, "RGB Luma/Alpha tint 2");
    put(0xA364, "RGB tint R");
    put(0xA366, "RGB tint G");
    put(0xA368, "RGB tint B");
    put(0xB000, "Key Framer");
    put(0xB001, "Ambient light information block");
    put(0xB002, "Mesh information block");
    put(0xB003, "Camera information block");
    put(0xB004, "Camera target information block");
    put(0xB005, "Omni light information block");
    put(0xB006, "Spot light target information block");
    put(0xB007, "Spot light information block");
    put(0xB008, "Frames (Start and End)");
    put(0xB009, "Current Frame");
    put(0xB00A, "Animation revision, filename and length");
    put(0xB010, "Object name, parameters and hierarchy father");
    put(0xB013, "Object pivot point");
    put(0xB014, "Bounding Box");
    put(0xB015, "Object morph angle");
    put(0xB020, "Position track");
    put(0xB021, "Rotation track");
    put(0xB022, "Scale track");
    put(0xB023, "FOV track");
    put(0xB024, "Roll track");
    put(0xB025, "Color track");
    put(0xB026, "Morph track");
    put(0xB027, "Hotspot track");
    put(0xB028, "Falloff track");
    put(0xB029, "Hide track");
    put(0xB030, "Hierarchy position");
	*/
}
