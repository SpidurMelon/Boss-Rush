package nl.br.map.floorfilling.props;

import java.awt.Point;

import en.lib.drawing.Animation;

public class AbstractProp {
	public int width, height, drawHeight;
	public Animation animation;
	public Point[] lightningPoints;
	
	public AbstractProp(int width, int height, int drawHeight, Animation animation, Point[] lightningPoints) {
		this.width = width;
		this.height = height;
		this.drawHeight = drawHeight;
		this.animation = animation;
		this.lightningPoints = lightningPoints;
	}
	public AbstractProp(int width, int height, int drawHeight, Animation animation) {
		this(width, height, drawHeight, animation, null);
	}
}
