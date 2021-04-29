package nl.br.weapons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.util.ArrayList;

import en.lib.drawing.Animation;
import en.lib.drawing.SpriteMap;
import en.lib.math.MathUtils;
import en.lib.shapes.ShapeUtils;
import en.lib.sounds.AudioMaster;
import nl.br.entities.MoveableEntity;
import nl.br.panels.DrawPanel;

public class Sword extends Weapon {

	public boolean swinging = false, swingRight = false;
	private double swingAngle, maxSwingAngle = 90, swingSpeed = 10;
	
	private int maxWooshLines = 10;
	private ArrayList<Line2D.Double> wooshLines = new ArrayList<Line2D.Double>();
	public double damage = 1;
	
	public Sword(MoveableEntity holder) {
		super(holder);
		//hide();
		for (int i = 0; i < maxWooshLines; i++) {
			wooshLines.add(new Line2D.Double());
		}
	}
	
	public void tick(double delta) {
		super.tick(delta);
		if (swinging && swingAngle >= -maxSwingAngle/2 && swingAngle <= maxSwingAngle/2) {
			setRotation(getAngleToMouse() + swingAngle);
			if (swingRight) {
				swingAngle += swingSpeed*delta;
			} else {
				swingAngle -= swingSpeed*delta;
			}
			ArrayList<Point> shapePoints = ShapeUtils.getCoords(getTransformedShape(), null);
			getOldestWooshLine().setLine(ShapeUtils.getCenterPoint(shapePoints.get(3), shapePoints.get(4)), ShapeUtils.getCenterPoint(shapePoints.get(1), shapePoints.get(2)));
		} else if (swinging) {
			swinging = false;
			swingRight = !swingRight;
			resetWooshLines();
			//hide();
		}
	}
	
	private static BasicStroke wooshThickness = new BasicStroke(8);
	private static BasicStroke standardThickness = new BasicStroke(1);
	public void draw(Graphics2D g2) {
		if (visible) {
			g2.setColor(new Color(255,255,255,50));
			g2.setStroke(wooshThickness);
			for (Line2D.Double wooshLine:wooshLines) {
				if (wooshLine.getX1() != 0 || wooshLine.getY1() != 0 || wooshLine.getX2() != 0 || wooshLine.getY2() != 0) {
					g2.draw(wooshLine);
				}
			}
			g2.setStroke(standardThickness);
		}
		super.draw(g2);
		
		if (DrawPanel.DEBUG) {
			g2.setColor(Color.RED);
			g2.draw(getTransformedShape());
		}
	}
	
	private static int oldestWooshLineIndex = 0;
	public Line2D.Double getOldestWooshLine() {
		if (oldestWooshLineIndex < wooshLines.size()-1) {
			oldestWooshLineIndex++;
		} else {
			oldestWooshLineIndex = 0;
		}
		return wooshLines.get(oldestWooshLineIndex);
	}
	
	private void resetWooshLines() {
		for (int i = 0; i < wooshLines.size(); i++) {
			wooshLines.get(i).setLine(0, 0, 0, 0);
		}
	}
	
	protected SpriteMap getSpriteMap() {
		return new SpriteMap(20, 7, 0, new File("resources/textures/weapons/Sword.png"));
	}

	protected Animation getRestAnimation() {
		return new Animation(textureMap, 0, 0, 0, 0, 99999, true);
	}
	
	public void onEnemyHit() {
		
	}
	
	private static int swingSound = AudioMaster.loadSound("resources/sounds/Sword/Sword swing.wav");
	public void startSwing() {
		if (!swinging) {
			swinging = true;
			if (swingRight) {
				swingAngle = -maxSwingAngle/2;
			} else {
				swingAngle = maxSwingAngle/2;
			}
			setRotation(getAngleToMouse() + swingAngle);
			AudioMaster.playSound(swingSound, 1, (float)((MathUtils.randInt(8, 12)/10.0)*DrawPanel.deltaMultiplier));
			//show();
		}
	}

	public void mousePressed(MouseEvent m) {
		startSwing();
	}

	public void mouseReleased(MouseEvent m) {
		
	}

	public void reset() {
		resetWooshLines();
		swinging = false;
		swingAngle = 0;
		//hide();
	}

}
