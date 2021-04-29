package nl.br.map.floorfilling;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import en.lib.drawing.Drawable;
import en.lib.io.IO;
import nl.br.map.MapUtils;
import nl.br.map.floorfilling.props.Prop;
import nl.br.map.floors.Floor;
import nl.br.panels.DrawPanel;

public class Room implements Drawable {
	public static final int roomSize = 50;
	
	public String config = "";
	public Block[][] blocks;
	public ArrayList<Prop> props = new ArrayList<Prop>();
	private BufferedImage floorTexture;
	
	private ArrayList<Area> areas;
	public ArrayList<Corner> corners = new ArrayList<Corner>();
	
	public Rectangle bounds;
	
	private Floor floor;
	
	public int rix, riy;
	
	public Room(int ix, int iy, String config, Floor floor) {
		this.rix = ix;
		this.riy = iy;
		this.floor = floor;
		this.config = config;
		
		bounds = new Rectangle(ix*roomSize*DrawPanel.SCALE, iy*roomSize*DrawPanel.SCALE, roomSize*DrawPanel.SCALE, roomSize*DrawPanel.SCALE);
		int[][] blockTypes = new int[roomSize][roomSize];
		int[][] blockHeights = new int[roomSize][roomSize];
		ArrayList<Integer> layout = IO.readPositiveArray(IO.getPropertyValue("Layout", config));
		ArrayList<Integer> heights = IO.readPosNegArray(IO.getPropertyValue("Heights", config));
		for (int y = 0; y < roomSize; y++) {
			for (int x = 0; x < roomSize; x++) {
				blockTypes[y][x] = layout.get(x+y*roomSize);
				blockHeights[y][x] = heights.get(x+y*roomSize);
			}
		}
		blocks = new Block[blockTypes.length][blockTypes[0].length];
		for (int y = 0; y < blockTypes.length; y++) {
			for (int x = 0; x < blockTypes[0].length; x++) {
				blocks[y][x] = new Block(x, y, blockHeights[y][x], blockTypes[y][x], this);
			}
		}
		areas = getAreas();
		
		ArrayList<Integer> props = IO.readPositiveArray(IO.getPropertyValue("Props", config));
		for (int y = 0; y < roomSize; y++) {
			for (int x = 0; x < roomSize; x++) {
				if (props.get(x+y*roomSize) != 0) {
					this.props.add(MapUtils.getPropByType(getX()+x*DrawPanel.SCALE, getY()+y*DrawPanel.SCALE, props.get(x+y*roomSize)));
				}
			}
		}
		
		floorTexture = createFloorTexture();
	}
	
	public Room(int ix, int iy, Floor floor, String path) {
		this(ix, iy, IO.readFile(path), floor);
	}
	
	public void updateCorners() {
		ArrayList<Area> allAreas = DrawPanel.getAllCollisionAreas();
		for (Area a:areas) {
			if (a.solid || a.pit) {
				boolean blockTop = a.hasTopSimilarNeighbour(allAreas), blockRight = a.hasRightSimilarNeighbour(allAreas), blockBottom = a.hasBottomSimilarNeighbour(allAreas), blockLeft = a.hasLeftSimilarNeighbour(allAreas);
				if (!blockTop && !blockLeft && !DrawPanel.currentFloor.isInAnyCollisionArea(a.getXInt()-2, a.getYInt()-2)) {
					a.corners.add(new Corner(a.getXInt(), a.getYInt(), 3));
				}
				if (!blockTop && !blockRight && !DrawPanel.currentFloor.isInAnyCollisionArea(a.getXInt()+a.getWidthInt()+2, a.getYInt()-2)) {
					a.corners.add(new Corner(a.getXInt()+a.getWidthInt(), a.getYInt(), 0));
				}
				if (!blockBottom && !blockRight && !DrawPanel.currentFloor.isInAnyCollisionArea(a.getXInt()+a.getWidthInt()+2, a.getYInt()+a.getHeightInt()+2)) {
					a.corners.add(new Corner(a.getXInt()+a.getWidthInt(), a.getYInt()+a.getHeightInt(), 1));
				}
				if (!blockBottom && !blockLeft && !DrawPanel.currentFloor.isInAnyCollisionArea(a.getXInt()-2, a.getYInt()+a.getHeightInt()+2)) {
					a.corners.add(new Corner(a.getXInt(), a.getYInt()+a.getHeightInt(), 2));
				}
				corners.addAll(a.corners);
			}
		}
	}
	
	
	private ArrayList<Area> getAreas() {
		return Area.gridToAreas(blocks, this);
	}
	
	public ArrayList<Area> getCollisionAreas() {
		ArrayList<Area> result = new ArrayList<Area>();
		for (Area a:areas) {
			if (a.solid || a.pit) {
				result.add(a);
			}
		}
		return result;
	}
	
	public boolean isInCollisionArea(int x, int y) {
		for (Area a:getCollisionAreas()) {
			if (a.contains(x, y)) {
				return true;
			}
		}
		return false;
	}
	
	public void draw(Graphics2D g2) {
		if (DrawPanel.DEBUG2) {
			g2.setColor(Color.RED);
			g2.draw(bounds);
		}
	}
	
	public void drawFloor(Graphics2D g2) {
		g2.drawImage(floorTexture, getX(), getY(), roomWidth(), roomHeight(), null);
	}
	
	public BufferedImage createFloorTexture() {
		BufferedImage floorTexture = new BufferedImage((int)(blocks[0].length*MapUtils.blockResolution), (int)(blocks.length*MapUtils.blockResolution), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D floorGraphics = floorTexture.createGraphics();
		for (int y = 0; y < blocks.length; y++) {
			for (int x = 0; x < blocks[0].length; x++) {
				if (blocks[y][x].floorTexture != null) {
					floorGraphics.drawImage(blocks[y][x].floorTexture, (int)(x*MapUtils.blockResolution), (int)(y*MapUtils.blockResolution), (int)MapUtils.blockResolution, (int)MapUtils.blockResolution, null);
				}
			}
		}
		floorGraphics.dispose();
		
		return floorTexture;
	}
	
	public Block getBlock(int ix, int iy) {
		return blocks[iy][ix];
	}
	
	public boolean contains(Point p) {
		return bounds.contains(p);
	}
	
	public Point getStart() {
		return new Point(Integer.valueOf(IO.getPropertyValue("Start_blockX", config))*DrawPanel.SCALE, Integer.valueOf(IO.getPropertyValue("Start_blockY", config))*DrawPanel.SCALE);
	}
	
	public int getX() {
		return rix*roomWidth();
	}
	
	public int getY() {
		return riy*roomHeight();
	}
	
	public static int roomWidth() {
		return roomSize*DrawPanel.SCALE;
	}
	
	public static int roomHeight() {
		return roomSize*DrawPanel.SCALE;
	}

	@Override
	public Point getCenter() {
		return new Point((int)bounds.getCenterX(), (int)bounds.getCenterY());
	}

	@Override
	public int getDrawHeight() {
		return (int)bounds.getMaxY();
	}
}
