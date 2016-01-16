package com.jme3.asset.max3ds.app;

import java.util.ArrayList;
import java.util.List;

import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.max3ds.M3DLoader;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;

public class TestLoad3DS extends SimpleApplication {

	// Lights
	private AmbientLight ambient;
	private DirectionalLight sun;
	
	// Models
	private List<TestLoader> list;
	private int index = 0;
	private Spatial current;
	
	// Debug
	private Spatial axisNode;
	
	@Override
	public void simpleInitApp() {
		assetManager.registerLoader(M3DLoader.class, "3ds");
		
		index = 0;
		initModelList();
		loadModel();
		
		initGui();
		initCamera();
		initLight();
		initShadow();
		initKeys();
		initViewPort();
		axisNode = showNodeAxies(50f);
	}
	
	private void initGui() {
		String txtB = "KeyPress:\n[J] [K]: Load model.\n[U] [I]: Rotate model.\n[O] [P]: Scale model.\n[F1]: turn on/off wireframe.\n[F2]: turn on/off axis.";
		BitmapText txt;
		BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
        txt = new BitmapText(fnt, false);
        txt.setBox(new Rectangle(0, 0, settings.getWidth(), settings.getHeight()));
        txt.setText(txtB);
        txt.setLocalTranslation(0, txt.getHeight(), 0);
        guiNode.attachChild(txt);
		
	}
	private void initCamera() {
		cam.setLocation(new Vector3f(100, 80, 100));
		cam.lookAt(Vector3f.ZERO, cam.getUp());
		this.flyCam.setMoveSpeed(100f);
	}
	
	/**
	 * Initialize the light
	 */
	private void initLight() {
		// Ambient light
		ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(1.3f));
		rootNode.addLight(ambient);
		
		// Sun
		sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
		rootNode.addLight(sun);
	}
	
	private void initShadow() {
		/* Drop shadows */
		rootNode.setShadowMode(ShadowMode.CastAndReceive);
		
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 4);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
        
	}
	
	private void initViewPort() {
		viewPort.setBackgroundColor(new ColorRGBA(0.3f, 0.4f, 0.5f, 1));
	}
	
	public Spatial showNodeAxies(float axisLen) {
		Node rootNode = new Node();
		Mesh mesh = new Grid(31, 31, 4f);
		Geometry grid = new Geometry("Axis", mesh);
		Material gm = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		gm.setColor("Color", ColorRGBA.White);
		gm.getAdditionalRenderState().setWireframe(true);
		grid.setMaterial(gm);
		grid.center().move(0, 0, 0);

		rootNode.attachChild(grid);

		//
		Vector3f v = new Vector3f(axisLen, 0, 0);
		Arrow a = new Arrow(v);
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Red);
		Geometry geom = new Geometry(rootNode.getName() + "XAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		//
		v = new Vector3f(0, axisLen, 0);
		a = new Arrow(v);
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Green);
		geom = new Geometry(rootNode.getName() + "YAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		//
		v = new Vector3f(0, 0, axisLen);
		a = new Arrow(v);
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom = new Geometry(rootNode.getName() + "ZAxis", a);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
		
		return rootNode;
	}
	
	private void initKeys() {
		inputManager.addMapping("wireFrame", new KeyTrigger(KeyInput.KEY_F1));
		inputManager.addMapping("showAxis", new KeyTrigger(KeyInput.KEY_F2));
		inputManager.addMapping("nextModel", new KeyTrigger(KeyInput.KEY_J));
		inputManager.addMapping("preModel", new KeyTrigger(KeyInput.KEY_K));
		inputManager.addMapping("rotateP", new KeyTrigger(KeyInput.KEY_U));
		inputManager.addMapping("rotateN", new KeyTrigger(KeyInput.KEY_I));
		inputManager.addMapping("scaleP", new KeyTrigger(KeyInput.KEY_O));
		inputManager.addMapping("scaleN", new KeyTrigger(KeyInput.KEY_P));
		inputManager.addListener(new ActionListener(){
			@Override
			public void onAction(String name, boolean isPressed, float tpf) {
				if (isPressed) {
					switch (name) {
					case "wireFrame":
						wireFrameFlag = !wireFrameFlag;
						toggleWireFrame();
						break;
					case "showAxis":
						if (rootNode.hasChild(axisNode)) {
							rootNode.detachChild(axisNode);
						} else {
							rootNode.attachChild(axisNode);
						}
						break;
					case "nextModel":
						index = index==list.size()-1?0:index+1;
						loadModel();
						break;
					case "preModel":
						index = index==0?list.size()-1:index-1;
						loadModel();
						break;
					case "rotateP":
						if (current != null) {
							current.rotate(0, FastMath.QUARTER_PI/3, 0);
						}
						break;
					case "rotateN":
						if (current != null) {
							current.rotate(0, -FastMath.QUARTER_PI/3, 0);
						}
						break;
					case "scaleP":
						if (current != null) {
							current.scale(1.33333333333333f);
						}
						break;
					case "scaleN":
						if (current != null) {
							current.scale(0.75f);
						}
						break;
					}
				}
				
			}}, "wireFrame", "showAxis", "nextModel", "preModel", "rotateP", "rotateN", "scaleP", "scaleN");
	}
	
	boolean wireFrameFlag = false;
	
	private void toggleWireFrame() {
		if (current != null) {
			current.depthFirstTraversal(new SceneGraphVisitor() {
				@Override
				public void visit(Spatial spatial) {
					if (spatial instanceof Geometry) {
						Geometry geom = (Geometry)spatial;
						Material material = geom.getMaterial();
						if (material != null) {
							RenderState rs = material.getAdditionalRenderState();
							rs.setWireframe(wireFrameFlag);
						}
					}
				}
			});
		}
	}
	
	void loadModel() {
		if (current != null) rootNode.detachChild(current);
		TestLoader tl = list.get(index);
		Spatial model = tl.load();
		rootNode.attachChild(model);
		current = model;
		
		toggleWireFrame();
		
		startAnim();
	}
	
	void startAnim() {
		AnimControl ac = current.getControl(AnimControl.class);
		if (ac != null) {
			try {
			ac.createChannel().setAnim("3DS Animation");
			} catch (Exception e) {
				
			}
			
			SkeletonDebugger sd = new SkeletonDebugger("SkeletonDebuger", ac.getSkeleton());
			final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", ColorRGBA.Magenta);
			mat.getAdditionalRenderState().setDepthTest(false);
			sd.setMaterial(mat);
			((Node)current).attachChild(sd);
		}
	}
	
	public static void main(String[] args) {
		TestLoad3DS app = new TestLoad3DS();
		app.start();
	}

	/**
	 * Use for test loading model
	 * @author yanmaoyuan
	 *
	 */
	abstract static class TestLoader {
		String name;
		TestLoader(String name) {
			this.name = name;
		}
		abstract Spatial load();
	}
	void initModelList() {
		list = new ArrayList<TestLoader>();
		list.add(new TestLoader("X-Wing") {
			Spatial load() {
				return assetManager.loadModel("Model/XWing/x-wing.3DS");
			}
		});
		list.add(new TestLoader("Bounce") {
			Spatial load() {
				return assetManager.loadModel("Model/bounce.3DS");
			}
		});
		list.add(new TestLoader("Char") {
			Spatial load() {
				Node model = (Node)assetManager.loadModel("Model/Examples/char.3ds");
				return model;
			}
		});
		list.add(new TestLoader("Bike") {
			Spatial load() {
				return assetManager.loadModel("Model/Examples/bike.3ds");
			}
		});
		list.add(new TestLoader("Book") {
			Spatial load() {
				return assetManager.loadModel("Model/Examples/Books.3DS");
			}
		});
		list.add(new TestLoader("DeathKnight") {
			Spatial load() {
				return assetManager.loadModel("Model/Examples/dk.3DS");
			}
		});
		list.add(new TestLoader("Dolphin") {
			Spatial load() {
				return assetManager.loadModel("Model/Examples/Dolphin 1.3ds");
			}
		});
		list.add(new TestLoader("Manikin") {
			Spatial load() {
				return assetManager.loadModel("Model/Examples/Manikin-5.3DS");
			}
		});
		list.add(new TestLoader("Woman01") {
			Spatial load() {
				return assetManager.loadModel("Model/Examples/Woman.3ds");
			}
		});
		list.add(new TestLoader("Woman02") {
			Spatial load() {
				return assetManager.loadModel("Model/Examples/Woman2.3ds");
			}
		});
		list.add(new TestLoader("ledy") {
			Spatial load() {
				Node ledy = (Node)assetManager.loadModel("Model/Examples/ledy-2.3DS");
				ledy.move(0, 0, 0);
				return ledy;
			}
		});
		list.add(new TestLoader("Girl") {
			Spatial load() {
				Node girl = (Node)assetManager.loadModel("Model/Examples/Girl N171207.3ds");
				girl.scale(10f);
				return girl;
			}
		});
		list.add(new TestLoader("Ostrich") {
			Spatial load() {
				Node ostrich = (Node)assetManager.loadModel("Model/Examples/Ostrich.3ds");
				ostrich.depthFirstTraversal(new SceneGraphVisitor() {
					@Override
					public void visit(Spatial spatial) {
						if (spatial instanceof Geometry && spatial.getName().equals("ostrich")) {
							// I don't understand the one who made this model don't give it a texture
							// so I add it my self
							Material mat = ((Geometry)spatial).getMaterial();
							Texture tex = assetManager.loadTexture("Model/Examples/ostrich.jpg");
							mat.setTexture("DiffuseMap", tex);
						}
					}
				});
				return ostrich;
			}
		});
		list.add(new TestLoader("fighter") {
			Spatial load() {
				Node fighter = (Node)assetManager.loadModel("Model/Resources/fighter.3ds");
				fighter.depthFirstTraversal(new SceneGraphVisitor() {
					@Override
					public void visit(Spatial spatial) {
						if (spatial instanceof Geometry && spatial.getName().equals("Box01")) {
							Material mat = ((Geometry)spatial).getMaterial();
							mat.setTexture("DiffuseMap", assetManager.loadTexture("Model/Resources/fighter.png"));
						}
					}
				});
				return fighter;
			}
		});
		list.add(new TestLoader("frigate") {
			Spatial load() {
				return assetManager.loadModel("Model/Resources/frigate.3ds");
			}
		});
		list.add(new TestLoader("ship") {
			Spatial load() {
				Node ship = (Node)assetManager.loadModel("Model/Resources/ship.3ds");
				ship.depthFirstTraversal(new SceneGraphVisitor() {
					@Override
					public void visit(Spatial spatial) {
						if (spatial instanceof Geometry && spatial.getName().equals("Cylinder06")) {
							Material mat = ((Geometry)spatial).getMaterial();
							mat.setTexture("DiffuseMap", assetManager.loadTexture("Model/Resources/ship.png"));
						}
					}
				});
				return ship;
			}
		});
		list.add(new TestLoader("stall") {
			Spatial load() {
				Node stall = (Node)assetManager.loadModel("Model/Resources/stall.3ds");
				stall.depthFirstTraversal(new SceneGraphVisitor() {
					@Override
					public void visit(Spatial spatial) {
						if (spatial instanceof Geometry && spatial.getName().equals("Cube.001")) {
							Material mat = ((Geometry)spatial).getMaterial();
							mat.setTexture("DiffuseMap", assetManager.loadTexture("Model/Resources/stall.png"));
						}
					}
				});
				return stall;
			}
		});
	}
}
