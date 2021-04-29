package nl.br.map.floorfilling;

import java.awt.Graphics2D;
import java.util.ArrayList;

import en.lib.objects.Collideable;
import nl.br.panels.DrawPanel;

public class Area extends Collideable {
	public boolean solid, pit;
	public ArrayList<Corner> corners = new ArrayList<Corner>();
	public int rx, ry;
	
	public Room room;
	
	public Area(int x, int y, int width, int height, boolean solid, boolean pit, Room room) {
		super(room.getX()+x, room.getY()+y, width, height);
		rx = x;
		ry = y;
		this.solid = solid;
		this.pit = pit;
		this.room = room;
	}
	
	public int getSurface() {
		return (int)(width*height);
	}
	
	public boolean hasTopSimilarNeighbour(ArrayList<Area> areas) {
		ArrayList<Area> neighbours = getSimilarNeighbours(areas);
		for (Area n:neighbours) {
			if (n.x <= this.x && this.getMaxX() <= n.getMaxX() && (int)n.getMaxY() == this.y) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasBottomSimilarNeighbour(ArrayList<Area> areas) {
		ArrayList<Area> neighbours = getSimilarNeighbours(areas);
		for (Area n:neighbours) {
			if (n.x <= this.x && this.getMaxX() <= n.getMaxX() && (int)this.getMaxY() == n.y) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRightSimilarNeighbour(ArrayList<Area> areas) {
		ArrayList<Area> neighbours = getSimilarNeighbours(areas);
		for (Area n:neighbours) {
			if (n.y <= this.y && this.getMaxY() <= n.getMaxY() && (int)this.getMaxX() == n.x) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasLeftSimilarNeighbour(ArrayList<Area> areas) {
		ArrayList<Area> neighbours = getSimilarNeighbours(areas);
		for (Area n:neighbours) {
			if (n.y <= this.y && this.getMaxY() <= n.getMaxY() && (int)n.getMaxX() == this.x) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Area> getNeighbours(ArrayList<Area> areas) {
		Area aModified = new Area(rx-2, ry-2, getWidthInt()+4, getHeightInt()+4, false, false, room);
		ArrayList<Area> result = new ArrayList<Area>();
		for (Area area:areas) {
			if (area != this && area.intersects(aModified)) {
				result.add(area);
			}
		}
		return result;
	}
	
	public ArrayList<Area> getSimilarNeighbours(ArrayList<Area> areas) {
		Area aModified = new Area(rx-2, ry-2, getWidthInt()+4, getHeightInt()+4, false, false, room);
		ArrayList<Area> result = new ArrayList<Area>();
		for (Area area:areas) {
			if (area != this && area.intersects(aModified) && area.solid == this.solid && area.pit == this.pit) {
				result.add(area);
			}
		}
		return result;
	}
	
	public ArrayList<Area> combine(Area a) {
		int maxCurrentSurface = 0;
		if (this.getSurface() > a.getSurface()) {
			maxCurrentSurface = this.getSurface();
		} else {
			maxCurrentSurface = a.getSurface();
		}
		ArrayList<Area> result = new ArrayList<Area>();
		int minX1=rx, minY1=ry, maxX1=(int)(rx+width), maxY1=(int)(ry+height);
		int minX2=a.rx, minY2=a.ry, maxX2=(int)(a.rx+a.width), maxY2=(int)(a.ry+a.height);
		int minX3, minY3, maxX3, maxY3;
		
		if (minX1 >= minX2) {
			minX3 = minX1;
		} else {
			minX3 = minX2;
		}
		
		if (minY1 <= minY2) {
			minY3 = minY1;
		} else {
			minY3 = minY2;
		}
		
		if (maxX1 <= maxX2) {
			maxX3 = maxX1;
		} else {
			maxX3 = maxX2;
		}
		
		if (maxY1 >= maxY2) {
			maxY3 = maxY1;
		} else {
			maxY3 = maxY2;
		}
		
		Area biggestCombinedArea = new Area(minX3, minY3, maxX3-minX3, maxY3-minY3, this.solid, this.pit, room);
		if (biggestCombinedArea.getSurface() > maxCurrentSurface) {
			result.add(biggestCombinedArea);
			if (minX1 < minX3) {
				result.add(new Area(minX1, minY1, minX3-minX1, maxY1-minY1, this.solid, this.pit, room));
			}
			if (maxX1 > maxX3) {
				result.add(new Area(maxX3, minY1, maxX1-maxX3, maxY1-minY1, this.solid, this.pit, room));
			}
			if (minY1 < minY3) {
				result.add(new Area(minX1, minY1, maxX1-minX1, minY3-minY1, this.solid, this.pit, room));
			}
			if (maxY1 > maxY3) {
				result.add(new Area(minX1, maxY3, maxX1-minX1, maxY1-maxY3, this.solid, this.pit, room));
			}
			
			
			if (minX2 < minX3) {
				result.add(new Area(minX2, minY2, minX3-minX2, maxY2-minY2, this.solid, this.pit, room));
			}
			if (maxX2 > maxX3) {
				result.add(new Area(maxX3, minY2, maxX2-maxX3, maxY2-minY2, this.solid, this.pit, room));
			}
			if (minY2 < minY3) {
				result.add(new Area(minX2, minY2, maxX2-minX2, minY3-minY2, this.solid, this.pit, room));
			}
			if (maxY2 > maxY3) {
				result.add(new Area(minX2, maxY3, maxX2-minX2, maxY2-maxY3, this.solid, this.pit, room));
			}
		} else {
			result.add(this);
			result.add(a);
		}
		return result;
	}
	
	public static ArrayList<Area> gridToAreas(Block[][] blocks, Room room) {
		ArrayList<Area> result = new ArrayList<Area>();
		for (int y = 0; y < blocks.length; y++) {
			int areaStartX = 0;
			for (int x = 0; x < blocks[y].length; x++) {
				if (!blocks[y][areaStartX].canCombine(blocks[y][x])) {
					result.add(new Area(areaStartX*DrawPanel.SCALE, y*DrawPanel.SCALE, ((x)-areaStartX)*DrawPanel.SCALE, DrawPanel.SCALE, blocks[y][areaStartX].escalation > 0, blocks[y][areaStartX].escalation < 0, room));
					areaStartX = x;
				}
			}
			result.add(new Area(areaStartX*DrawPanel.SCALE, y*DrawPanel.SCALE, ((blocks[y].length)-areaStartX)*DrawPanel.SCALE, DrawPanel.SCALE, blocks[y][areaStartX].escalation > 0, blocks[y][areaStartX].escalation < 0, room));
		}
		return optimizeAreas(result);
	}
	
	private static ArrayList<Area> optimizeAreas(ArrayList<Area> areas) {
		ArrayList<Area> result = (ArrayList<Area>)areas.clone();
		boolean optimized = false;
		while (!optimized) {
			optimized = true;
			for (int i = 0; i < result.size()-1; i++) {
				Area toCombine1 = result.get(i);
				ArrayList<Area> neighbours = toCombine1.getSimilarNeighbours(result);
				for (int j = 0; j < neighbours.size(); j++) {
					Area toCombine2 = neighbours.get(j);
					ArrayList<Area> resultClone = (ArrayList<Area>)result.clone();
					result.remove(toCombine1);
					result.remove(toCombine2);
					result.addAll(toCombine1.combine(toCombine2));
					if (!result.containsAll(resultClone)) {
						optimized = false;
						break;
					}
				}
				if (!optimized) {
					break;
				}
			}
		}
		return result;
	}
}
