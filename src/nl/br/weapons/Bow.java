package nl.br.weapons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import en.lib.drawing.Animation;
import en.lib.drawing.Drawable;
import en.lib.drawing.SpriteMap;
import en.lib.math.MathUtils;
import en.lib.pooling.PoolMaster;
import en.lib.sounds.AudioMaster;
import nl.br.entities.enemies.Enemy;
import nl.br.entities.players.Ben;
import nl.br.panels.DrawPanel;
import nl.br.weapons.projectiles.Arrow;

public class Bow extends Weapon implements Drawable {
	public boolean charging = false, charged = false;
	private double chargeTimer = 0;
	private int chargeTime = 180;
	
	private Animation chargeAnimation;
	private Animation chargedAnimation;
	
	private Arrow chargingArrow;
	public double arrowSpeed = 15;
	
	public Bow(Ben player) {
		super(player);
		chargeAnimation = new Animation(textureMap, 0, 1, 5, 1, chargeTime/6, false);
		chargedAnimation = new Animation(textureMap, 0, 2, 0, 2, 9999, true);
		
		//hide();
	}
	
	public void tick(double delta) {
		super.tick(delta);
		
		if (holder instanceof Ben && (charging || charged)) {
			double rotationMod = Math.floorMod((int)getRotation(), 360);
			Ben playerHolder = (Ben)holder; 
			if (rotationMod > 250 && rotationMod < 290) {
				playerHolder.currentChargeAnimation = playerHolder.upChargeAnimation;
			} else if (rotationMod > 290 && rotationMod < 340) {
				playerHolder.currentChargeAnimation = playerHolder.upRightChargeAnimation;
			} else if (rotationMod > 340 || rotationMod < 20) {
				playerHolder.currentChargeAnimation = playerHolder.rightChargeAnimation;
			} else if (rotationMod > 20 && rotationMod < 70) {
				playerHolder.currentChargeAnimation = playerHolder.downRightChargeAnimation;
			} else if (rotationMod > 70 && rotationMod < 110) {
				playerHolder.currentChargeAnimation = playerHolder.downChargeAnimation;
			} else if (rotationMod > 110 && rotationMod < 160) {
				playerHolder.currentChargeAnimation = playerHolder.downLeftChargeAnimation;
			} else if (rotationMod > 160 && rotationMod < 200) {
				playerHolder.currentChargeAnimation = playerHolder.leftChargeAnimation;
			} else if (rotationMod > 200 && rotationMod < 250) {
				playerHolder.currentChargeAnimation = playerHolder.upLeftChargeAnimation;
			} 
		}
		
		if (charging) {
			chargeTimer+=delta;
			DrawPanel.alphaOverlay = (int)((chargeTimer/(double)chargeTime)*50);
			if (chargeTimer >= chargeTime) {
				switchAnimation(chargedAnimation);
				DrawPanel.shakeScreen(1, 0);
				
				charging = false;
				charged = true;
				chargeTimer = 0;
			}
		}
	}
	
	public void draw(Graphics2D g2) {
		super.draw(g2);
		
		if (DrawPanel.DEBUG) {
			g2.setColor(Color.BLACK);
			g2.draw(getTransformedShape());
			g2.fillRect(getCenter().x, getCenter().y, 5, 5);
		}
	}
	
	public void mousePressed(MouseEvent m) {
		chargeArrow();
	}

	public void mouseReleased(MouseEvent m) {
		releaseArrow();
	}
	
	private int chargeSource = AudioMaster.createSource();
	private int chargeSound = AudioMaster.loadSound("resources/sounds/Bow/BowPull.wav");
	public void chargeArrow() {
		AudioMaster.playSound(chargeSource, chargeSound, 1, (float)(1*DrawPanel.deltaMultiplier));
		//show();
		charging = true;
		holder.freeze();
		chargingArrow = (Arrow)PoolMaster.getOldestObject("friendlyArrows");
		chargingArrow.enable();
		chargingArrow.firedOnce = true;
		chargingArrow.drawOnBow(this);
		switchAnimation(chargeAnimation);
	}
	
	private int releaseSound = AudioMaster.loadSound("resources/sounds/Bow/BowRelease.wav");
	public void releaseArrow() {
		if (charged) {
			chargingArrow.releaseFromBow();
			AudioMaster.playSound(releaseSound, 1, (float)(1*DrawPanel.deltaMultiplier));
		} else {
			chargingArrow.fall();
		}
		AudioMaster.stopSound(chargeSource);
		//hide();
		charging = false;
		charged = false;
		chargeTimer = 0;
		holder.unFreeze();
		switchAnimation(restAnimation);
		DrawPanel.stopScreenShake();
		DrawPanel.alphaOverlay = 0;
	}
	
	public ArrayList<Point> getEnemyProjections(Enemy enemy) {
		ArrayList<Point> result = new ArrayList<Point>();
		int checkLimit = 15, checkDelay = 20;
		for (int i = 0; i < checkLimit*checkDelay; i+=checkDelay) {
			double arrowDistance = getDistance(i);
			Point enemyPoint = enemy.path.getPoint(enemy.getCenter(), enemy.movement.getSize(), enemy.walkAccuracy, i);
			double enemyDistance = MathUtils.distPoints(enemyPoint, getAxis());
			if (arrowDistance-enemyDistance > 0 && arrowDistance-enemyDistance <= arrowSpeed*checkDelay+enemy.movement.getSize()*checkDelay) {
				for (double j = 0; j < checkDelay; j+=0.2) {
					double accurateArrowDistance = getDistance(i-j);
					Point accurateEnemyPoint = enemy.path.getPoint(enemy.getCenter(), enemy.movement.getSize(), enemy.walkAccuracy, i-j);
					double accurateEnemyDistance = MathUtils.distPoints(accurateEnemyPoint, getAxis());
					if (Math.abs(accurateArrowDistance-accurateEnemyDistance) < 2) {
						result.add(accurateEnemyPoint);
						break;
					}
				}
			}
		}
		return result;
	}
	
	private double getDistance(double afterDelta) {
		return afterDelta*arrowSpeed;
	}

	protected SpriteMap getSpriteMap() {
		return new SpriteMap(10, 15, 1, new File("resources/textures/weapons/BenBow.png"));
	}

	protected Animation getRestAnimation() {
		return new Animation(textureMap, 0, 0, 0, 0, 9999, true);
	}

	public void reset() {
		if (charging || charged) {
			releaseArrow();
		}
	}
}
