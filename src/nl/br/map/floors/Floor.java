package nl.br.map.floors;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import en.lib.io.IO;
import en.lib.math.MathUtils;
import nl.br.main.BossRush;
import nl.br.map.MapUtils;
import nl.br.map.floorfilling.Block;
import nl.br.map.floorfilling.Corner;
import nl.br.map.floorfilling.Room;
import nl.br.map.floorfilling.props.Prop;
import nl.br.panels.DrawPanel;
import nl.br.panels.LoadingScreen;

public class Floor {
	public static final int floorSize = 20;
	public ArrayList<Room> allRooms = new ArrayList<Room>();
	public Room[][] rooms;
	public Room spawnRoom;
	public Rectangle bounds = new Rectangle(0, 0, floorSize*Room.roomSize*DrawPanel.SCALE, floorSize*Room.roomSize*DrawPanel.SCALE);
	public static int spawnX = floorSize/2, spawnY = floorSize/2;
	
	public Floor(String path) {
		rooms = loadFloor(path);
		
		for (int y = 0; y < floorSize; y++) {
			for (int x = 0; x < floorSize; x++) {
				if (rooms[y][x] != null) {
					allRooms.add(rooms[y][x]);
				}
			}
		}
	}
	
	public Floor() {
		FloorConfig floorConfig = new FloorConfig("resources/floors/Floor1/Config");
		rooms = generateFloor(floorConfig);
		
		for (int y = 0; y < floorSize; y++) {
			for (int x = 0; x < floorSize; x++) {
				if (rooms[y][x] != null) {
					allRooms.add(rooms[y][x]);
				}
			}
		}
	}
	
	public Block getBlock(Room r, int ix, int iy) {
		if (r != null && ix > 0 && ix < Room.roomSize && iy > 0 && iy < Room.roomSize) {
			return r.getBlock(ix, iy);
		} else {
			int rix = 0, riy = 0, bix = ix, biy = iy;
			if (ix < 0) {
				rix = -1;
				bix = 50+ix;
			} else if (ix >= Room.roomSize) {
				rix = 1;
				bix = ix-50;
			}
			if (iy < 0) {
				riy = -1;
				biy = 50+iy;
			} else if (iy >= Room.roomSize) {
				riy = 1;
				biy = iy-50;
			}
			Room inRoom = getRoom(r.rix+rix, r.riy+riy);
			if (inRoom != null) {
				return inRoom.getBlock(bix, biy);
			} else {
				return null;
			}
		}
	}
	
	public Room getRoom(Point p) {
		for (Room r:allRooms) {
			if (r.contains(p)) {
				return r;
			}
		}
		//System.err.println("Could not find room on x="+x+",y="+y);
		return null;
	}
	
	public Room getRoom(int ix, int iy) {
		if (ix > 0 && iy > 0 && ix < floorSize && iy < floorSize) {
			return rooms[iy][ix];
		} else {
			return null;
		}
	}
	
	public boolean isInAnyCollisionArea(int px, int py) {
		for (Room r:allRooms) {
			if (r.isInCollisionArea(px, py)) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Room> getRooms(Line2D line) {
		ArrayList<Room> result = new ArrayList<Room>();
		for (Room r:allRooms) {
			if (r.bounds.intersectsLine(line)) {
				result.add(r);
			}
		}
		return result;
	}
	
	public void drawFloor(Graphics2D g2) {
		for (Room r:allRooms) {
			//g2.drawImage(floorTexture, (int)(minIXOffset*DrawPanel.SCALE), (int)(minIYOffset*DrawPanel.SCALE), (int)(blocks[0].length*DrawPanel.SCALE), (int)(blocks.length*DrawPanel.SCALE), null);
			if (DrawPanel.extraLargeRenderBox.contains(r.getCenter())) {
				r.drawFloor(g2);
			}
			
		}
	}
	
	public Room[][] generateFloor(FloorConfig config) {
		Room[][] result = new Room[floorSize][floorSize];
		String[][] roomTypes = config.generateNiceFloor();
		LoadingScreen.loadingLabel = "Loading rooms..."; 
		for (int y = 0; y < floorSize; y++) {
			for (int x = 0; x < floorSize; x++) {
				//System.out.println("Loading rooms: " + MathUtils.roundToDecimals((y*Floor.floorSize+x)/(double)(Floor.floorSize*Floor.floorSize), 2)*100 + "%");
				if (roomTypes[y][x] != null) {
					LoadingScreen.loadPercent = (y*Floor.floorSize+x)/(double)(Floor.floorSize*Floor.floorSize);
					BossRush.loadingScreen.paintImmediately(0, 0, BossRush.WIDTH, BossRush.HEIGHT);
					result[y][x] = new Room(x, y, roomTypes[y][x], this);
				}
			}
		}
		spawnRoom = result[spawnY][spawnX];
		return result;
	}
	
	public Room[][] loadFloor(String path) {
		Room[][] result = new Room[floorSize][floorSize];
		String floorConfig = IO.readFile(path);
		ArrayList<Integer> roomTypes = IO.readPositiveArray(IO.getPropertyValue("Layout", floorConfig));
		
		int spawnType = Integer.valueOf(IO.getPropertyValue("Spawn", floorConfig));
		for (int y = 0; y < floorSize; y++) {
			for (int x = 0; x < floorSize; x++) {
				if (roomTypes.get(x+y*floorSize) != 0) {
					result[y][x] = new Room(x, y, "resources/rooms/" + IO.getPropertyValue(String.valueOf(roomTypes.get(x+y*floorSize)), floorConfig), this);
					if (roomTypes.get(x+y*floorSize) == spawnType) {
						spawnRoom = result[y][x];
					}
				}
			}
		}
		
		return result;
	}
	
	
	public Point getStart() {
		if (IO.getPropertyValue("Start_blockX", spawnRoom.config) != null) {
			return new Point(spawnRoom.getX()+Integer.valueOf(IO.getPropertyValue("Start_blockX", spawnRoom.config))*DrawPanel.SCALE, spawnRoom.getY()+Integer.valueOf(IO.getPropertyValue("Start_blockY", spawnRoom.config))*DrawPanel.SCALE);
		} else {
			return new Point(spawnRoom.getCenter().x, spawnRoom.getCenter().y);
		}
	}
	
}
