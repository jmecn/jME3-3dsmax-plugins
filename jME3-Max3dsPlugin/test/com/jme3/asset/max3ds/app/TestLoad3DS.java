package com.jme3.asset.max3ds.app;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.max3ds.M3DLoader;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;

public class TestLoad3DS extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		cam.setLocation(new Vector3f(100, 80, 100));
		cam.lookAt(Vector3f.ZERO, cam.getUp());
		this.flyCam.setMoveSpeed(100f);
		
		assetManager.registerLoader(M3DLoader.class, "3ds");
		Node model = (Node)assetManager.loadModel("Model/bounce.3DS");
//		Node model = (Node)assetManager.loadModel("Model/Books/Books.3DS");
		Node dk = (Node)assetManager.loadModel("Model/DeathKnight/dk.3DS");
		rootNode.attachChild(model);
		rootNode.attachChild(dk);
		
		rootNode.setShadowMode(ShadowMode.CastAndReceive);
		initLight();
		
	}

	private void initLight() {
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(1.3f));
		rootNode.addLight(al);
		
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
		rootNode.addLight(sun);
		
        /* Drop shadows */
        final int SHADOWMAP_SIZE=1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(sun);
        viewPort.addProcessor(dlsr);
 
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(sun);
        dlsf.setEnabled(true);
        fpp.addFilter(dlsf);
        
        SSAOFilter ssaoFilter = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
        fpp.addFilter(ssaoFilter);

        viewPort.addProcessor(fpp);
	}
	public static void main(String[] args) {
		TestLoad3DS app = new TestLoad3DS();
		app.start();
//		app.start(Type.Headless);

	}

}
