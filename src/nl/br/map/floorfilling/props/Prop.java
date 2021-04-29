package nl.br.map.floorfilling.props;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import en.lib.drawing.Animation;
import en.lib.drawing.Drawable;
import en.lib.math.MathUtils;
import en.lib.objects.Tickable;
import nl.br.map.MapUtils;
import nl.br.panels.DrawPanel;

public class Prop implements Drawable, Tickable {
	
	public int x, y;
	private int drawHeight;
	private Animation animation;
	
	private Point[] lightningPoints;
	private int lightRadius = 30;
	
	private static double blockScale = (((double)DrawPanel.SCALE)/((double)MapUtils.blockResolution));
	private int drawToWidth, drawToHeight;
	
	private static Color innerLightningRing = new Color(253,207,88,50), outerLightningRing = new Color(242,125,12,50);
	private Point[] scaledLightningPoints;
	
	public Prop(int x, int y, int drawHeight, int width, int height, Animation animation, Point[] lightningPoints) {
		this.x = x;
		this.y = y;
		this.drawHeight = drawHeight;
		this.animation = animation;
		this.lightningPoints = lightningPoints;
		
		scaledLightningPoints = new Point[lightningPoints.length];
		for (int i = 0; i < lightningPoints.length; i++) {
			scaledLightningPoints[i] = new Point((int)(lightningPoints[i].x*blockScale), (int)(lightningPoints[i].y*blockScale));
		}
		
		drawToWidth = (int)(width*blockScale);
		drawToHeight = (int)(height*blockScale);
	}
	
	public Prop(int x, int y, int drawHeight, int width, int height, Animation animation) {
		this(x, y, drawHeight, width, height, animation, null);
	}
	
	public Prop(int x, int y, AbstractProp prop) {
		this(x, y, prop.drawHeight, prop.width, prop.height, prop.animation, prop.lightningPoints);
	}
	
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void tick(double delta) {
		animation.tick(delta);
	}
	
	public void draw(Graphics2D g2) {
		//animation.draw((int)x, (int)y, width, height, g2);
		animation.draw((int)x, (int)y, drawToWidth, drawToHeight, g2);
	
		if (lightningPoints != null) {
			g2.setColor(outerLightningRing);
			int xShift = MathUtils.randInt(-1, 1), yShift = MathUtils.randInt(-1, 1);
			int scaledLightRadius = (int)(lightRadius*blockScale);
			for (Point p:scaledLightningPoints) {
				g2.fillOval((int)(x+p.getX()-scaledLightRadius/2.0+xShift), (int)(y+p.getY()-scaledLightRadius/2.0+yShift), scaledLightRadius, scaledLightRadius);
			}
			g2.setColor(innerLightningRing);
			for (Point p:scaledLightningPoints) {
				g2.fillOval((int)(x+p.getX()-scaledLightRadius/3.0+xShift), (int)(y+p.getY()-scaledLightRadius/3.0+yShift), (int)(scaledLightRadius/1.5), (int)(scaledLightRadius/1.5));
			}
		}
	}

	public int getDrawHeight() {
		return drawHeight;
	}

	public Point getCenter() {
		return new Point(x+drawToWidth/2, y+drawToHeight/2);
	}
}
