package nl.br.weapons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;

import en.lib.drawing.Animation;
import en.lib.drawing.SpriteMap;
import en.lib.math.MathUtils;
import en.lib.math.Vector;
import en.lib.pooling.PoolMaster;
import en.lib.sounds.AudioMaster;
import nl.br.entities.MoveableEntity;
import nl.br.panels.DrawPanel;
import nl.br.weapons.projectiles.Bullet;

public class Gun extends Weapon {
	
	private int xShift = 15, yShift = -3;
	Point bulletStart = new Point();
	
	Vector aimVector = new Vector();
	private double recoil = 0, recoilRecovery = 2;
	
	public Gun(MoveableEntity holder) {
		super(holder);
	}
	
	public void tick(double delta) {
		super.tick(delta);
		
		if (recoil > recoilRecovery) {
			recoil -= recoilRecovery*delta;
		} else if (recoil < recoilRecovery) {
			recoil = 0;
		}
		
		if (onLeft()) {
			setRotation(getRotation()+recoil);
			bulletStart.setLocation(getCenterDouble().x+(xShift*Math.cos(Math.toRadians(getRotation())))+(yShift*Math.sin(Math.toRadians(getRotation()))),
									getCenterDouble().y+(xShift*Math.sin(Math.toRadians(getRotation())))-(yShift*Math.cos(Math.toRadians(getRotation()))));
			aimVector.setDirection(Vector.getVectorDirection(getAxis(), DrawPanel.relMousePoint)+recoil);
		} else {
			setRotation(getRotation()-recoil);
			bulletStart.setLocation(getCenterDouble().x+(xShift*Math.cos(Math.toRadians(getRotation())))-(yShift*Math.sin(Math.toRadians(getRotation()))),
									getCenterDouble().y+(xShift*Math.sin(Math.toRadians(getRotation())))+(yShift*Math.cos(Math.toRadians(getRotation()))));
			aimVector.setDirection(Vector.getVectorDirection(getAxis(), DrawPanel.relMousePoint)-recoil); 
		}
		
		aimVector.setSize(MathUtils.distPoints(getAxis(), DrawPanel.relMousePoint));
		
		DrawPanel.aimPoint.setLocation(getAxis().x+aimVector.getXComp(), getAxis().y+aimVector.getYComp());
	}
	
	public void draw(Graphics2D g2) {
		super.draw(g2);
	}
	
	private void recoil(double amount) {
		recoil = amount;
	}

	protected SpriteMap getSpriteMap() {
		return new SpriteMap(15, 10, 0, new File("resources/textures/weapons/Gun.png"));
	}

	protected Animation getRestAnimation() {
		return new Animation(textureMap, 0, 0, 0, 0, 99999, true);
	}
	
	private static int gunShotSound = AudioMaster.loadSound("resources/sounds/Gun/Gunshot.wav");
	public void mousePressed(MouseEvent m) {
		AudioMaster.playSound(gunShotSound, 0.5f, (float)(1.0f*DrawPanel.deltaMultiplier));
		
		Double bulletDirection = Vector.getVectorDirection(bulletStart, DrawPanel.aimPoint);
		//DrawPanel.fireOldestBullet(bulletStart.x, bulletStart.y, bulletDirection, 15, true);
		((Bullet)PoolMaster.getObject("friendlyBullets")).fire(bulletStart.x, bulletStart.y, bulletDirection, 15);
		recoil(50);
	}

	public void mouseReleased(MouseEvent m) {
		
	}

	public void reset() {
		recoil = 0;
	}
}
