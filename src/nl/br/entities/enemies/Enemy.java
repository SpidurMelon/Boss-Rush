package nl.br.entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.File;

import en.lib.drawing.Animation;
import en.lib.drawing.SpriteMap;
import en.lib.pooling.Pool;
import en.lib.pooling.PoolMaster;
import nl.br.entities.MoveableEntity;
import nl.br.entities.players.Ben;
import nl.br.map.pathFinding.PointPath;
import nl.br.panels.DrawPanel;
import nl.br.weapons.Bow;
import nl.br.weapons.Gun;
import nl.br.weapons.Sword;
import nl.br.weapons.Wand;
import nl.br.weapons.projectiles.Arrow;
import nl.br.weapons.projectiles.Bullet;
import nl.br.weapons.projectiles.MagicDamageOrb;
import nl.br.weapons.projectiles.Projectile;

public class Enemy extends MoveableEntity {
	private int actionDelay = 60, actionTimer = 0;
	private boolean inAction;
	
	private int animationDelay = 15;
	private SpriteMap animations;
	public Animation idleAnimation;
	public Animation currentAnimation;
	
	public EnemyType enemyType;
	
	private int pathLength = 10;
	public PointPath path;
	public int walkAccuracy;
	
	private SpriteMap ghostMap = new SpriteMap(10, 10, 1, new File("resources/textures/characters/Enemies/BlobGhost.png"));
	private Animation ghost = new Animation(ghostMap, 0, 0, 1, 0, animationDelay, true);
	
	public boolean living = true;
	
	public double contactDamage = 1;
	
	public Enemy(int startX, int startY, int spriteWidth, int spriteHeight, int collideWidth, int collideHeight, SpriteMap animations) {
		super(startX, startY, spriteWidth, spriteHeight, collideWidth, collideHeight);
		this.animations = animations;
		idleAnimation = new Animation(animations, 0, 0, 1, 0, animationDelay, true);
		currentAnimation = idleAnimation;
		
		path = new PointPath();
	}
	
	public Enemy(int startX, int startY, EnemyType enemyType) {
		this(startX, startY, enemyType.spriteWidth, enemyType.spriteHeight, enemyType.collideWidth, enemyType.collideHeight, enemyType.animations);
		this.actionDelay = enemyType.actionDelay;
		this.enemyType = enemyType;
		
		enemyType.init(this);
		
		calculatePath();
	}
	
	public void tick(double delta) {
		if (living) {
			super.tick(delta);
			currentAnimation.tick(delta);
			ghost.tick(delta);
			
			if (inAction) {
				actionTick(delta);
			} else if (!inAction && actionTimer >= actionDelay) {
				actionStart();
			} else if (!inAction) {
				actionTimer++;
			}
			
			if (DrawPanel.ben.weapon instanceof Bow) {
				Pool<Arrow> friendlyArrows = (Pool<Arrow>)PoolMaster.getPool("friendlyArrows");
				for (int i = 0; i < friendlyArrows.size(); i++) {
					if (friendlyArrows.get(i).isActive() && friendlyArrows.get(i).getTransformedShape().intersects(spriteBox)) {
						onProjectileHit(friendlyArrows.get(i));
						friendlyArrows.get(i).onEntityHit();
					}
				}
			}
			
			if (DrawPanel.ben.weapon instanceof Gun) {
				Pool<Bullet> friendlyBullets = (Pool<Bullet>)PoolMaster.getPool("friendlyBullets");
				for (int i = 0; i < friendlyBullets.size(); i++) {
					if (friendlyBullets.get(i).isActive() && friendlyBullets.get(i).getTransformedShape().intersects(spriteBox)) {
						onProjectileHit(friendlyBullets.get(i));
						friendlyBullets.get(i).onEntityHit();
					}
				}
			}
			
			if (DrawPanel.ben.weapon instanceof Wand) {
				Pool<MagicDamageOrb> friendlyMagic = (Pool<MagicDamageOrb>)PoolMaster.getPool("friendlyMagic");
				for (int i = 0; i < friendlyMagic.size(); i++) {
					if (friendlyMagic.get(i).isActive() && friendlyMagic.get(i).getTransformedShape().intersects(spriteBox)) {
						onProjectileHit(friendlyMagic.get(i));
						friendlyMagic.get(i).onEntityHit();
					}
				}
			}
			
			if (DrawPanel.ben.weapon instanceof Sword) {
				Sword benSword = (Sword)DrawPanel.ben.weapon;
				if (benSword.swinging && benSword.getTransformedShape().intersects(spriteBox)) {
					onMeleeHit(benSword);
					benSword.onEnemyHit();
				}
			}
			
			
			
			if (HP <= 0.0) {
				onDeath();
			}
		}
	}

	public void draw(Graphics2D g2) {
		if (living) {
			currentAnimation.draw(spriteBox.x, spriteBox.y, spriteBox.width, spriteBox.height, g2);
			g2.setColor(Color.RED);
			/*
			ArrayList<Point> enemyProjections = DrawPanel.player.bow.getEnemyProjections(this);
			int centerTopTranslationX = spriteBox.x-getCenter().x, centerTopTranslationY = spriteBox.y-getCenter().y;
			for (Point p:enemyProjections) {
				ghost.draw(p.x+centerTopTranslationX, p.y+centerTopTranslationY, 20, 20, g2);
			}
			*/
		}
		
		if (DrawPanel.DEBUG) {
			g2.setColor(Color.BLUE);
			for (int i = 0; i < path.size()-1; i++) {
				if (i == 0) {
					g2.drawLine(getFeetCenter().x, getFeetCenter().y, path.get(0).x, path.get(0).y);
				}
				
				g2.drawLine(path.get(i).x, path.get(i).y, path.get(i+1).x, path.get(i+1).y);
			}
			
			g2.setColor(Color.GREEN);
			Point pointIn2Sec = path.getPoint(getFeetCenter(), movement.getSize(), walkAccuracy, 120);
			g2.fillOval(pointIn2Sec.x-3, pointIn2Sec.y-3, 6, 6);
		}
	}
	
	public Point getFinalPathPoint() {
		if (!path.isEmpty()) {
			return path.get(path.size()-1);
		} else {
			return getCenter();
		}
	}
	
	public void calculatePath() {
		for (int i = path.size(); i < pathLength; i++) {
			//System.out.println("path found");
			path.addAll(enemyType.findPath(this));
		}
	}
	
	public void actionStart() {
		inAction = true;
		enemyType.actionStart(this);
	}
	
	public void actionTick(double delta) {
		enemyType.actionTick(this, delta);
		if (enemyType.actionFinishCondition(this)) {
			actionEnd();
		}
	}
	
	public void actionEnd() {
		enemyType.actionEnd(this);
		actionTimer = 0;
		inAction = false;
	}
	
	public void onProjectileHit(Projectile projectile) {
		enemyType.onProjectileHit(projectile, this);
	}
	
	public void onMeleeHit(Sword sword) {
		enemyType.onMeleeHit(sword, this);
	}
	
	public void onDeath() {
		enemyType.onDeath(this);
		living = false;
	}
}
