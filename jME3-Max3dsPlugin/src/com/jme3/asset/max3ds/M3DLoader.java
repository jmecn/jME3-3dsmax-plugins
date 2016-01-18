package com.jme3.asset.max3ds;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.max3ds.anim.KeyframeControl;
import com.jme3.asset.max3ds.data.EditorLight;
import com.jme3.asset.max3ds.data.EditorMaterial;
import com.jme3.asset.max3ds.data.EditorObject;
import com.jme3.asset.max3ds.data.KeyFrame;
import com.jme3.asset.max3ds.data.KeyFrameTrack;
import com.jme3.asset.max3ds.data.Max3dsScene;
import com.jme3.asset.max3ds.data.SpotLightInfo;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
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
	private ArrayList<Spatial> spatialNodes;
	private ArrayList<String> spatialNames;
	private HashMap<Integer, Node> nodesByID;
	private KeyframeControl st;

	/**
	 * Change Max 3DS data structure to JME3 data structure
	 * 
	 * @return
	 */
	private Node buildScene() {
		rootNode = new Node("3DS Scene");

		materials = new HashMap<String, Material>();
		spatialNodes = new ArrayList<Spatial>();
		spatialNames = new ArrayList<String>();

		generateMaterials();
		generateNodes();
		generateLights();
		generateAnimation();

		for (Spatial spatialNode : spatialNodes) {
			if (spatialNode != null) {
				Spatial toAttach = spatialNode;
				if (toAttach.getParent() == null) {
					rootNode.attachChild(toAttach);
				}
			}
		}

		if (scene.frames.size() > 0) {
			st.interpolateMissing();
			st.update(0);
			if (st.keyframes.size() == 1) {
				// one keyframe: update controller once and disregard it
			} else {
				// multiple keyframes: add controller to node
				rootNode.addControl(st);
			}
		}

		return rootNode;
	}

	/**
	 * Create names material maps
	 */
	private void generateMaterials() {
		for (EditorMaterial mat : scene.materials) {
			Material material = buildMaterial(mat);
			materials.put(mat.name, material);
		}
	}

	private void generateNodes() {
		spatialNodes = new ArrayList<Spatial>();
		spatialNames = new ArrayList<String>();
		nodesByID = new HashMap<Integer, Node>();
		// build dummy nodes
		if (scene.frames.size() > 0) {
			for (KeyFrameTrack track : scene.frames) {
				String name = track.name;

				if (scene.findObject(name) == null) {
					Node node = new Node(track.name);
					nodesByID.put(track.ID, node);
					spatialNodes.add(node);
					spatialNames.add(name);
				}
			}
		}

		// build meshes
		for (EditorObject obj : scene.objects) {
			String name = obj.name;
			KeyFrameTrack track = scene.findTrack(name);

			Node node = new Node(name);

			Spatial spatial;
			if (track == null) {
				putChildMeshes(node, obj, new Vector3f());
				spatial = usedSpatial(node);
			} else {
				putChildMeshes(node, obj, track.pivot);
				spatial = node;
				nodesByID.put(track.ID, node);
			}
			spatialNames.add(name);
			spatialNodes.add(spatial);
		}

		// build hierarchy
		if (scene.frames.size() > 0) {
			for (KeyFrameTrack track : scene.frames) {
				if (track.fatherID != -1) {
					Node node = nodesByID.get(track.ID);
					if (node != null) {
						Node parentNode = nodesByID.get(track.fatherID);
						if (parentNode != null) {
							parentNode.attachChild(node);
						} else {
							throw new RuntimeException("Parent node (id="
									+ track.fatherID + ") not found!");
						}
					}
				}
			}
		}
	}

	private Spatial usedSpatial(Node myNode) {
		Spatial spatial;
		if (myNode.getQuantity() == 1) {
			myNode.getChild(0).setName(myNode.getName());
			spatial = myNode.getChild(0);

			myNode.detachChild(spatial);
		} else {
			spatial = myNode;
		}
		return spatial;
	}

	private void generateLights() {
		for (EditorLight light : scene.lights) {
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

				spotLight.setSpotInnerAngle(spotinfo.hotSpot / 180);
				spotLight.setSpotOuterAngle(spotinfo.falloff / 180);

				spotLight.setSpotRange(light.rangeEnd);

				rootNode.addLight(spotLight);
			}

		}
	}

	private Material buildMaterial(EditorMaterial mat) {
		Material material = getLightMaterial();
		material.setBoolean("UseMaterialColors", true);

		float alpha = 1;
		if (mat.transparency != null) {
			alpha = 1 - mat.transparency;
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
		if (mat.bumpMap != null) {
			Texture texture = createTexture(mat.bumpMap);
			if (texture != null)
				material.setTexture("NormalMap", texture);
		}
		if (mat.twoSided != null) // Just being there is equivalent to a boolean true.
		{
			RenderState rs = material.getAdditionalRenderState();
			rs.setFaceCullMode(RenderState.FaceCullMode.Off);// two sided
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

	/**
	 * Build meshes<br>
	 * As each 3ds model my have multi face materials, build several meshes for
	 * each material.
	 * 
	 * @param parentNode
	 * @param object
	 * @param pivotLoc
	 */
	private void putChildMeshes(Node parentNode, EditorObject object,
			Vector3f pivotLoc) {
		if (object.numFaces == 0)
			return;

		int nFaces = object.numFaces;

		boolean[] faceHasMaterial = new boolean[nFaces];
		int noMaterialCount = nFaces;

		// this part works with animation
		if (scene.frames.size() > 0) {
			// recalculate vertex coordinates
			if (object.coordinateSystem == null) {
				object.coordinateSystem = new Matrix4f();
			}
			Matrix4f coordSys = new Matrix4f(object.coordinateSystem);
			coordSys.invertLocal();
			for (Vector3f vertex : object.vertexs) {
				coordSys.mult(vertex, vertex);
				vertex.subtractLocal(pivotLoc);
			}
		}

		// calculate face normals, use it to calculate smooth groups
		Vector3f[] faceNormals = new Vector3f[nFaces];
		calculateFaceNormals(faceNormals, object.vertexs, object.indices);

		// Precaching
		int[] vertexCount = new int[object.vertexs.length];
		for (int i = 0; i < nFaces; i++) {
			for (int j = 0; j < 3; j++) {
				vertexCount[object.indices[i * 3 + j]]++;
			}
		}
		int[][] realNextFaces = new int[object.vertexs.length][];
		for (int i = 0; i < realNextFaces.length; i++) {
			realNextFaces[i] = new int[vertexCount[i]];
		}
		int vertexIndex;
		for (int i = 0; i < nFaces; i++) {
			for (int j = 0; j < 3; j++) {
				vertexIndex = object.indices[i * 3 + j];
				realNextFaces[vertexIndex][--vertexCount[vertexIndex]] = i;
			}
		}
		// Precaching done

		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
		ArrayList<Vector3f> vertexes = new ArrayList<Vector3f>();
		ArrayList<Vector2f> texCoords = new ArrayList<Vector2f>();
		Vector3f tempNormal = new Vector3f();

		int[] indexes = new int[nFaces * 3];
		for (int i = 0; i < object.matNames.size(); i++) {// For every original
															// material
			String matName = object.matNames.get(i);
			int[] appliedFacesIndexes = object.appliedFacesIndexes.get(i);

			if (appliedFacesIndexes.length != 0) { // If it's got something make
													// a new trimesh for it
				Geometry geom = new Geometry(parentNode.getName() + "##" + i);
				normals.clear();
				vertexes.clear();
				texCoords.clear();

				int curPosition = 0;
				for (int j = 0; j < appliedFacesIndexes.length; j++) { // Look
																		// thru
																		// every
																		// face
																		// in
																		// that
																		// new
																		// TriMesh
					int actuallFace = appliedFacesIndexes[j];
					if (!faceHasMaterial[actuallFace]) {
						faceHasMaterial[actuallFace] = true;
						noMaterialCount--;
					}
					for (int k = 0; k < 3; k++) {// and every vertex in that
													// face
						// what faces contain this vertex index? If they do and
						// are in the same SG, average
						vertexIndex = object.indices[actuallFace * 3 + k];
						tempNormal.set(faceNormals[actuallFace]);
						calcFacesWithVertexAndSmoothGroup(
								realNextFaces[vertexIndex], faceNormals,
								object.smoothGroups, tempNormal, actuallFace);
						// Now can I just index this Vertex/tempNormal
						// combination?
						normals.add(new Vector3f(tempNormal));
						vertexes.add(object.vertexs[vertexIndex]);
						if (object.texCoord != null) {
							texCoords.add(object.texCoord[vertexIndex]);
						}
						indexes[curPosition++] = normals.size() - 1;
					}
				}
				Vector3f[] newVerts = new Vector3f[vertexes.size()];
				for (int indexV = 0; indexV < newVerts.length; indexV++) {
					newVerts[indexV] = vertexes.get(indexV);
				}

				Mesh mesh = new Mesh();
				geom.setMesh(mesh);

				// Vertex
				mesh.setBuffer(Type.Position, 3,
						BufferUtils.createFloatBuffer(newVerts));

				// Vertex Normals
				mesh.setBuffer(Type.Normal, 3, BufferUtils
						.createFloatBuffer(normals.toArray(new Vector3f[] {})));

				// Faces
				int[] intIndexes = new int[curPosition];
				System.arraycopy(indexes, 0, intIndexes, 0, curPosition);
				mesh.setBuffer(Type.Index, 3,
						BufferUtils.createIntBuffer(intIndexes));

				if (object.texCoord != null) {
					// Texture Coord
					mesh.setBuffer(Type.TexCoord, 2, BufferUtils
							.createFloatBuffer(texCoords
									.toArray(new Vector2f[] {})));
				}
				mesh.setStatic();
				mesh.updateBound();
				mesh.updateCounts();

				Material material = materials.get(matName);
				if (material == null) {
					geom.setMaterial(getDefaultMaterial());
				} else {
					geom.setMaterial(material);
				}

				parentNode.attachChild(geom);
			}
		}

		if (noMaterialCount != 0) {// attach materialless parts
			int[] noMaterialIndexes = new int[noMaterialCount * 3];
			int partCount = 0;
			for (int i = 0; i < nFaces; i++) {
				if (!faceHasMaterial[i]) {
					noMaterialIndexes[partCount++] = object.indices[i * 3];
					noMaterialIndexes[partCount++] = object.indices[i * 3 + 1];
					noMaterialIndexes[partCount++] = object.indices[i * 3 + 2];
				}
			}
			Geometry noMaterials = new Geometry(parentNode.getName() + "-1");

			Mesh mesh = new Mesh();
			mesh.setBuffer(Type.Position, 3,
					BufferUtils.createFloatBuffer(object.vertexs));
			mesh.setBuffer(Type.Index, 3,
					BufferUtils.createIntBuffer(noMaterialIndexes));
			mesh.setStatic();
			mesh.updateCounts();
			mesh.updateBound();

			noMaterials.setMesh(mesh);
			noMaterials.setMaterial(getDefaultMaterial());
			parentNode.attachChild(noMaterials);
		}
	}

	/**
	 * Calculate face normals
	 * 
	 * @param faceNormals
	 * @param vertexs
	 * @param indices
	 */
	private void calculateFaceNormals(Vector3f[] faceNormals,
			Vector3f[] vertexs, int[] indices) {
		for (int i = 0; i < faceNormals.length; i++) {
			int index0 = indices[i * 3];
			int index1 = indices[i * 3 + 1];
			int index2 = indices[i * 3 + 2];

			Vector3f v1 = vertexs[index1].subtract(vertexs[index0]);
			Vector3f v2 = vertexs[index2].subtract(vertexs[index0]);

			faceNormals[i] = v1.cross(v2).normalize();
		}
	}

	/**
	 * Find all face normals for faces that contain that vertex AND are in that
	 * smoothing group.
	 * 
	 * @param thisVertexTable
	 * @param faceNormals
	 * @param smoothingGroups
	 * @param tempNormal
	 * @param faceIndex
	 */
	private void calcFacesWithVertexAndSmoothGroup(int[] thisVertexTable,
			Vector3f[] faceNormals, int[] smoothingGroups, Vector3f tempNormal,
			int faceIndex) {
		if (smoothingGroups == null) {
			return;// no need to calculate smooth groups
		}
		// tempNormal starts out with the face normal value
		int smoothingGroupValue = smoothingGroups[faceIndex];
		if (smoothingGroupValue == 0)
			return; // 0 smoothing group values don't have smooth edges anywhere
		for (int arrayFace : thisVertexTable) {
			if (arrayFace == faceIndex) {
				continue;
			}
			if ((smoothingGroups[arrayFace] & smoothingGroupValue) != 0) {
				tempNormal.addLocal(faceNormals[arrayFace]);
			}
		}
		tempNormal.normalizeLocal();
	}

	/**
	 * Create Animation !
	 * 
	 */
	private void generateAnimation() {

		if (scene.frames.size() == 0)
			return;
		int spatialCount = 0;
		for (Spatial spatialNode : spatialNodes) {
			if (spatialNode != null) {
				spatialCount++;
			}
		}
		st = new KeyframeControl(spatialCount);
		st.setRepeatType(1);
		spatialCount = 0;
		for (int i = 0; i < spatialNodes.size(); i++) {
			if (spatialNodes.get(i) != null) {
				// hand the Spatial over to the SpatialTransformer
				// the parent ID is not passed here, as that would produce wrong results
				// because of the ST applying hierarchichal transformations, which the
				// scene graph applies anyway
				spatialNodes.get(i).setUserData("#DS_ID", new Integer(spatialCount));
				st.setObject(spatialNodes.get(i), spatialCount++, -1);// getParentIndex(i));
			}
		}
		for (KeyFrameTrack thisOne : scene.frames) {
			Node node = nodesByID.get(thisOne.ID);
			int indexInST = findIndex(node);
			for (KeyFrame thisTime : thisOne.tracks) {
				if (thisTime.rotation != null) {
					st.setRotation(indexInST, thisTime.frame, thisTime.rotation);
				}
				if (thisTime.position != null) {
					st.setPosition(indexInST, thisTime.frame, thisTime.position);
				}
				if (thisTime.scale != null) {
					st.setScale(indexInST, thisTime.frame, thisTime.scale);
				}
			}
		}
		st.setSpeed(30);// 30 FPS
	}

	private int findIndex(Node node) {
		int j = 0;
		for(int i=0; i<spatialNodes.size(); i++) {
			if (spatialNodes.get(i) == node) {
				return j;
			}
			if (spatialNodes.get(i) != null) {
				j++;
			}
		}
		throw new RuntimeException("Logic error.  Unknown keyframed node " + node);
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
			System.err.println("Cannot load texture image " + textureImageName + ". Make sure it is in the directory with the model file. If its a bmp make sure JAI is installed.");
			texture = manager.loadTexture("Common/Textures/MissingTexture.png");
			texture.setWrap(WrapMode.BorderClamp);
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
			defaultMaterial = new Material(manager, "Common/MatDefs/Misc/Unshaded.j3md");
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
		Material material = new Material(manager, "Common/MatDefs/Light/Lighting.j3md");
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
