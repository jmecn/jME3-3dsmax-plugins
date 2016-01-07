/**
 * Make a donation http://sourceforge.net/donate/index.php?group_id=98797
 * Microcrowd.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Contact Josh DeFord jdeford@microcrowd.com
 */

package com.jme3.asset.max3ds;

import java.util.HashMap;

import com.jme3.asset.max3ds.chunks.*;

/**
 * A Hashmap with the chunk names as values with keys
 * being the chunk id.
 */
@SuppressWarnings("serial")
public class ChunkMap extends HashMap<Integer, Chunk>
{
    private Chunk mainChunk;

    /** Constant designating a chunk as a frames chunk*/
    public static final Integer FRAMES_CHUNK = new Integer((short)0x0B008);
    /** Constant designating a chunk as a mesh info chunk*/
    public static final Integer AMBIENT_LIGHT_INFO = new Integer((short)0x0B001);
    public static final Integer MESH_INFO = new Integer((short)0x0B002);
    public static final Integer CAMERA_INFO = new Integer((short)0x0B003);
    public static final Integer CAMERA_TARGET_INFO = new Integer((short)0x0B004);
    public static final Integer OMNI_LIGHT_INFO = new Integer((short)0x0B005);
    public static final Integer SPOT_LIGHT_TARGET_INFO = new Integer((short)0x0B006);
    public static final Integer SPOT_LIGHT_INFO = new Integer((short)0x0B007);
    /** Key for the name and flags chunk */
    public static final Integer NAME_AND_FLAGS = new Integer((short)0xB010);
    /** Key for the pivot chunk */
    public static final Integer PIVOT = new Integer((short)0xB013);
    /** Indicates a position track chunk **/
    public static final Integer POSITION = new Integer((short)0xB020);
    /** Indicates a scale track chunk */
    public static final Integer SCALE_TRACK= new Integer((short)0xB022);
    /** Indicates a rotation track chunk */
    public static final Integer ROTATION= new Integer((short)0xB021);
    public static final Integer BOUNDING_BOX    = new Integer((short)0x0B014);
    /** Indicates a hierarchy info chunk **/
    public static final Integer HIERARCHY_INFO= new Integer((short)0xB030);
    /** Signifies that the light is off **/
    public static final Integer LIGHT_OFF = new Integer((short)0x4620);
    /** Signifies that the light is attenuated **/
    public static final Integer ATTENUATED = new Integer((short)0x4625);
    public static final Integer RANGE_START = new Integer((short)0x4659);
    public static final Integer RANGE_END = new Integer((short)0x465A);
    public static final Integer MULTIPLIER = new Integer((short)0x465B);
    public static final Integer SPOTLIGHT = new Integer((short)0x4610);
    public static final Integer COLOR     = new Integer((short)0x0010);
    public static final Integer VERSION = new Integer((short)0x2);
    public static final Integer EDITOR = new Integer((short)0x3D3D);
    public static final Integer KEYFRAMER = new Integer((short)0xB000);
    /** These are the chunk ids for colors */
    public static final Integer MATERIAL_NAME = new Integer((short)0xA000);
    /** ID of the chunk that will be used to represent the ambient color. **/
    public static final Integer AMBIENT_COLOR = new Integer((short)0xA010);
    /** ID of the chunk that will be used to represent the diffuse color. **/
    public static final Integer DIFFUSE_COLOR = new Integer((short)0xA020);
    /** ID of the chunk that will be used to represent the specular color. **/
    public static final Integer SPECULAR_COLOR = new Integer((short)0xA030);
    /** ID of the chunk that will be used to represent the shinines. **/
    public static final Integer SHININESS = new Integer((short)0xA040);
//    public static final Integer SHININESS = new Integer((short)0xA041);
    /** ID of the chunk that will be used to represent the transparency. **/
    public static final Integer TRANSPARENCY = new Integer((short)0xA050);
    /** ID of the chunk that will be used to represent the two sided. **/
    public static final Integer TWO_SIDED = new Integer((short)0xA081);
    /** ID of the chunk that will be used to represent the texture. **/
    public static final Integer TEXTURE = new Integer((short)0xA200);
    /** ID of the chunk that will be used to represent the self illumination. **/
    /** Represent a mesh object for shapes. */
    public static final Integer MESH = new Integer((short)0x4100);
    /** Represent a camera for viewing */
    public static final Integer CAMERA = new Integer((short)0x4700);
    /** Represent a light */
    public static final Integer LIGHT = new Integer((short)0x4600);
    /** Signifies that the light is off **/
//    public static final Integer LIGHT_OFF      = new Integer((short)0x4620);
    public static final Integer RAYTRACE       = new Integer((short)0x4627);
    public static final Integer SHADOWED       = new Integer((short)0x4630);
    public static final Integer SHADOW_MAP     = new Integer((short)0x4641);
    public static final Integer SHOW_CONE      = new Integer((short)0x4650);
    public static final Integer RECTANGULAR    = new Integer((short)0x4651);
    public static final Integer OVERSHOOT      = new Integer((short)0x4652);
    public static final Integer SPOT_MAP       = new Integer((short)0x4653);
    public static final Integer SPOT_ROLL      = new Integer((short)0x4656);
    public static final Integer RAY_TRACE_BIAS = new Integer((short)0x4658);
    /** the id of a texture name chunk.*/
    public static final Integer TEXTURE_NAME = new Integer((short)0xA300);
    public static final int TEXTURE_TILING = 0xA351;
    public static final int TEXBLUR = 0xA353;
    /** The vertex list from which vertices of a face array will be used. */
    public static final Integer VERTEX_LIST = new Integer((short)0x4110);
    /** reference coordinates into the vertex list which represent texture coordinates. */
    public static final Integer TEXTURE_COORDINATES = new Integer((short)0x4140);
    /** Local coordinate system of the mesh. */
    public static final Integer COORDINATE_AXES = new Integer((short)0x4160);
    /** reference coordinates into the vertex list which represent shape vertex coordinates. */
    public static final Integer FACES_DESCRIPTION = new Integer((short)0x4120);
    public static final Integer MATERIAL = new Integer((short)0xAFFF);
    public static final Integer SCALE = new Integer((short)0x100);
    public static final Integer NAMED_OBJECT = new Integer((short)0x4000);
    /**  Key mapping faces material chunk as a child of this chunk */
    public static final Integer FACES_MATERIAL = new Integer((short)0x4130);
    /**  Key mapping smoothing chunk as a child of this chunk */
    public static final Integer SMOOTH = new Integer((short)0x4150);

    /**
     * singleton constructor.
     */
    public ChunkMap(Chunk mainChunk)
    {
        this.mainChunk = mainChunk;
        initializeDataMap();
    }

    public Chunk get(Integer chunkID)
    {
        return (Chunk)super.get(chunkID);
    }

    /**
     * The keys are cast to short so that they are improperly signed since 
     * java will be reading improperly signed ids out of the file.
     */
    private void initializeDataMap()
    {
        Chunk keyFramerChunk         = new Chunk("KeyFramerChunk");
        Chunk editorChunk            = new Chunk("EditorChunk");
        Chunk triangularMeshChunk    = new Chunk("TriangularMeshChunk");

        Chunk facesDescriptionChunk  = new FacesDescriptionChunk();
        Chunk framesDescriptionChunk = new FramesDescriptionChunk();
        Chunk textureChunk           = new TextureChunk();
        Chunk lightChunk             = new LightChunk();
        Chunk namedObjectChunk       = new NamedObjectChunk();
        Chunk materialChunk          = new MaterialChunk();
        Chunk keyFramerInfoChunk     = new KeyFramerInfoChunk();
        Chunk spotLightChunk         = new SpotLightChunk();
        Chunk floatChunk             = new FloatChunk();
        Chunk framesChunk            = new FramesChunk();
        Chunk pivotChunk             = new PivotChunk();
        Chunk positionChunk          = new PositionChunk();
        Chunk rotationChunk          = new RotationChunk();
        Chunk scaleChunk             = new ScaleChunk();
        Chunk hierarchyInfoChunk     = new HierarchyInfoChunk();
        Chunk boundingBoxChunk       = new BoundingBoxChunk();
        Chunk stringChunk            = new StringChunk();
        Chunk globalColorChunk       = new GlobalColorChunk();
        Chunk booleanChunk           = new BooleanChunk();
        Chunk percentageChunk        = new PercentageChunk();
        Chunk cameraChunk            = new CameraChunk();
        Chunk colorChunk             = new ColorChunk();
        Chunk vertex3ListChunk       = new Vertex3ListChunk();
        Chunk vertex2ListChunk       = new Vertex2ListChunk();
        Chunk axisChunk              = new AxisChunk();
        Chunk facesMaterialChunk     = new FacesMaterialChunk();
        Chunk smoothingChunk         = new SmoothingChunk();


        //mainChunk.addSubChunk(VERSION, stringChunk);
        mainChunk.addSubChunk(EDITOR, editorChunk);
        mainChunk.addSubChunk(KEYFRAMER, keyFramerChunk);

        editorChunk.addSubChunk(MATERIAL, materialChunk);
        editorChunk.addSubChunk(SCALE, floatChunk);
        editorChunk.addSubChunk(NAMED_OBJECT, namedObjectChunk);

        keyFramerChunk.addSubChunk(FRAMES_CHUNK, framesChunk);
        keyFramerChunk.addSubChunk(MESH_INFO, keyFramerInfoChunk);
        //keyFramerChunk.addSubChunk(AMBIENT_LIGHT_INFO, keyFramerInfoChunk);
        //keyFramerChunk.addSubChunk(CAMERA_INFO, keyFramerInfoChunk);
        //keyFramerChunk.addSubChunk(CAMERA_TARGET_INFO, keyFramerInfoChunk);
        //keyFramerChunk.addSubChunk(OMNI_LIGHT_INFO, keyFramerInfoChunk);
        //keyFramerChunk.addSubChunk(SPOT_LIGHT_TARGET_INFO, keyFramerInfoChunk);
        //keyFramerChunk.addSubChunk(SPOT_LIGHT_INFO, keyFramerInfoChunk);

        keyFramerInfoChunk.addSubChunk(NAME_AND_FLAGS, framesDescriptionChunk);
        keyFramerInfoChunk.addSubChunk(PIVOT, pivotChunk);
        keyFramerInfoChunk.addSubChunk(POSITION, positionChunk);
        keyFramerInfoChunk.addSubChunk(ROTATION, rotationChunk);
        keyFramerInfoChunk.addSubChunk(SCALE_TRACK, scaleChunk);
        keyFramerInfoChunk.addSubChunk(HIERARCHY_INFO, hierarchyInfoChunk);
        keyFramerInfoChunk.addSubChunk(BOUNDING_BOX, boundingBoxChunk);

        //spotLightChunk.addSubChunk(LIGHT_OFF, booleanChunk);
        //spotLightChunk.addSubChunk(RAYTRACE, booleanChunk);
        //spotLightChunk.addSubChunk(SHADOWED, booleanChunk);
        //spotLightChunk.addSubChunk(SHOW_CONE, booleanChunk);
        //spotLightChunk.addSubChunk(RECTANGULAR, booleanChunk);
        //spotLightChunk.addSubChunk(SHADOW_MAP, booleanChunk);
        //spotLightChunk.addSubChunk(OVERSHOOT, booleanChunk);
        //spotLightChunk.addSubChunk(SPOT_MAP, booleanChunk);
        //spotLightChunk.addSubChunk(SPOT_ROLL, booleanChunk);
        //spotLightChunk.addSubChunk(RAY_TRACE_BIAS, booleanChunk);

        materialChunk.addSubChunk(MATERIAL_NAME, stringChunk);

        materialChunk.addSubChunk(AMBIENT_COLOR, globalColorChunk);
        materialChunk.addSubChunk(DIFFUSE_COLOR, globalColorChunk);
        materialChunk.addSubChunk(SPECULAR_COLOR, globalColorChunk);
        materialChunk.addSubChunk(TEXTURE, textureChunk);

        materialChunk.addSubChunk(TWO_SIDED, booleanChunk);

        materialChunk.addSubChunk(SHININESS, percentageChunk);
        materialChunk.addSubChunk(TRANSPARENCY, percentageChunk);

        namedObjectChunk.addSubChunk(MESH, triangularMeshChunk);
        namedObjectChunk.addSubChunk(CAMERA, cameraChunk);
        namedObjectChunk.addSubChunk(LIGHT, lightChunk);

        lightChunk.addSubChunk(RANGE_START, floatChunk);
        lightChunk.addSubChunk(COLOR, colorChunk);
        lightChunk.addSubChunk(RANGE_END, floatChunk);
        lightChunk.addSubChunk(MULTIPLIER, floatChunk);
        lightChunk.addSubChunk(SPOTLIGHT, spotLightChunk);


        textureChunk.addSubChunk(TEXTURE_NAME, stringChunk);

        triangularMeshChunk.addSubChunk(VERTEX_LIST, vertex3ListChunk);
        triangularMeshChunk.addSubChunk(TEXTURE_COORDINATES, vertex2ListChunk);
        triangularMeshChunk.addSubChunk(FACES_DESCRIPTION, facesDescriptionChunk);
        triangularMeshChunk.addSubChunk(COORDINATE_AXES, axisChunk);

        facesDescriptionChunk.addSubChunk(FACES_MATERIAL, facesMaterialChunk);
        facesDescriptionChunk.addSubChunk(SMOOTH, smoothingChunk);

        /*
           put(new Integer((short)0x0002), "Max 3DS Version");
           put(new Integer((short)0x0010), "Rgb (float)");
           put(new Integer((short)0x0011), "Rgb (byte)");
           put(new Integer((short)0x0012), "Rgb (byte) gamma corrected");
           put(new Integer((short)0x0013), "Rgb (float) gamma corrected");
           put(new Integer((short)0x0030), "percent (int)");
           put(new Integer((short)0x0031), "percent (float)");
           
           put(new Integer((short)0x3D3D), "3D editor chunk");
           put(new Integer((short)0x0100), "One unit");
           put(new Integer((short)0x1100), "Background bitmap");
           put(new Integer((short)0x1101), "Use background bitmap");
           put(new Integer((short)0x1200), "Background color");
           put(new Integer((short)0x1201), "Use background color");
           put(new Integer((short)0x1300), "Gradient colors");
           put(new Integer((short)0x1301), "Use gradient");
           put(new Integer((short)0x1400), "Shadow map bias");
           put(new Integer((short)0x1420), "Shadow map size");
           put(new Integer((short)0x1450), "Shadow map sample range");
           put(new Integer((short)0x1460), "Raytrace bias");
           put(new Integer((short)0x1470), "Raytrace on");
           put(new Integer((short)0x2100), "Ambient color");
           put(new Integer((short)0x2200), "Fog");
           put(new Integer((short)0x2210), "fog background");
           put(new Integer((short)0x2201), "Use fog");
           put(new Integer((short)0x2210), "Fog background");
           put(new Integer((short)0x2300), "Distance queue");
           put(new Integer((short)0x2310), "Dim background");
           put(new Integer((short)0x2301), "Use distance queue");
           put(new Integer((short)0x2302), "Layered fog options");
           put(new Integer((short)0x2303), "Use layered fog");
           put(new Integer((short)0x3D3E), "Mesh version");
           put(new Integer((short)0x4000), "Object block");
           put(new Integer((short)0x4010), "Object hidden");
           put(new Integer((short)0x4012), "Object doesn't cast");
           put(new Integer((short)0x4013), "Matte object");
           put(new Integer((short)0x4015), "External process on");
           put(new Integer((short)0x4017), "Object doesn't receive shadows");
           put(new Integer((short)0x4100), "Triangular mesh");
           put(new Integer((short)0x4110), "Vertices list");
           put(new Integer((short)0x4120), "Faces description");
           put(new Integer((short)0x4130), "Faces material list");
           put(new Integer((short)0x4140), "Mapping coordinates list");
           put(new Integer((short)0x4150), "Smoothing group list");
           put(new Integer((short)0x4160), "Local coordinate system");
           put(new Integer((short)0x4165), "Object color in editor");
           put(new Integer((short)0x4181), "External process name");
           put(new Integer((short)0x4182), "External process parameters");
           put(new Integer((short)0x4600), "Light");
           put(new Integer((short)0x4610), "Spotlight");
           put(new Integer((short)0x4627), "Spot raytrace");
           put(new Integer((short)0x4630), "Light shadowed");
           put(new Integer((short)0x4641), "Spot shadow map");
           put(new Integer((short)0x4650), "Spot show cone");
           put(new Integer((short)0x4651), "Spot is rectangular");
           put(new Integer((short)0x4652), "Spot overshoot");
           put(new Integer((short)0x4653), "Spot map");
           put(new Integer((short)0x4656), "Spot roll");
           put(new Integer((short)0x4658), "Spot ray trace bias");
           put(new Integer((short)0x4620), "Light off");
           put(new Integer((short)0x4625), "Attenuation on");
           put(new Integer((short)0x4659), "Range start");
           put(new Integer((short)0x465A), "Range end");
           put(new Integer((short)0x465B), "Multiplier");
           put(new Integer((short)0x4700), "Camera");
           put(new Integer((short)0x7001), "Window settings");
           put(new Integer((short)0x7011), "Window description #2 ...");
           put(new Integer((short)0x7012), "Window description #1 ...");
           put(new Integer((short)0x7020), "Mesh windows ...");
           put(new Integer((short)0xAFFF), "Material block");
           put(new Integer((short)0xA000), "Material name");
           put(new Integer((short)0xA010), "Ambient color");
           put(new Integer((short)0xA020), "Diffuse color");
           put(new Integer((short)0xA030), "Specular color");
           put(new Integer((short)0xA040), "Shininess percent");
           put(new Integer((short)0xA041), "Shininess strength percent");
           put(new Integer((short)0xA050), "Transparency percent");
           put(new Integer((short)0xA052), "Transparency falloff percent");
           put(new Integer((short)0xA053), "Reflection blur percent");
           put(new Integer((short)0xA081), "2 sided");
           put(new Integer((short)0xA083), "Add trans");
           put(new Integer((short)0xA084), "Self illum");
           put(new Integer((short)0xA085), "Wire frame on");
           put(new Integer((short)0xA087), "Wire thickness");
           put(new Integer((short)0xA088), "Face map");
           put(new Integer((short)0xA08A), "In tranc");
           put(new Integer((short)0xA08C), "Soften");
           put(new Integer((short)0xA08E), "Wire in units");
           put(new Integer((short)0xA100), "Render type");
           put(new Integer((short)0xA240), "Transparency falloff percent present");
           put(new Integer((short)0xA250), "Reflection blur percent present");
           put(new Integer((short)0xA252), "Bump map present (true percent)");
           put(new Integer((short)0xA200), "Texture map 1");
           put(new Integer((short)0xA33A), "Texture map 2");
           put(new Integer((short)0xA210), "Opacity map");
           put(new Integer((short)0xA230), "Bump map");
           put(new Integer((short)0xA33C), "Shininess map");
           put(new Integer((short)0xA204), "Specular map");
           put(new Integer((short)0xA33D), "Self illum. map");
           put(new Integer((short)0xA220), "Reflection map");
           put(new Integer((short)0xA33E), "Mask for texture map 1");
           put(new Integer((short)0xA340), "Mask for texture map 2");
           put(new Integer((short)0xA342), "Mask for opacity map");
           put(new Integer((short)0xA344), "Mask for bump map");
           put(new Integer((short)0xA346), "Mask for shininess map");
           put(new Integer((short)0xA348), "Mask for specular map");
           put(new Integer((short)0xA34A), "Mask for self illum. map");
           put(new Integer((short)0xA34C), "Mask for reflection map");
           put(new Integer((short)0xA300), "Mapping filename");
           put(new Integer((short)0xA351), "Mapping parameters");
           put(new Integer((short)0xA353), "Blur percent");
           put(new Integer((short)0xA354), "V scale");
           put(new Integer((short)0xA356), "U scale");
           put(new Integer((short)0xA358), "U offset");
           put(new Integer((short)0xA35A), "V offset");
           put(new Integer((short)0xA35C), "Rotation angle");
           put(new Integer((short)0xA360), "RGB Luma/Alpha tint 1");
           put(new Integer((short)0xA362), "RGB Luma/Alpha tint 2");
           put(new Integer((short)0xA364), "RGB tint R");
           put(new Integer((short)0xA366), "RGB tint G");
           put(new Integer((short)0xA368), "RGB tint B");
           put(new Integer((short)0xB000), "Key Framer");
           put(new Integer((short)0xB001), "Ambient light information block");
           put(new Integer((short)0xB002), "Mesh information block");
           put(new Integer((short)0xB003), "Camera information block");
           put(new Integer((short)0xB004), "Camera target information block");
           put(new Integer((short)0xB005), "Omni light information block");
           put(new Integer((short)0xB006), "Spot light target information block");
           put(new Integer((short)0xB007), "Spot light information block");
           put(new Integer((short)0xB008), "Frames (Start and End)");
           put(new Integer((short)0xB009), "Current Frame");
           put(new Integer((short)0xB00A), "Animation revision, filename and length");
           put(new Integer((short)0xB010), "Object name, parameters and hierarchy father");
           put(new Integer((short)0xB013), "Object pivot point");
           put(new Integer((short)0xB014), "Bounding Box");
           put(new Integer((short)0xB015), "Object morph angle");
           put(new Integer((short)0xB020), "Position track");
           put(new Integer((short)0xB021), "Rotation track");
           put(new Integer((short)0xB022), "Scale track");
           put(new Integer((short)0xB023), "FOV track");
           put(new Integer((short)0xB024), "Roll track");
           put(new Integer((short)0xB025), "Color track");
           put(new Integer((short)0xB026), "Morph track");
           put(new Integer((short)0xB027), "Hotspot track");
           put(new Integer((short)0xB028), "Falloff track");
           put(new Integer((short)0xB029), "Hide track");
           put(new Integer((short)0xB030), "Hierarchy position");
        */
    }

    }
