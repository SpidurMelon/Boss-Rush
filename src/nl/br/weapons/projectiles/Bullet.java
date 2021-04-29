package nl.br.weapons.projectiles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import en.lib.objects.Tickable;
import en.lib.pooling.Poolable;

public class Bullet extends Projectile {
	private boolean enabled = false;
	public Bullet(boolean friendly) {
		super(friendly);
	}

	public BufferedImage getTexture(boolean friendly) {
		try {
			if (friendly) {
				return ImageIO.read(new File("resources/textures/weapons/projectiles/bullets/Bullet.png"));
			} else {
				return ImageIO.read(new File("resources/textures/weapons/projectiles/bullets/Bullet.png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		};
		return null;
	}

	public void onWallHit() {
		inAir = false;
		disable();
	}

	public void onEntityHit() {
		inAir = false;
		disable();
	}

	public Poolable createNew() {
		return new Bullet(friendly);
	}
}
