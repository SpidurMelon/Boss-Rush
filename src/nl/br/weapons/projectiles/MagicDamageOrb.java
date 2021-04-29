package nl.br.weapons.projectiles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import en.lib.pooling.Poolable;

public class MagicDamageOrb extends Projectile {
	
	public MagicDamageOrb(boolean friendly) {
		super(friendly);
		damage = 0.2;
	}

	public BufferedImage getTexture(boolean friendly) {
		try {
			return ImageIO.read(new File("resources/textures/weapons/projectiles/magic/DamageOrb.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void fire(double x, double y, double direction, double speed, double damage) {
		super.fire(x, y, direction, speed);
		this.damage = damage;
	}

	public void onWallHit() {
		inAir = false;
		disable();
	}

	public void onEntityHit() {
		inAir = false;
		disable();
	}

	@Override
	public Poolable createNew() {
		return new MagicDamageOrb(friendly);
	}
	
}
