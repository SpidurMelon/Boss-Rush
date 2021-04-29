package nl.br.map;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;

import en.lib.drawing.Animation;
import en.lib.drawing.SpriteMap;
import nl.br.map.floorfilling.props.AbstractProp;
import nl.br.map.floorfilling.props.Prop;

public class MapUtils {
	public static final int blockResolution = 20;
	private static final String textureFolder = "resources/textures/blocks/";
	private static HashMap<Integer, SpriteMap> spriteMaps = new HashMap<Integer, SpriteMap>();
	public static SpriteMap getSpriteMapByType(int type) {
		if (spriteMaps.containsKey(type)) {
			return spriteMaps.get(type);
		} else {
			if (type == 1) {
				spriteMaps.put(type, new SpriteMap(blockResolution, blockResolution, 0, new File(textureFolder + "TestBlock.png")));
			} else if (type == 2) {
				spriteMaps.put(type, new SpriteMap(blockResolution, blockResolution, 0, new File(textureFolder + "Garden.png")));
			} else if (type == 3) {
				spriteMaps.put(type, new SpriteMap(blockResolution, blockResolution, 0, new File(textureFolder + "Quartz.png")));
			}
			return spriteMaps.get(type);
		}
	}
	
	private static final String propFolder = "resources/textures/props/";
	private static HashMap<Integer, AbstractProp> props = new HashMap<Integer, AbstractProp>();
	public static Prop getPropByType(int x, int y, int type) {
		if (props.containsKey(type)) {
			return new Prop(x, y, props.get(type));
		} else {
			if (type == 1) {
				SpriteMap torchSM = new SpriteMap(20, 20, 0, new File(propFolder + "Torch.png"));
				Animation torchAnimation = new Animation(torchSM, 0, 0, torchSM.getWidth()-1, 0, 5, true);
				AbstractProp torch = new AbstractProp(40, 40, 100000, torchAnimation, new Point[]{new Point(20, 14)});
				props.put(type, torch);
			}
			if (type == 2) {
				SpriteMap spawnPlatformSM = new SpriteMap(240, 240, 0, new File(propFolder + "SpawnPlatform.png"));
				Animation spawnPlatformAnimation = new Animation(spawnPlatformSM, 0, 0, spawnPlatformSM.getWidth()-1, 0, 50, true);
				AbstractProp spawnPlatform = new AbstractProp(480, 480, -100000, spawnPlatformAnimation, null);
				props.put(type, spawnPlatform);
			}
			return new Prop(x, y, props.get(type));
		}
	}
}
