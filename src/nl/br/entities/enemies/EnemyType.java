package nl.br.entities.enemies;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import en.lib.drawing.SpriteMap;
import en.lib.math.Vector;
import nl.br.weapons.Sword;
import nl.br.weapons.projectiles.Projectile;

public abstract class EnemyType {
	public int spriteWidth, spriteHeight, collideWidth, collideHeight, actionDelay;
	public SpriteMap animations;
	
	public EnemyType(int spriteWidth, int spriteHeight, int collideWidth, int collideHeight, SpriteMap animations, int actionDelay) {
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		this.collideWidth = collideWidth;
		this.collideHeight = collideHeight;
		this.animations = animations;
		this.actionDelay = actionDelay;
	}
	
	public abstract void init(Enemy enemy);
	
	public abstract void actionStart(Enemy enemy);
	public abstract void actionTick(Enemy enemy, double delta);
	public abstract boolean actionFinishCondition(Enemy enemy);
	public abstract void actionEnd(Enemy enemy);
	
	public abstract ArrayList<Point> findPath(Enemy enemy);
	
	public abstract void onProjectileHit(Projectile projectile, Enemy enemy);
	public abstract void onMeleeHit(Sword sword, Enemy enemy);
	
	public abstract void onDeath(Enemy enemy);
}
