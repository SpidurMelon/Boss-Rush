package nl.br.map.floorfilling;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import en.lib.drawing.Drawable;
import en.lib.drawing.SpriteMap;
import en.lib.math.MathUtils;
import nl.br.map.MapUtils;
import nl.br.panels.DrawPanel;

public class Block extends Rectangle implements Drawable, Combineable<Block> {
	public final int type;
	public int bix, biy, escalation;
	
	private SpriteMap spriteMap;
	public BufferedImage floorTexture;
	private BufferedImage roofTexture, pillarTexture;
	
	private GradientPaint upGrad, rightGrad, downGrad, leftGrad;	
	
	public Rectangle floorRectangle;
	
	private final Point center = new Point();
	
	public Room room;
	
	public Block(int ix, int iy, int escalation, int type, Room room) {
		super(room.getX()+ix*getScale(), room.getY()+(iy-escalation)*getScale(), getScale(), getScale());
		this.type = type;
		this.escalation = escalation;
		this.bix = ix;
		this.biy = iy;
		this.room = room;
		
		floorRectangle = new Rectangle(ix*getScale(), iy*getScale(), getScale(), getScale());
		
		center.setLocation(this.x+width/2, this.y+(height*(escalation+1))/2);
		
		if (type != 0) {
			spriteMap = MapUtils.getSpriteMapByType(type);
			floorTexture = spriteMap.getSprite(MathUtils.randInt(0, spriteMap.getWidth()-1), 2);
			roofTexture = spriteMap.getSprite(MathUtils.randInt(0, spriteMap.getWidth()-1), 0);
			pillarTexture = getPillar();
			
			upGrad = new GradientPaint(x, y, new Color(0,0,0,50), x, y+getScale()/4, new Color(0,0,0,0));
			rightGrad = new GradientPaint(x+(getScale()/4)*3, y, new Color(0,0,0,0), x+getScale(), y, new Color(0,0,0,50));
			downGrad = new GradientPaint(x, y+(getScale()/4)*3, new Color(0,0,0,0), x, y+getScale(), new Color(0,0,0,50));
			leftGrad = new GradientPaint(x, y, new Color(0,0,0,50), x+getScale()/4, y, new Color(0,0,0,0));
		}
		
	}
	
	public static int getScale() {
		return DrawPanel.SCALE;
	}
	
	public int getFloorY() {
		return y+escalation*getScale();
	}
	
	public Rectangle getFloorRectangle() {
		if (escalation == 0) {
			return this;
		} else {
			return floorRectangle;
		}
	}
	
	private BufferedImage getPillar() {
		
		BufferedImage pillarTexture = new BufferedImage(MapUtils.blockResolution, (Math.abs(escalation)+1)*MapUtils.blockResolution, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D pillarGraphics = pillarTexture.createGraphics();
		if (escalation > 0) {
			pillarGraphics.drawImage(roofTexture, 0, 0, MapUtils.blockResolution, MapUtils.blockResolution, null);
		}
		
		for (int i = 0; i < Math.abs(escalation); i++) {
			if (escalation > 0) {
				pillarGraphics.drawImage(spriteMap.getSprite(MathUtils.randInt(0, spriteMap.getWidth()-1), 1), 0, (i+1)*MapUtils.blockResolution, MapUtils.blockResolution, MapUtils.blockResolution, null);
			} else {
				pillarGraphics.drawImage(spriteMap.getSprite(MathUtils.randInt(0, spriteMap.getWidth()-1), 1), 0, (i)*MapUtils.blockResolution, MapUtils.blockResolution, MapUtils.blockResolution, null);
			}
		}
		
		if (escalation > 0) {
			GradientPaint gPaint = new GradientPaint(0, MapUtils.blockResolution, new Color(0,0,0,0), 0, escalation*MapUtils.blockResolution, new Color(0,0,0,150));
			pillarGraphics.setPaint(gPaint);
			pillarGraphics.fillRect(0, MapUtils.blockResolution, MapUtils.blockResolution, escalation*MapUtils.blockResolution);
		} else {
			GradientPaint gPaint = new GradientPaint(0, 0, new Color(0,0,0,50), 0, Math.abs(escalation)*MapUtils.blockResolution, new Color(0,0,0,255));
			pillarGraphics.setPaint(gPaint);
			pillarGraphics.fillRect(0, 0, MapUtils.blockResolution, Math.abs(escalation)*MapUtils.blockResolution);
		}
		
		pillarGraphics.dispose();
		return pillarTexture;
	}
	
	public void draw(Graphics2D g2) {
		if (type != 0) {
			
			if (escalation > 0) {
				if (DrawPanel.getBlock(room, bix, biy+1) == null || DrawPanel.getBlock(room, bix, biy+1).escalation < this.escalation) {
					g2.drawImage(pillarTexture, x, y, getScale(), getScale()*(escalation+1), null);
				} else {
					g2.drawImage(roofTexture, x, y, getScale(), getScale(), null);
				}
			} else if (escalation < 0) {
				if (DrawPanel.getBlock(room, bix, biy+Math.abs(escalation)) != null && DrawPanel.getBlock(room, bix, biy+Math.abs(escalation)).escalation == this.escalation) {
					g2.setColor(Color.BLACK);
					g2.fillRect(x, y, getScale(), getScale());
				}
				
				if (DrawPanel.getBlock(room, bix, biy-1).escalation > this.escalation) {
					g2.drawImage(pillarTexture, x, getFloorY(), getScale(), getScale()*(Math.abs(escalation)+1), null);
				}
			}
			
			//Shadow at edges start
			if (escalation >= 0) {
				if (DrawPanel.getBlock(room, bix, biy-1) != null && DrawPanel.getBlock(room, bix, biy-1).type != type | DrawPanel.getBlock(room, bix, biy-1).escalation != escalation) {
					g2.setPaint(upGrad);
					g2.fillRect(x, y, getScale(), getScale()/4);
				}
				
				if (DrawPanel.getBlock(room, bix+1, biy) != null && DrawPanel.getBlock(room, bix+1, biy).type != type | DrawPanel.getBlock(room, bix+1, biy).escalation != escalation ) {
					g2.setPaint(rightGrad);
					g2.fillRect(x+(getScale()/4)*3, y, getScale()/4, getScale());
				}
				
				if (DrawPanel.getBlock(room, bix, biy+1) != null && DrawPanel.getBlock(room, bix, biy+1).type != type | DrawPanel.getBlock(room, bix, biy+1).escalation != escalation) {
					g2.setPaint(downGrad);
					g2.fillRect(x, y+(getScale()/4)*3, getScale(), getScale()/4);
				}
				
				if (DrawPanel.getBlock(room, bix-1, biy) != null && DrawPanel.getBlock(room, bix-1, biy).type != type | DrawPanel.getBlock(room, bix-1, biy).escalation != escalation) {
					g2.setPaint(leftGrad);
					g2.fillRect(x, y, getScale()/4, getScale());
				}
			}
			//Shadow at edges end
			
			
		}
		if (DrawPanel.DEBUG) {
			/*
			g2.setColor(Color.CYAN);
			g2.setFont(new Font("Arial", 0, 8));
			g2.drawString(String.valueOf((int)z), x, y);
			*/
		}
	}

	public int getDrawHeight() {
		if (escalation >= 0) {
			return y+escalation*getScale();
		} else {
			return -999999999;
		}
	}

	
	public Point getCenter() {
		return center;
	}

	public boolean canCombine(Block c) {
		return (c.escalation == 0 && this.escalation == 0) || (c.escalation > 0 && this.escalation > 0) || (c.escalation < 0 && this.escalation < 0);
	}
}
