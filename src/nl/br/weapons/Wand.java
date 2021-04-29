package nl.br.weapons;

import java.awt.event.MouseEvent;
import java.io.File;

import en.lib.drawing.Animation;
import en.lib.drawing.SpriteMap;
import en.lib.math.Vector;
import en.lib.pooling.PoolMaster;
import en.lib.sounds.AudioMaster;
import nl.br.entities.MoveableEntity;
import nl.br.panels.DrawPanel;
import nl.br.weapons.projectiles.MagicDamageOrb;
import nl.br.weapons.projectiles.MagicOrb;

public class Wand extends Weapon {
	
	public MagicOrb orb = new MagicOrb(true) {
		public void explodeEffect() {
			inAir = false;
			for (int i = 0; i < 360; i+=10) {
				((MagicDamageOrb)PoolMaster.getObject("friendlyMagic")).fire(orb.getCenter().x, orb.getCenter().y, i, 10);
			}
		}
	};
	
	protected double delayTimer = 0, delayTime = 60;
	
	protected Animation disabledAnimation = new Animation(new SpriteMap(15, 7, 0, new File("resources/textures/weapons/WandDisabled.png")), 0, 0, 0, 0, 99999, true);
	
	public Wand(MoveableEntity holder) {
		super(holder);
		DrawPanel.addDrawable(orb);
	}
	
	public void tick(double delta) {
		super.tick(delta);
		orb.tick(delta);
		if (delayTimer > 0) {
			delayTimer-=delta;
		} else {
			switchAnimation(restAnimation);
			delayTimer = 0;
		}
	}

	protected SpriteMap getSpriteMap() {
		return new SpriteMap(15, 7, 0, new File("resources/textures/weapons/WandEnabled.png"));
	}

	protected Animation getRestAnimation() {
		return new Animation(textureMap, 0, 0, 0, 0, 99999, true);
	}

	private static int magicMissileSound = AudioMaster.loadSound("resources/sounds/Magic/Magic missile.wav");
	public void mousePressed(MouseEvent m) {
		if (!orb.isActive() && delayTimer <= 0) {
			AudioMaster.playSound(magicMissileSound, 1, (float)(1*DrawPanel.deltaMultiplier));
			orb.fire(getCenter().x, getCenter().y, Vector.getVectorDirection(getCenter(), DrawPanel.relMousePoint), 10, DrawPanel.relMousePoint.x, DrawPanel.relMousePoint.y);
			switchAnimation(disabledAnimation);
			delayTimer = delayTime;
		}
	}

	public void mouseReleased(MouseEvent m) {
		
	}

	public void reset() {
		orb.explode();
	}
	
}
