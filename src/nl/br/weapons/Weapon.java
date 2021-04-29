package nl.br.weapons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import en.lib.drawing.Animation;
import en.lib.drawing.SpriteMap;
import en.lib.math.Vector;
import en.lib.shapes.Transformable;
import nl.br.entities.MoveableEntity;
import nl.br.map.floors.Floor;
import nl.br.panels.DrawPanel;

public abstract class Weapon extends Transformable {
	protected MoveableEntity holder;
	
	protected double scale = 2;
	protected int xShift, yShift;
	
	protected boolean followMouse = true, visible = true;
	
	protected SpriteMap textureMap;
	protected Animation restAnimation, currentAnimation;
	
	public Weapon(MoveableEntity holder) {
		this.holder = holder;
		move(holder.getCenter());
		textureMap = getSpriteMap();
		restAnimation = getRestAnimation();
		currentAnimation = restAnimation;
		
		Rectangle initialBox = new Rectangle(0, 0, textureMap.spriteWidth, textureMap.spriteHeight);
		setInitialShape(initialBox);
		setScale(scale);
		
		xShift = (int)Math.round(textureMap.spriteWidth*scale*0.3);
		yShift = -(int)Math.round((textureMap.spriteHeight*scale)/2);
	}
	
	public void tick(double delta) {
		currentAnimation.tick(delta);
		
		if (followMouse) {
			setRotation(getAngleToMouse());
		}
	}
	
	public void draw(Graphics2D g2) {
		if (visible) {
			if (onRight()) {
				currentAnimation.draw(getAffineTransform(), g2);
			} else {
				AffineTransform flipped = (AffineTransform)getAffineTransform().clone();
				flipped.concatenate(AffineTransform.getScaleInstance(1, -1));
				flipped.concatenate(AffineTransform.getTranslateInstance(0, -textureMap.spriteHeight));
				currentAnimation.draw(flipped, g2);
			}
		}
	}
	
	protected double getAngleToMouse() {
		return Vector.getVectorDirection(getAxis(), DrawPanel.relMousePoint);
	}
	
	public boolean onRight() {
		return (Math.floorMod((int)getAngleToMouse(), 360) > 270 || Math.floorMod((int)getAngleToMouse(), 360) < 90);
	}
	
	public boolean onLeft() {
		return !onRight();
	}
	
	public boolean onTop() {
		return (Math.floorMod((int)getAngleToMouse(), 360) > 180);
	}
	
	public boolean onBottom() {
		return !onTop();
	}
	
	public void show() {
		visible = true;
	}
	
	public void hide() {
		visible = false;
	}
	
	public void move(Point anchor) {
		setAxis(anchor);
		setTranslation(new Point(anchor.x+xShift, anchor.y+yShift));
	}
	
	protected void switchAnimation(Animation newAnimation) {
		if (currentAnimation != newAnimation) {
			currentAnimation.reset();
			currentAnimation = newAnimation;
		}
	}
	
	public int getDrawHeight() {
		if (getRotation() < 0 || getRotation() > 180) {
			return holder.getDrawHeight()-1;
		} else {
			return holder.getDrawHeight()+1;
		}
	}
	
	
	protected abstract SpriteMap getSpriteMap();
	protected abstract Animation getRestAnimation();
	public abstract void mousePressed(MouseEvent m);
	public abstract void mouseReleased(MouseEvent m);
	public abstract void reset();
}
