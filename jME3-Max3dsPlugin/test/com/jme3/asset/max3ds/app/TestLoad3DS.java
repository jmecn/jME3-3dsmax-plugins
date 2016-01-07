package com.jme3.asset.max3ds.app;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.max3ds.M3DLoader;
import com.jme3.light.AmbientLight;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext.Type;

public class TestLoad3DS extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		assetManager.registerLoader(M3DLoader.class, "3ds");
		Node model = (Node)assetManager.loadModel("Model/bounce.3DS");
//		Node model = (Node)assetManager.loadModel("Model/DeathKnight/dk.3DS");
		model.depthFirstTraversal(new SceneGraphVisitor() {
			@Override
			public void visit(Spatial spatial) {
				System.out.println(spatial);
			}
		});
		
		rootNode.attachChild(model);
		rootNode.addLight(new AmbientLight());
	}

	public static void main(String[] args) {
		TestLoad3DS app = new TestLoad3DS();
		app.start(Type.Headless);

	}

}
