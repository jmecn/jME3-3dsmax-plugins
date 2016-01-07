# jME3-3dsmax-plugins
Plugins to import 3damax models into jME3.

Im working on this project aim to support 3 kind of 3DSMAX file format:
(1).ase (67% finished)
(2).3ds (44% finished)
(3).max (0% not started)

It can load meshes and materials, but animation cant work well.
Some kind of material may not work rightly.


== How to use ==

import com.jme3.app.SimpleApplication;
import com.jme3.asset.max3ds.M3DLoader;
import com.jme3.asset.maxase.AseLoader;

import com.jme3.scene.Node;

public class TestLoad3DS extends SimpleApplication {

	@Override
	public void simpleInitApp() {
	  assetManager.registerLoader(M3DLoader.class, "3ds");
	  // assetManager.registerLoader(AseLoader.class, "ase");
		Node model = (Node)assetManager.loadModel("Model/bounce.3DS");
		rootNode.attachChild(model);
		
	}
}

== About M3DLoader ==

I rewrite 3DSLoader, migrating it from java3d to jME3. Here is the article i read during writing this plugin.

(1) 3D-Studio File Format (.3ds)
Author: Martin van Velsen (email: vvelsen@ronix.ptf.hro.nl )
Site: http://www.martinreddy.net/gfx/3d/3DS.spec
(2) 3DSLoader for Java3D
Site:http://sourceforge.net/projects/java3dsloader/

== License == 

GNU Library or Lesser General Public License version 2.0 (LGPLv2)
