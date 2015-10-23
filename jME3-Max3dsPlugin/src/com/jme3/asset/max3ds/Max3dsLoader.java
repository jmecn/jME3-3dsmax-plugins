package com.jme3.asset.max3ds;

import java.io.IOException;
import java.io.InputStream;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.microcrowd.loader.java3d.max3ds.Loader3DS;

/**
 * Max 3ds loader for jME3
 * 
 * @author yanmaoyuan
 *
 */
public class Max3dsLoader extends Loader3DS implements AssetLoader {

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		AssetKey key = assetInfo.getKey();
		this.setBasePath(key.getFolder());
		
		InputStream inputStream = assetInfo.openStream();

		// TODO it may be wrong
		int modelSize = inputStream.available();
		
		
		// TODO just test if I can use Loader3D in jME3. I think I will change the Loader to jME3 later.
		return parseChunks(inputStream, modelSize);
	}
}
