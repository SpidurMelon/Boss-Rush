package nl.br.weapons.projectiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import en.lib.math.Vector;
import en.lib.objects.Tickable;
import en.lib.pooling.Poolable;
import en.lib.shapes.Transformable;
import nl.br.map.floorfilling.Area;
import nl.br.map.floorfilling.Room;
import nl.br.panels.DrawPanel;

public abstract class Projectile extends Transformable implements Poolable, Tickable {
	protected double x, y;
	protected Vector movement = new Vector();
	
	protected boolean inAir = false, friendly = false;
	protected BufferedImage texture;
	
	protected double scale = 1;
	
	public double damage = 1;
	
	protected boolean enabled = false;
	
	public Projectile(boolean friendly) {
		this.friendly = friendly;
		texture = getTexture(friendly);
		
		Rectangle initialShape = new Rectangle(0, 0, texture.getWidth(), texture.getHeight());
		setInitialShape(initialShape);
		setScale(scale);
	}
	
	public void tick(double delta) {
		if (inAir) {
			x+=movement.getXComp()*delta;
			y+=movement.getYComp()*delta;
			setAxis(new Point(getX(), getY()));
			setTranslation(new Point(getX()-(int)Math.round((texture.getWidth()*getScale())/2.0), getY()-(int)Math.round((texture.getHeight()*getScale())/2.0)));
			testProjectile();
		}
	}
	
	public void draw(Graphics2D g2){
		if (enabled) {
			g2.drawImage(texture, getAffineTransform(), null);
		}
		
		if (DrawPanel.DEBUG) {
			g2.setColor(Color.BLACK);
			g2.draw(getTransformedShape());
		}
	}
	
	public void fire(double x, double y, double direction, double speed) {
		moveTo(x, y);
		movement.setDirection(direction);
		movement.setSize(speed);
		
		setAxis(new Point(getX(), getY()));
		setRotation(direction);
		
		enable();
		
		inAir = true;
	}
	
	public void testProjectile() {
		if (inAir) {
			if (!DrawPanel.currentFloor.bounds.contains(getX(), getY())) {
				onWallHit();
				return;
			}
			
			Room currentProjectileRoom = DrawPanel.currentFloor.getRoom(new Point(getX(), getY()));
			if (currentProjectileRoom != null) {
				for (Area a:currentProjectileRoom.getCollisionAreas()) {
					if (a.solid && a.contains(getCenter())) {
						onWallHit();
						return;
					}
				}
			}
		}
	}
	
	public boolean isActive() {
		return inAir;
	}
	
	public void moveTo(double x, double y) {
		this.x = x;
		this.y = y;
		setTranslation(new Point(getX()-(int)Math.round((texture.getWidth()*getScale())/2.0), getY()-(int)Math.round((texture.getHeight()*getScale())/2.0)));
	}
	
	public int getX() {
		return (int)Math.round(this.x);
	}
	
	public int getY() {
		return (int)Math.round(this.y);
	}
	
	public int getDrawHeight() {
		return (int)(y+(Math.abs(movement.getYComp())/movement.getSize())*texture.getWidth());
	}
	
	public abstract BufferedImage getTexture(boolean friendly);
	public abstract void onWallHit();
	public abstract void onEntityHit();
	
	public abstract Poolable createNew();
	public void enable() {
		enabled = true;
	}
	public void disable() {
		enabled = false;
	}
	public boolean isEnabled() {
		return enabled;
	}
}
