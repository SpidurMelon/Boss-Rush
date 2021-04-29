package nl.br.weapons.projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import en.lib.objects.Tickable;
import en.lib.pooling.Poolable;
import nl.br.map.floorfilling.Area;
import nl.br.map.floorfilling.Room;
import nl.br.panels.DrawPanel;
import nl.br.weapons.Bow;

public class Arrow extends Projectile {
	public boolean onBow = false, falling = false, inGround = false, firedOnce = false;
	public double fallingTimer;
	
	private BufferedImage inGroundTexture;
	
	private Bow bow;
	
	private double scale = 2;
	public Arrow(boolean friendly) {
		super(friendly);
		damage = 5;
		try {
			inGroundTexture = ImageIO.read(new File("resources/textures/weapons/projectiles/arrows/ArrowInGround.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		setScale(scale);
	}
	
	public Arrow(int x, int y, double direction, double speed, boolean friendly) {
		this(friendly);
		fire(x, y, direction, speed);
	}
	
	public void tick(double delta) {
		super.tick(delta);
		if (onBow) {
			alignWithBow();
		} else if (falling) {
			y+=movement.getYComp()*delta;
			fallingTimer -= delta;
			if (fallingTimer <= 0) {
				inGround = true;
				falling = false;
			}
		}
	}
	
	public void draw(Graphics2D g2) {
		if (firedOnce) {
			if (!falling && !inGround) {
				g2.drawImage(texture, getAffineTransform(), null);
			} else {
				g2.drawImage(inGroundTexture, getX(), getY(), (int)(inGroundTexture.getWidth()*scale), (int)(inGroundTexture.getHeight()*scale), null);
			}
		}
		
		if (DrawPanel.DEBUG) {
			g2.setColor(Color.RED);
			g2.drawString(String.valueOf(getDrawHeight()), getX(), getY());
			
			g2.setColor(Color.BLACK);
			g2.draw(getTransformedShape());
		}
	}
	
	public void fire(double x, double y, double direction, double speed) {
		super.fire(x, y, direction, speed);
		firedOnce = true;
		inGround = false;
		falling = false;
		onBow = false;
	}
	
	public void drawOnBow(Bow bow) {
		inAir = false;
		inGround = false;
		falling = false;
		this.bow = bow;
		
		moveTo(bow.getCenter().x, bow.getCenter().y);
		alignWithBow();
		
		onBow = true;
	}
	
	public void alignWithBow() {
		setAxis(bow.getCenter());
		setRotation(bow.getRotation());
		setTranslation(new Point((int)(bow.getCenter().x-(texture.getWidth()*scale)/2)+1, (int)(bow.getCenter().y-(texture.getHeight()*scale)/2)+1));		
	}
	
	public void releaseFromBow() {
		Point bowCenter = bow.getCenter();
		onBow = false;
		fire(bowCenter.x, bowCenter.y, bow.getRotation(), bow.arrowSpeed);
		bow = null;
	}
	
	public void fall() {
		falling = true;
		movement.setDirection(90);
		movement.setSize(3);
		fallingTimer = 4;
		if (onBow) {
			moveTo(bow.getCenter().x, bow.getCenter().y);
			onBow = false;
			bow = null;
		}
	}

	public int getDrawHeight() {
		if (inGround) {
			return getY();
		} else if (onBow) {
			return bow.getDrawHeight();
		} else {
			return (int)(y+(Math.abs(movement.getYComp())/movement.getSize())*texture.getWidth());
		}
	}

	@Override
	public BufferedImage getTexture(boolean friendly) {
		try {
			if (friendly) {
				return ImageIO.read(new File("resources/textures/weapons/projectiles/arrows/FriendlyArrow.png"));
			} else {
				return ImageIO.read(new File("resources/textures/weapons/projectiles/arrows/EnemyArrow.png"));
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void onWallHit() {
		inAir = false;
		disable();
	}

	public void onEntityHit() {
		
	}

	@Override
	public Poolable createNew() {
		return new Arrow(friendly);
	}
}
