package com.jme3.asset.max3ds.app;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.max3ds.Max3dsLoader;

public class TestLoad3DS extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		assetManager.registerLoader(Max3dsLoader.class, "3ds");
		assetManager.loadAsset("Model/bounce.3DS");

	}

	public static void main(String[] args) {
		TestLoad3DS app = new TestLoad3DS();
		app.setShowSettings(false);
		app.start();

	}

}
