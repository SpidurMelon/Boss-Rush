package nl.br.weapons.projectiles;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import en.lib.math.MathUtils;
import en.lib.pooling.Poolable;
import en.lib.sounds.AudioMaster;
import nl.br.panels.DrawPanel;

public abstract class MagicOrb extends Projectile {
	public Point goal = new Point();
	public MagicOrb(boolean friendly) {
		super(friendly);
		setScale(2);
	}
	
	public void tick(double delta) {
		super.tick(delta);
		if (MathUtils.distPoints(getCenter(), goal) < movement.getSize()) {
			explode();
		}
	}

	public BufferedImage getTexture(boolean friendly) {
		try {
			return ImageIO.read(new File("resources/textures/weapons/projectiles/magic/Orb.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void fire(double x, double y, double direction, double speed, int goalX, int goalY) {
		super.fire(x, y, direction, speed);
		this.goal.setLocation(goalX, goalY);
	}
	
	public void onWallHit() {
		explode();
	}

	public void onEntityHit() {
		
	}
	
	private static int twinkleSound = AudioMaster.loadSound("resources/sounds/Magic/Twinkle.wav");
	public void explode() {
		if (inAir) {
			AudioMaster.playSound(twinkleSound, 1, (float)(1*DrawPanel.deltaMultiplier));
			explodeEffect();
			disable();
		}
	}
	
	public abstract void explodeEffect();
	
	public Poolable createNew() {
		return new MagicOrb(friendly) {
			public void explodeEffect() {
				
			}
		};
	}
	
}
