package nl.br.entities.enemies;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import en.lib.drawing.SpriteMap;
import en.lib.math.MathUtils;
import en.lib.math.Vector;
import en.lib.pooling.PoolMaster;
import nl.br.map.pathFinding.PathFindUtils;
import nl.br.panels.DrawPanel;
import nl.br.weapons.Sword;
import nl.br.weapons.projectiles.Arrow;
import nl.br.weapons.projectiles.Projectile;

public class EnemyUtils {
	private static final String enemyTextureFolder = "resources/textures/characters/Enemies/";
	public static EnemyType getEnemyByType(int type) {
		if (type == 1) {
			return new EnemyType(20, 20, 5, 5, new SpriteMap(10, 10, 1, new File(enemyTextureFolder + "Blob.png")), 0) {
				
				public void init(Enemy enemy) {
					enemy.movement.setSize(2);
					enemy.HP = 1;
					enemy.walkAccuracy = enemy.getWidthInt();
					enemy.contactDamage = 2;
				}
				
				public void actionStart(Enemy enemy) {
					enemy.movement.setDirection(Vector.getVectorDirection(enemy.getFeetCenter(), enemy.path.get(0)));
				}

				public void actionTick(Enemy enemy, double delta) {
					enemy.applyMovement(enemy.movement, delta);
					enemy.movement.setDirection(Vector.getVectorDirection(enemy.getFeetCenter(), enemy.path.get(0)));
				}
				
				public boolean actionFinishCondition(Enemy enemy) {
					//TODO Make method to test if a vector is pointing away from the goalPoint
					return MathUtils.distPoints(enemy.getFeetCenter(), enemy.path.get(0)) < enemy.walkAccuracy;
				}
					
				public void actionEnd(Enemy enemy) {
					enemy.path.remove(0);
					enemy.calculatePath();
				}
				
				public void onProjectileHit(Projectile projectile, Enemy enemy) {
					enemy.HP -= projectile.damage;
				}
				
				public void onMeleeHit(Sword sword, Enemy enemy) {
					enemy.HP -= sword.damage;
				}
				
				public void onDeath(Enemy enemy) {
					for (int i = 0; i < 360; i+=20) {
						((Arrow)PoolMaster.getOldestObject("enemyArrows")).fire((int)enemy.getCenterX(), (int)enemy.getCenterY(), Vector.getVectorDirection(enemy.getSpriteCenter(), DrawPanel.ben.getSpriteCenter())+i, 10);
					}
				}
				
				private Vector pathVector = new Vector();
				public ArrayList<Point> findPath(Enemy enemy) {
					pathVector.setDirection(MathUtils.randInt(0, 360));
					pathVector.setSize(MathUtils.randInt(50, 100));
					Point goalPoint = new Point((int)(enemy.getFinalPathPoint().x+pathVector.getXComp()), (int)(enemy.getFinalPathPoint().y+pathVector.getYComp()));
					while (true) {
						if (!DrawPanel.currentFloor.isInAnyCollisionArea(goalPoint.x, goalPoint.y)) {
							break;
						} else {
							pathVector.setDirection(MathUtils.randInt(0, 360));
							pathVector.setSize(MathUtils.randInt(50, 100));
							goalPoint.setLocation((int)(enemy.getFinalPathPoint().x+pathVector.getXComp()), (int)(enemy.getFinalPathPoint().y+pathVector.getYComp()));
						}
					}
					return PathFindUtils.getPath(enemy.getFinalPathPoint(), goalPoint, enemy.getWidthInt(), enemy.getHeightInt());
				}

				
			};
		} else {
			return null;
		}
	 }
}
