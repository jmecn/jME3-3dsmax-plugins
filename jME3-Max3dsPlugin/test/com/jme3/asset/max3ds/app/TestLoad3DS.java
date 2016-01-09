package com.jme3.asset.max3ds.app;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.max3ds.M3DLoader;
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
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.texture.Texture;

public class TestLoad3DS extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		assetManager.registerLoader(M3DLoader.class, "3ds");
		
		cam.setLocation(new Vector3f(100, 80, 100));
		cam.lookAt(Vector3f.ZERO, cam.getUp());
		this.flyCam.setMoveSpeed(100f);
		
//		Node model = (Node)assetManager.loadModel("Model/bounce.3DS");
//		rootNode.attachChild(model);
		
//		Node book = (Node)assetManager.loadModel("Model/Examples/Books.3DS");
//		rootNode.attachChild(book);
		
//		Node dk = (Node)assetManager.loadModel("Model/Examples/dk.3DS");
//		rootNode.attachChild(dk);
		
//		Node Dolphin = (Node)assetManager.loadModel("Model/Examples/Dolphin 1.3ds");
//		rootNode.attachChild(Dolphin);
		
//		Node manikin = (Node)assetManager.loadModel("Model/Examples/Manikin-5.3DS");
//		manikin.rotate(0, FastMath.QUARTER_PI, 0);
//		rootNode.attachChild(manikin);
		
//		Node woman = (Node)assetManager.loadModel("Model/Examples/Woman.3ds");
//		woman.scale(0.1f);
//		rootNode.attachChild(woman);
		
//		Node woman2 = (Node)assetManager.loadModel("Model/Examples/Woman2.3ds");
//		rootNode.attachChild(woman2);
		
//		Node ledy = (Node)assetManager.loadModel("Model/Examples/ledy-2.3DS");
//		rootNode.attachChild(ledy);
		
//		Node girl = (Node)assetManager.loadModel("Model/Examples/Girl N171207.3ds");
//		girl.scale(100);
//		rootNode.attachChild(girl);
		
		Node ostrich = (Node)assetManager.loadModel("Model/Examples/Ostrich.3ds");
		ostrich.depthFirstTraversal(new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				if (spatial instanceof Geometry) {
					if (spatial.getName().equals("ostrich"))
					{
						// I don't understand the one who made this model don't give it a texture
						// so I add it my self
						Material mat = ((Geometry)spatial).getMaterial();
						Texture tex = assetManager.loadTexture("Model/Examples/ostrich.jpg");
						mat.setTexture("DiffuseMap", tex);
					}
				}
				
			}
		});
		ostrich.scale(20);
		rootNode.attachChild(ostrich);
		
		initLight();
		initSunAndShadow();
		initKeys();
		
		viewPort.setBackgroundColor(new ColorRGBA(0.3f, 0.4f, 0.5f, 1));
	}

	private void initLight() {
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(1.3f));
		rootNode.addLight(al);
	}
	
	private void initSunAndShadow() {
		rootNode.setShadowMode(ShadowMode.CastAndReceive);
		
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
		rootNode.addLight(sun);
		
        /* Drop shadows */
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 4);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        
        SSAOFilter ssaoFilter = new SSAOFilter();
        fpp.addFilter(ssaoFilter);

        viewPort.addProcessor(fpp);
	}
	
	private void initKeys() {
		inputManager.addMapping("wireFrame", new KeyTrigger(KeyInput.KEY_F1));
		inputManager.addListener(new ActionListener(){
			@Override
			public void onAction(String name, boolean isPressed, float tpf) {
				// TODO Auto-generated method stub
				if (isPressed) {
					if (name.equals("wireFrame")) {
						toggleWireFrame();
					}
				}
				
			}}, "wireFrame");
	}
	
	boolean wireFrameFlag = false;
	
	private void toggleWireFrame() {
		wireFrameFlag = !wireFrameFlag;
		
		rootNode.depthFirstTraversal(new SceneGraphVisitor() {
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
	
	public static void main(String[] args) {
		TestLoad3DS app = new TestLoad3DS();
		app.start();
	}

}
