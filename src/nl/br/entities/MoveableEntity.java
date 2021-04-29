package nl.br.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import en.lib.drawing.Drawable;
import en.lib.math.Vector;
import en.lib.objects.Collideable;
import en.lib.objects.Tickable;
import nl.br.map.floorfilling.Area;
import nl.br.map.floorfilling.Room;
import nl.br.panels.DrawPanel;

public abstract class MoveableEntity extends Collideable implements Drawable, Tickable {
	
	public Vector movement = new Vector(0, 5);
	public boolean up = false, down = false, left = false, right = false, frozen = false;
	
	//public ArrayList<Block> surroundingBlocks = new ArrayList<Block>();
	
	public Rectangle spriteBox = new Rectangle();
	
	public double HP = 10;
	
	public Room currentRoom;
	
	public MoveableEntity(int startX, int startY, int spriteWidth, int spriteHeight, int collideWidth, int collideHeight) {
		super(startX+spriteWidth/2-collideWidth/2, startY+spriteHeight-collideHeight, collideWidth, collideHeight);
		
		spriteBox.setBounds(0, 0, spriteWidth, spriteHeight);
		updateSpriteBoxLocation();
		
		Room supposedCurrentRoom = DrawPanel.currentFloor.getRoom(new Point(getFeetCenter().x, getFeetCenter().y));
		if (supposedCurrentRoom != null) {
			currentRoom = supposedCurrentRoom;
		} else {
			currentRoom = DrawPanel.currentFloor.allRooms.get(0);
		}
	}
	
	public void tick(double delta) {
		Room supposedCurrentRoom = DrawPanel.currentFloor.getRoom(new Point(getFeetCenter().x, getFeetCenter().y));
		if (supposedCurrentRoom != null) {
			currentRoom = supposedCurrentRoom;
		}
		
	}
	
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
		
		updateSpriteBoxLocation();
	}
	
	public void applyMovement(Vector v, double delta) {
		x += v.getXComp()*delta;
		y += v.getYComp()*delta;
		collide(v);
		
		updateSpriteBoxLocation();
	}
	
	public void freeze() {
		this.frozen = true;
	}
	
	public void unFreeze() {
		this.frozen = false;
	}
	
	public void collide(Vector currentMovement) {
		for (Area a:currentRoom.getCollisionAreas()) {
			if (a.intersects(this)) {
				Vector collisionVector = this.pathOfLeastResistance(a);
				if (DrawPanel.currentFloor.isInAnyCollisionArea((int)(x+getWidthInt()/2+collisionVector.getXComp()), (int)(y+getHeightInt()/2+collisionVector.getYComp()))) {
					collisionVector = this.pathOfNthResistance(a, 1);
					if (DrawPanel.currentFloor.isInAnyCollisionArea((int)(x+getWidthInt()/2+collisionVector.getXComp()), (int)(y+getHeightInt()/2+collisionVector.getYComp()))) {
						collisionVector = new Vector(currentMovement.getDirection()+180, currentMovement.getSize());
					}
				}
				
				x += collisionVector.getXComp();
				y += collisionVector.getYComp();
				updateSpriteBoxLocation();
			}
		}
	}
	
	private void updateSpriteBoxLocation() {
		spriteBox.setLocation(getXInt()+getWidthInt()/2-spriteBox.width/2, getYInt()+getHeightInt()-spriteBox.height);
	}
	
	private final Point feetCenter = new Point();
	public Point getFeetCenter() {
		feetCenter.setLocation((int)getCenterX(), (int)getCenterY());
		return feetCenter;
	}
	
	private final Point spriteCenter = new Point();
	public Point getSpriteCenter() {
		spriteCenter.setLocation((int)spriteBox.getCenterX(), (int)spriteBox.getCenterY());
		return spriteCenter;
	}

	public int getDrawHeight() {
		return (int)(y+height/2);
	}

	public abstract void draw(Graphics2D g2);
	
	public Point getCenter() {
		return getSpriteCenter();
	}
}
