package nl.br.entities.players;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import en.lib.drawing.Animation;
import en.lib.drawing.SpriteMap;
import en.lib.input.KeyBinding;
import en.lib.math.MathUtils;
import en.lib.math.Vector;
import en.lib.pooling.Pool;
import en.lib.pooling.PoolMaster;
import en.lib.setup.Panel;
import en.lib.setup.Tick;
import en.lib.sounds.AudioMaster;
import nl.br.effects.Effect;
import nl.br.entities.MoveableEntity;
import nl.br.entities.enemies.Enemy;
import nl.br.panels.DrawPanel;
import nl.br.weapons.Bow;
import nl.br.weapons.Gun;
import nl.br.weapons.Sword;
import nl.br.weapons.Wand;
import nl.br.weapons.Weapon;
import nl.br.weapons.projectiles.Arrow;
import nl.br.weapons.projectiles.Bullet;
import nl.br.weapons.projectiles.MagicDamageOrb;
import nl.br.weapons.projectiles.Projectile;

public class Ben extends MoveableEntity implements MouseListener {
	private Panel parent;
	private ArrayList<String> keysDown = new ArrayList<String>();
	
	public static int playerSpriteWidth = 40, playerSpriteHeight = 60;
	public static int playerCollideWidth = 15, playerCollideHeight = 10;
	
	private static int idleAnimationDelay = 10, walkAnimationDelay = 6, chargeAnimationDelay = 99999;
	private SpriteMap animations = new SpriteMap(20, 30, 1, new File("resources/textures/characters/Player/Ben.png"));
	public Animation idleAnimation = new Animation(animations, 0, 0, 5, 0, idleAnimationDelay, true);
	private Animation upAnimation = new Animation(animations, 0, 1, 7, 1, walkAnimationDelay, true);
	private Animation upRightAnimation = new Animation(animations, 0, 2, 7, 2, walkAnimationDelay, true);
	private Animation rightAnimation = new Animation(animations, 0, 3, 7, 3, walkAnimationDelay, true);
	private Animation downRightAnimation = new Animation(animations, 0, 4, 7, 4, walkAnimationDelay, true);
	private Animation downAnimation = new Animation(animations, 0, 5, 7, 5, walkAnimationDelay, true);
	private Animation downLeftAnimation = new Animation(animations, 0, 6, 7, 6, walkAnimationDelay, true);
	private Animation leftAnimation = new Animation(animations, 0, 7, 7, 7, walkAnimationDelay, true);
	private Animation upLeftAnimation = new Animation(animations, 0, 8, 7, 8, walkAnimationDelay, true);
	private Animation currentAnimation = idleAnimation; 
	public Animation upChargeAnimation = new Animation(animations, 9, 1, 9, 1, chargeAnimationDelay, true);
	public Animation upRightChargeAnimation = new Animation(animations, 9, 2, 9, 2, chargeAnimationDelay, true);
	public Animation rightChargeAnimation = new Animation(animations, 9, 3, 9, 3, chargeAnimationDelay, true);
	public Animation downRightChargeAnimation = new Animation(animations, 9, 4, 9, 4, chargeAnimationDelay, true);
	public Animation downChargeAnimation = new Animation(animations, 9, 5, 9, 5, chargeAnimationDelay, true);
	public Animation downLeftChargeAnimation = new Animation(animations, 9, 6, 9, 6, chargeAnimationDelay, true);
	public Animation leftChargeAnimation = new Animation(animations, 9, 7, 9, 7, chargeAnimationDelay, true);
	public Animation upLeftChargeAnimation = new Animation(animations, 9, 8, 9, 8, chargeAnimationDelay, true);
	public Animation currentChargeAnimation = upChargeAnimation; 
	
	public Bow bow = new Bow(this);
	public Gun gun = new Gun(this);
	public Sword sword = new Sword(this);
	public Wand wand = new Wand(this);
	public Weapon weapon;
	
	public double maxHP = 10, HP = maxHP;
	public double iFrames = 30, iTimer = 0;
	public boolean invulnerable = false;
	
	public boolean cameraFollowPlayer = false, inCutscene = false, canShoot = true;
	
	private int startX, startY;
	
	public Ben(int startX, int startY, Panel parent) {
		super(startX, startY, playerSpriteWidth, playerSpriteHeight, playerCollideWidth, playerCollideHeight);
		this.startX = startX;
		this.startY = startY;
		moveTo(startX, startY);
		this.parent = parent;
		DrawPanel.setCameraLocation((int)spriteBox.getCenterX(), (int)spriteBox.getCenterY());
		
		weapon = gun;
		DrawPanel.addDrawable(weapon);
		
		initKeys();
	}
	
	public void tick(double delta) {
		super.tick(delta);
		currentAnimation.tick(delta);
		checkMovement(delta);
		
		weapon.move(getSpriteCenter());
		weapon.tick(delta);
		
		Pool<Arrow> enemyArrows = (Pool<Arrow>)PoolMaster.getPool("enemyArrows");
		for (int i = 0; i < enemyArrows.size(); i++) {
			if (enemyArrows.get(i).isActive() && enemyArrows.get(i).getTransformedShape().intersects(spriteBox)) {
				onProjectileHit(enemyArrows.get(i));
				enemyArrows.get(i).onEntityHit();
			}
		}
		
		Pool<Bullet> enemyBullets = (Pool<Bullet>)PoolMaster.getPool("enemyBullets");
		for (int i = 0; i < enemyBullets.size(); i++) {
			if (enemyBullets.get(i).isActive() && enemyBullets.get(i).getTransformedShape().intersects(spriteBox)) {
				onProjectileHit(enemyBullets.get(i));
				enemyBullets.get(i).onEntityHit();
			}
		}
	
		Pool<MagicDamageOrb> enemyMagic = (Pool<MagicDamageOrb>)PoolMaster.getPool("enemyMagic");
		for (int i = 0; i < enemyMagic.size(); i++) {
			if (enemyMagic.get(i).isActive() && enemyMagic.get(i).getTransformedShape().intersects(spriteBox)) {
				onProjectileHit(enemyMagic.get(i));
				enemyMagic.get(i).onEntityHit();
			}
		}
		
		for (int i = 0; i < DrawPanel.enemies.size(); i++) {
			if (DrawPanel.enemies.get(i).living && DrawPanel.enemies.get(i).spriteBox.intersects(this.spriteBox)) {
				onContactHit(DrawPanel.enemies.get(i));
			}
		}
		
		if (HP <= 0) {
			onDeath();
		}
		
		if (iTimer > 0) {
			iTimer -= delta;
		} else {
			invulnerable = false;
			iTimer = 0;
		}
		
		//System.out.println(ShapeUtils.getCoords(bow.initialSpriteBox, bow.bowTransform));
	}
	
	private void onProjectileHit(Projectile projectile) {
		if (!invulnerable) {
			HP -= projectile.damage;
			invulnerable = true;
			iTimer = iFrames;
		}
	}
	
	private void onContactHit(Enemy enemy) {
		if (!invulnerable) {
			HP -= enemy.contactDamage;
			invulnerable = true;
			iTimer = iFrames;
		}
	}
	
	private void onMeleeHit(Sword sword) {
		if (!invulnerable) {
			HP -= sword.damage;
			invulnerable = true;
			iTimer = iFrames;
		}
	}
	
	private void onDeath() {
		respawn();
	}

	private void respawn() {
		moveTo(startX, startY);
		HP = maxHP;
	}

	public void draw(Graphics2D g2) {
		if (((int)iTimer)%4==0) {
			if (weapon instanceof Bow) {
				Bow bow = (Bow)weapon;
				if (!bow.charging && !bow.charged) {
					currentAnimation.draw(spriteBox.x, spriteBox.y, spriteBox.width, spriteBox.height, g2);
				} else {
					currentChargeAnimation.draw(spriteBox.x, spriteBox.y, spriteBox.width, spriteBox.height, g2);
				}
			} else {
				currentAnimation.draw(spriteBox.x, spriteBox.y, spriteBox.width, spriteBox.height, g2);
			}
		}
		
		if (DrawPanel.DEBUG) {
			g2.setColor(Color.BLUE);
			g2.draw(this);
			g2.setColor(Color.CYAN);
			g2.drawString(String.valueOf(getDrawHeight()), getXInt()+10, getYInt());
		}
	}
	
	private int[] steps = new int[]{AudioMaster.loadSound("resources/sounds/Steps/Step1.wav"), AudioMaster.loadSound("resources/sounds/Steps/Step2.wav")};
	private double smokePoofTimer;
	private int smokePoofDelay = walkAnimationDelay*4;
	public void checkMovement(double delta) {
		if (!inCutscene) {
			if (keysDown.contains("W")) {
				up = true;
			} else {
				up = false;
			}
			if (keysDown.contains("S")) {
				down = true;
			} else {
				down = false;
			}
			if (keysDown.contains("A")) {
				left = true;
			} else {
				left = false;
			}
			if (keysDown.contains("D")) {
				right = true;
			} else {
				right = false;
			}
		}
		
		
		if (!frozen && (up || down || left || right)) {
			if (up && !down && !left && !right) {
				movement.setDirection(-90);
				switchAnimation(upAnimation);
			} else if (!up && down && !left && !right) {
				movement.setDirection(90);
				switchAnimation(downAnimation);
			} else if (!up && !down && left && !right) {
				movement.setDirection(180);
				switchAnimation(leftAnimation);
			} else if (!up && !down && !left && right) {
				movement.setDirection(0);
				switchAnimation(rightAnimation);
			} else if (up && !down && left && !right) {
				movement.setDirection(-135);
				switchAnimation(upLeftAnimation);
			} else if (up && !down && !left && right) {
				movement.setDirection(-45);
				switchAnimation(upRightAnimation);
			} else if (!up && down && left && !right) {
				movement.setDirection(135);
				switchAnimation(downLeftAnimation);
			} else if (!up && down && !left && right) {
				movement.setDirection(45);
				switchAnimation(downRightAnimation);
			} 	
			applyMovement(movement, delta);
			if (smokePoofTimer >= smokePoofDelay) {
				AudioMaster.playSound(steps[MathUtils.randInt(0, steps.length-1)], 1, (float)(1*DrawPanel.deltaMultiplier));
				((Effect)PoolMaster.getObject("smokePoofs")).play(getFeetCenter().x, getFeetCenter().y, getDrawHeight(), 2, movement.getDirection()+180, 2, true);
				smokePoofTimer = smokePoofTimer-smokePoofDelay;
			} else {
				smokePoofTimer+=delta;
			}
		} else {
			switchAnimation(idleAnimation);
		}
	}
	
	public void applyMovement(Vector v, double delta) {
		super.applyMovement(v, delta);
		if (cameraFollowPlayer) {
			DrawPanel.setCameraLocation((int)spriteBox.getCenterX(), (int)spriteBox.getCenterY());
		}
	}
	
	public void moveTo(int x, int y) {
		super.moveTo(x, y);
		if (cameraFollowPlayer) {
			DrawPanel.setCameraLocation((int)spriteBox.getCenterX(), (int)spriteBox.getCenterY());
		}
	}
	
	public void switchAnimation(Animation animation) {
		if (currentAnimation != animation) {
			currentAnimation.reset();
			currentAnimation = animation;
		}
	}
	
	public void switchWeapon(Weapon weapon) {
		DrawPanel.removeDrawable(this.weapon);
		this.weapon.reset();
		this.weapon = weapon;
		weapon.move(getSpriteCenter());
		weapon.tick(1);
		DrawPanel.addDrawable(weapon);
	}
	
	private void initKeys() {
		KeyBinding upPressed = new KeyBinding(KeyEvent.VK_W, parent, false) {
			public void onAction() {
				if (!keysDown.contains("W")) {
					keysDown.add("W");
				}
			}
		};
		KeyBinding upReleased = new KeyBinding(KeyEvent.VK_W, parent, true) {
			public void onAction() {
				keysDown.remove("W");
			}
		};
		
		KeyBinding rightPressed = new KeyBinding(KeyEvent.VK_D, parent, false) {
			public void onAction() {
				if (!keysDown.contains("D")) {
					keysDown.add("D");
				}
			}
		};
		KeyBinding rightReleased = new KeyBinding(KeyEvent.VK_D, parent, true) {
			public void onAction() {
				keysDown.remove("D");
			}
		};
		KeyBinding downPressed = new KeyBinding(KeyEvent.VK_S, parent, false) {
			public void onAction() {
				if (!keysDown.contains("S")) {
					keysDown.add("S");
				}
			}
		};
		KeyBinding downReleased = new KeyBinding(KeyEvent.VK_S, parent, true) {
			public void onAction() {
				keysDown.remove("S");
			}
		};
		
		KeyBinding leftPressed = new KeyBinding(KeyEvent.VK_A, parent, false) {
			public void onAction() {
				if (!keysDown.contains("A")) {
					keysDown.add("A");
				}
			}
		};
		KeyBinding leftReleased = new KeyBinding(KeyEvent.VK_A, parent, true) {
			public void onAction() {
				keysDown.remove("A");
			}
		};
		
		
		KeyBinding R = new KeyBinding(KeyEvent.VK_R, parent, false) {
			public void onAction() {
				movement.setSize(5);
			}
		};
		KeyBinding Shift = new KeyBinding(KeyEvent.VK_SHIFT, parent, false) {
			public void onAction() {
				movement.setSize(10);
			}
		};
		KeyBinding Control = new KeyBinding(KeyEvent.VK_CONTROL, parent, false) {
			public void onAction() {
				movement.setSize(100);
			}
		};
		KeyBinding E = new KeyBinding(KeyEvent.VK_E, parent, false) {
			public void onAction() {
				DrawPanel.setSlowMotion(0.1);
			}
		};
		KeyBinding C = new KeyBinding(KeyEvent.VK_C, parent, false) {
			public void onAction() {
				if (weapon == bow) {
					switchWeapon(gun);
				} else if (weapon == gun) {
					switchWeapon(sword);
				} else if (weapon == sword) {
					switchWeapon(wand);
				} else if (weapon == wand) {
					switchWeapon(bow);
				}
			}
		};
		KeyBinding Q = new KeyBinding(KeyEvent.VK_Q, parent, false) {
			public void onAction() {
				DrawPanel.resetSlowMotion();
			}
		};
		KeyBinding P = new KeyBinding(KeyEvent.VK_P, parent, false) {
			public void onAction() {
				Tick.pauseUnpause();
			}
		};
		KeyBinding Esc = new KeyBinding(KeyEvent.VK_ESCAPE, parent, false) {
			public void onAction() {
				AudioMaster.cleanUp();
				System.exit(0);
			}
		};
		KeyBinding debug = new KeyBinding(KeyEvent.VK_SUBTRACT, parent, false) {
			public void onAction() {
				DrawPanel.DEBUG = !DrawPanel.DEBUG;
			}
		};
		KeyBinding debug2 = new KeyBinding(KeyEvent.VK_ADD, parent, false) {
			public void onAction() {
				DrawPanel.DEBUG2 = !DrawPanel.DEBUG2;
			}
		};
	}

	public void mousePressed(MouseEvent m) {
		if (canShoot) {
			weapon.mousePressed(m);
		}
	}

	public void mouseReleased(MouseEvent m) {
		if (canShoot) {
			weapon.mouseReleased(m);
		}
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
