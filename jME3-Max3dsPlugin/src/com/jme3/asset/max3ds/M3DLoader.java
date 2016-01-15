package com.jme3.asset.max3ds;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Set;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.Skeleton;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.max3ds.data.*;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.BufferUtils;

/**
 * Used to load a 3ds studio max file. This will sequentially read a 3ds file,
 * load or skip chunks and subchunks and initialize the data for the chunks. A
 * {@link ChunkChopper} is a singleton flyweight factory responsible for
 * chopping the data up and sending it to the corresponding chunks(which are
 * flyweights ala the flyweight pattern) for processing.
 * 
 * <p>
 * Features not supported; unknown chunks are skipped.
 * </p>
 * 
 * @author yanmaoyuan
 * 
 */
public class M3DLoader implements AssetLoader {
	private AssetManager manager = null;
	private AssetKey<?> key = null;

	private Max3dsScene scene;
	private Node rootNode;

	private Material defaultMaterial;

	public M3DLoader() {
		ChunkChopper.debug = false;
	}

	/**
	 * Loads asset from the given input stream, parsing it into an
	 * application-usable object.
	 * 
	 * @see com.jme3.asset.AssetLoader#load(com.jme3.asset.AssetInfo)
	 */
	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		key = assetInfo.getKey();
		manager = assetInfo.getManager();

		return parseChunks(assetInfo.openStream());
	}

	/**
	 * Gets a chunk chopper to do all the dirty work.
	 * 
	 * @param inputStream
	 *            the stream containing the model.
	 * @param modelSize
	 *            size of the model file.
	 * 
	 * @return a java3d scene built from input.
	 * @throws IOException
	 */
	protected Node parseChunks(InputStream inputStream) {
		ChunkChopper chopper = new ChunkChopper();
		try {
			scene = chopper.loadScene(inputStream);
			rootNode = buildScene();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return rootNode;
	}

	private HashMap<String, Material> materials;
	/**
	 * Change Max 3DS data structure to JME3 data structure
	 * @return
	 */
	private Node buildScene() {
		rootNode = new Node("3DS Scene");
		
		generateMaterials();
		generateGeometrys();
		generateLights();
		generateAnimation();
		
		return rootNode;
	}
	
	/**
	 * Create names material maps
	 */
	private void generateMaterials() {
		materials = new HashMap<String, Material>();
		
		for(EditorMaterial mat:scene.materials) {
			Material material = buildMaterial(mat);
			materials.put(mat.name, material);
		}
		
	}
	
	private void generateGeometrys() {
		for(EditorObject obj : scene.objects) {
			Geometry geom = buildGeometry(obj);
			applyMaterial(geom, obj);
			
			rootNode.attachChild(geom);
		}
	}
	
	private void generateLights() {
		for(EditorLight light : scene.lights) {
			if (light.spotinfo == null) {
				PointLight pointLight = new PointLight();
				pointLight.setColor(light.color);
				pointLight.setPosition(light.position);
				pointLight.setRadius(light.rangeEnd);
				rootNode.addLight(pointLight);
			} else {
				SpotLightInfo spotinfo = light.spotinfo;
				
				SpotLight spotLight = new SpotLight();
				spotLight.setColor(light.color);
				spotLight.setPosition(light.position);
				
				Vector3f direction = spotinfo.target.subtract(light.position);
				spotLight.setDirection(direction.normalize());
				
				spotLight.setSpotInnerAngle(spotinfo.hotSpot/180);
				spotLight.setSpotOuterAngle(spotinfo.falloff/180);
				
				spotLight.setSpotRange(light.rangeEnd);
				
				rootNode.addLight(spotLight);
			}
			
		}
	}
	
	private void generateAnimation() {
		
	}

	private Material buildMaterial(EditorMaterial mat) {
		Material material = getLightMaterial();
		material.setBoolean("UseMaterialColors", true);

		float alpha = 1;
		if (mat.transparency != null) {
			alpha = 1-mat.transparency;
		}
		
		if (mat.ambientColor != null) {
			mat.ambientColor.a = alpha;
			material.setColor("Ambient", mat.ambientColor);
		}

		if (mat.diffuseColor != null) {
			mat.diffuseColor.a = alpha;
			material.setColor("Diffuse", mat.diffuseColor);
		}

		if (mat.specularColor != null) {
			mat.specularColor.a = alpha;
			material.setColor("Specular", mat.specularColor);
		}

		if (mat.diffuseMap != null) {
			Texture texture = createTexture(mat.diffuseMap);
			if (texture != null)
				material.setTexture("DiffuseMap", texture);
		}

		if (mat.twoSided != null) // Just being there is equivalent to a boolean
									// true.
		{
			RenderState rs = material.getAdditionalRenderState();
			rs.setFaceCullMode(RenderState.FaceCullMode.Off);// twoside
		}

		if (mat.shininess != null) {
			float shine = mat.shininess.floatValue() * 128f;
			material.setFloat("Shininess", shine);
		}

		if (mat.illuminated != null && mat.illuminated.booleanValue() == true) {
			material.setColor("Emissive", mat.ambientColor);
		}

		material.setName(mat.name);

		return material;
	}

	private Geometry buildGeometry(EditorObject object) {
		Geometry geom = new Geometry(object.name);
		Mesh mesh = new Mesh();
		geom.setMesh(mesh);

		// Vertex
		mesh.setBuffer(Type.Position, 3,
				BufferUtils.createFloatBuffer(object.vertexs));

		if (object.indices != null) {
			// Faces
			mesh.setBuffer(Type.Index, 3,
					BufferUtils.createIntBuffer(object.indices));
			// Vertex Normals
			// ignored smoothGroups for now
			Vector3f[] normals = generateNormals(object.vertexs, object.indices);
			mesh.setBuffer(Type.Normal, 3,
					BufferUtils.createFloatBuffer(normals));
		}

		if (object.texCoord != null) {
			// Texture Coord
			mesh.setBuffer(Type.TexCoord, 2,
					BufferUtils.createFloatBuffer(object.texCoord));
		}
		mesh.setStatic();
		mesh.updateBound();
		mesh.updateCounts();

		return geom;
	}
	
	/**
	 * Generates normals for each vertex of each face that are absolutely normal
	 * to the face.
	 * 
	 * @param point0
	 *            The first point of the face
	 * @param point1
	 *            The second point of the face
	 * @param point2
	 *            The third point of the face
	 * @return the three normals that should be used for the triangle
	 *         represented by the parameters.
	 */
	private Vector3f[] generateNormals(Vector3f points[], int[] indices) {
		Vector3f[] normals = new Vector3f[points.length];
		for (int i = 0; i < indices.length; i += 3) {
			int index0 = indices[i];
			int index1 = indices[i + 1];
			int index2 = indices[i + 2];

			Vector3f v1 = points[index1].subtract(points[index0]);
			Vector3f v2 = points[index2].subtract(points[index0]);
			Vector3f normal = v1.cross(v2);
			normal.normalize();

			if (normals[index0] == null) {
				normals[index0] = normal;
			} else {
				normals[index0].addLocal(normal);
			}

			if (normals[index1] == null) {
				normals[index1] = normal;
			} else {
				normals[index1].addLocal(normal);
			}

			if (normals[index2] == null) {
				normals[index2] = normal;
			} else {
				normals[index2].addLocal(normal);
			}
		}

		for (int i = 0; i < normals.length; i++) {
			if (normals[i] != null) {
				normals[i].normalize();
			}
		}
		return normals;
	}
	
	private void applyMaterial(Geometry geom, EditorObject obj) {
		// No materials
		if (obj.matNames == null) {
			geom.setMaterial(getDefaultMaterial());
			return;
		}
		
		// only one material
		if (obj.matNames.size() == 1) {
			String matName = obj.matNames.get(0);
			Material material = materials.get(matName);
			geom.setMaterial(material);
		}
		
		// multiplier materials
		if (obj.matNames.size() > 1) {
			// Currently I do it like this..
			System.out.println(obj.name + " has multiplier materials.");
			String matName = obj.matNames.get(0);
			Material material = materials.get(matName);
			geom.setMaterial(material);
		}
	}

	/**
	 * Retrieves the named object for the each key framer inserts the rotation,
	 * position and pivot transformations for frame 0 and assigns the coordinate
	 * system to it.
	 * 
	 * The inverse of the local coordinate system converts from 3ds
	 * semi-absolute coordinates (what is in the file) to local coordinates.
	 * 
	 * Then these local coordinates are converted with matrix that will
	 * instantiate them to absolute coordinates: Xabs = sx a1 (Xl-Px) + sy a2
	 * (Yl-Py) + sz a3 (Zl-Pz) + Tx Yabs = sx b1 (Xl-Px) + sy b2 (Yl-Py) + sz b3
	 * (Zl-Pz) + Ty Zabs = sx c1 (Xl-Px) + sy c2 (Yl-Py) + sz c3 (Zl-Pz) + Tz
	 * Where: (Xabs,Yabs,Zabs) = absolute coordinate (Px,Py,Pz) = mesh pivot
	 * (constant) (X1,Y1,Z1) = local coordinates
	 * 
	 */
	private void reCalculateVertex(Skeleton ske) {

		for (KeyFrameTrack track : scene.frames) {
			String name = track.name;

		}
	}

	/**
	 * Find the Geometry's pivot by it's name
	 * 
	 * @param name
	 * @return
	 */
	private Vector3f findPivot(String name) {
		Vector3f rVal = null;

		if (scene.frames == null)
			return new Vector3f();

		for (KeyFrameTrack track : scene.frames) {
			if (track.name.equals(name)) {
				rVal = track.pivot;
				break;
			}
		}

		if (rVal == null)
			rVal = new Vector3f();

		return rVal;
	}

	private void reCalculate(Geometry geom, Matrix4f coordinateSystem,
			Vector3f pivot) {
		Vector3f tmp = new Vector3f();
		Matrix4f coordinateTransform = new Matrix4f(coordinateSystem)
				.invertLocal();

		// get vertex data
		Mesh mesh = geom.getMesh();
		FloatBuffer fb = (FloatBuffer) mesh.getBuffer(Type.Position).getData();
		fb.flip();// ready to read

		// recalculate vertex
		int limit = fb.limit();
		for (int i = 0; i < limit; i += 3) {
			tmp.x = fb.get();
			tmp.y = fb.get();
			tmp.z = fb.get();

			tmp = coordinateTransform.mult(tmp);
			tmp.subtractLocal(pivot);

			fb.put(i, tmp.x);
			fb.put(i + 1, tmp.y);
			fb.put(i + 2, tmp.z);
		}
	}

	/**
	 * Create Animation !
	 * 
	 */
	private void createAnimation() {
		System.out.printf("start: %d stop:%d\n", scene.startFrame,
				scene.stopFrame);

		if (scene.stopFrame == 0 || scene.frames == null)
			return;

		/** add controls to the model */
		AnimControl ac = createAnimControl();
		rootNode.addControl(ac);

		Skeleton ske = ac.getSkeleton();
		SkeletonControl sc = new SkeletonControl(ske);
		rootNode.addControl(sc);

		/** recalcualte the vertex coordinate */
		reCalculateVertex(ske);

		/** skinning the model */
		for (Spatial child : rootNode.getChildren()) {
			if (child instanceof Geometry) {
				Geometry geom = (Geometry) child;
				String name = geom.getName();
				skinning(geom.getMesh(), (byte) ske.getBoneIndex(name));
			}
		}
	}

	/**
	 * Create an AnimControl, it contains an Animation with 3 BoneTracks.
	 * 
	 * @return
	 */
	private AnimControl createAnimControl() {

		Skeleton ske = buildSkeleton();

		AnimControl animControl = new AnimControl(ske);

		Animation anim = buildAnimation(ske);

		animControl.addAnim(anim);

		return animControl;
	}

	/**
	 * Create a Skeleton with data of KeyFrameTracks.
	 * 
	 * @return
	 */
	private Skeleton buildSkeleton() {
		int boneSize = scene.frames.size();
		Bone[] bones = new Bone[boneSize];

		for (int i = 0; i < scene.frames.size(); i++) {
			KeyFrameTrack track = scene.frames.get(i);

			bones[track.ID] = new Bone(track.name);
			if (track.fatherID != -1)
				bones[track.fatherID].addChild(bones[track.ID]);

			Vector3f initTranslation = new Vector3f();
			Quaternion initRotation = new Quaternion();
			Vector3f initScale = new Vector3f();

			if (track.locateTrack(0) != null) {
				initTranslation.set(track.locateTrack(0).position);
				initRotation.set(track.locateTrack(0).rotation);
				initScale.set(track.locateTrack(0).scale);
			}
			bones[track.ID].setBindTransforms(initTranslation, initRotation,
					initScale);
			;

		}

		Skeleton skeleton = new Skeleton(bones);

		System.out.println(skeleton);
		return skeleton;
	}

	/**
	 * Create animation
	 * 
	 * @param ske
	 * @return
	 */
	private Animation buildAnimation(Skeleton ske) {
		// Calculate animation length
		float speed = 30f;
		float length = scene.stopFrame / speed;

		Animation anim = new Animation("3DS Animation", length);

		for (KeyFrameTrack track : scene.frames) {
			int targetBoneIndex = ske.getBoneIndex(track.name);

			anim.addTrack(track.toBoneTrack(targetBoneIndex, speed));
		}
		return anim;
	}

	/**
	 * Skinning the mesh
	 * 
	 * @param mesh
	 * @param targetBoneIndex
	 */
	private void skinning(Mesh mesh, byte targetBoneIndex) {
		if (targetBoneIndex == -1)
			return;

		// Calculate vertex count
		int limit = mesh.getBuffer(Type.Position).getData().limit();
		// Notice: i should call mesh.getMode() to decide how many
		// floats is used for each vertex. Default mode is Mode.Triangles
		int vertexCount = limit / 3;// by default

		int boneIndexCount = vertexCount * 4;
		byte[] boneIndex = new byte[boneIndexCount];
		float[] boneWeight = new float[boneIndexCount];

		// calculate bone indices and bone weights;
		for (int i = 0; i < boneIndexCount; i += 4) {
			boneIndex[i] = targetBoneIndex;
			// I don't need the other 3 indices so I discard them
			boneIndex[i + 1] = 0;
			boneIndex[i + 2] = 0;
			boneIndex[i + 3] = 0;

			boneWeight[i] = 1;
			// I don't need the other 3 indices so I discard them
			boneWeight[i + 1] = 0;
			boneWeight[i + 2] = 0;
			boneWeight[i + 3] = 0;
		}
		mesh.setMaxNumWeights(4);

		// apply software skinning
		mesh.setBuffer(Type.BoneIndex, 4, boneIndex);
		mesh.setBuffer(Type.BoneWeight, 4, boneWeight);

		mesh.generateBindPose(true);
	}
	
	
	
	
	
	

	/**
	 * Loads the image to serve as a texture.
	 * 
	 * @param textureImageName
	 *            name of the image that is going to be set to be the texture.
	 */
	private Texture createTexture(String textureImageName) {
		Texture texture = null;
		try {
			texture = manager.loadTexture(key.getFolder() + textureImageName);
			texture.setWrap(WrapMode.Repeat);
		} catch (Exception ex) {
			System.err
					.println("Cannot load texture image "
							+ textureImageName
							+ ". Make sure it is in the directory with the model file. "
							+ "If its a bmp make sure JAI is installed.");

			texture = manager.loadTexture("Common/Textures/MissingTexture.png");
			texture.setWrap(WrapMode.Clamp);
		}
		return texture;
	}

	/**
	 * load a default material
	 * 
	 * @return
	 */
	private Material getDefaultMaterial() {
		if (defaultMaterial == null) {
			defaultMaterial = new Material(manager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			defaultMaterial.setColor("Color", ColorRGBA.Cyan);
		}

		return defaultMaterial;
	}

	/**
	 * load a light material
	 * 
	 * @return
	 */
	private Material getLightMaterial() {
		Material material = new Material(manager,
				"Common/MatDefs/Light/Lighting.j3md");
		material.setColor("Ambient", ColorRGBA.White);
		material.setColor("Diffuse", ColorRGBA.White);
		material.setColor("Specular", ColorRGBA.White);
		material.setColor("GlowColor", ColorRGBA.Black);
		material.setFloat("Shininess", 25f);

		RenderState rs = material.getAdditionalRenderState();
		rs.setAlphaTest(true);
		rs.setAlphaFallOff(0.01f);

		return material;
	}
}
