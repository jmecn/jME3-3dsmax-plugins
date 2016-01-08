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
public class ChunkMap extends HashMap<Integer, Chunk> implements ChunkID
{
    private Chunk mainChunk;


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
    }

}
