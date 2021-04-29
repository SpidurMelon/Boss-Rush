package nl.br.effects;

import java.awt.Graphics2D;
import java.awt.Point;

import en.lib.drawing.Animation;
import en.lib.drawing.Drawable;
import en.lib.drawing.SpriteMap;
import en.lib.math.Vector;
import en.lib.objects.Tickable;
import en.lib.pooling.Poolable;

public class Effect implements Drawable, Tickable, Poolable {
	private int drawHeight = 0;
	public double x, y, scale = 1;
	public Vector movement = new Vector();
	public Animation animation;
	public boolean enabled = false;
	public Effect(SpriteMap sm, int startX, int startY, int endX, int endY, int frameDelay, boolean repeating) {
		animation = new Animation(sm, startX, startY, endX, endY, frameDelay, repeating);
	}
	public Effect(Animation animation) {
		this.animation = animation;
	}
	public void tick(double delta) {
		if (!animation.finished) {
			x+=movement.getXComp()*delta;
			y+=movement.getYComp()*delta;
			animation.tick(delta);
		} else {
			disable();
		}
	}
	public void draw(Graphics2D g2) {
		if (enabled) {
			animation.draw((int)x, (int)y, (int)(animation.sprites.get(0).getWidth()*scale), (int)(animation.sprites.get(0).getHeight()*scale), g2);
		}
	}
	public void reset() {
		animation.reset();
	}
	public void play(int x, int y, int drawHeight, double scale, double direction, double speed, boolean centered) {
		
		if (centered) {
			this.x = (int)(x-(animation.spriteWidth/2)*scale);
			this.y = (int)(y-(animation.spriteHeight/2)*scale);
		} else {
			this.x = x;
			this.y = y;
		}
		this.drawHeight = drawHeight;
		this.scale = scale;
		this.movement.setDirection(direction);
		this.movement.setSize(speed);
		reset();
		enable();
	}
	
	public int getDrawHeight() {
		return drawHeight;
	}
	
	public Point getCenter() {
		return new Point((int)(x+(animation.spriteWidth*scale)/2), (int)(y+(animation.spriteHeight*scale)/2));
	}

	public Poolable createNew() {
		return new Effect(animation.clone());
	}
	
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
