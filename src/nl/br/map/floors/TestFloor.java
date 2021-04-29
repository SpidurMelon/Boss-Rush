/*
package nl.br.map.floors;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import en.lib.io.IO;
import nl.br.map.floorfilling.Room;
import nl.br.map.floorfilling.props.Prop;
import nl.br.panels.DrawPanel;

public class TestFloor extends Floor {
	
	public TestFloor() {
		super();
	}
	
	public Room[][] stitchRooms() {
		Room[][] result = new Room[floorSize][floorSize];
		spawnRoom = new Room(0, 0, "resources/rooms/Miscellaneous/Spawn");
		rooms.add(spawnRoom);
		
		rooms.add(new Room(0, -50, "resources/rooms/Crossings/Crossing"));
		rooms.add(new Room(0, 50, "resources/rooms/Crossings/Crossing"));
		rooms.add(new Room(50, 0, "resources/rooms/Crossings/Pit"));
		rooms.add(new Room(-50, 0, "resources/rooms/Crossings/Pit"));
		
		rooms.add(new Room(-50, -50, "resources/rooms/Corners/BRCorner"));
		rooms.add(new Room(50, -50, "resources/rooms/Corners/BLCorner"));
		rooms.add(new Room(50, 50, "resources/rooms/Corners/TLCorner"));
		rooms.add(new Room(-50, 50, "resources/rooms/Corners/TRCorner"));
		
		rooms.add(new Room(0, -100, "resources/rooms/Straights/Vertical"));
		rooms.add(new Room(0, 100, "resources/rooms/Straights/Vertical"));
		rooms.add(new Room(100, 0, "resources/rooms/Straights/Horizontal"));
		rooms.add(new Room(-100, 0, "resources/rooms/Straights/Horizontal"));
		
		rooms.add(new Room(0, -150, "resources/rooms/Ts/TBottom"));
		rooms.add(new Room(0, 150, "resources/rooms/Ts/TTop"));
		rooms.add(new Room(150, 0, "resources/rooms/Ts/TLeft"));
		rooms.add(new Room(-150, 0, "resources/rooms/Ts/TRight"));
		
		rooms.add(new Room(-150, -50, "resources/rooms/Straights/Vertical"));
		rooms.add(new Room(-150, 50, "resources/rooms/Straights/Vertical"));
		rooms.add(new Room(150, -50, "resources/rooms/Straights/Vertical"));
		rooms.add(new Room(150, 50, "resources/rooms/Straights/Vertical"));
		
		rooms.add(new Room(50, -150, "resources/rooms/Straights/Horizontal"));
		rooms.add(new Room(-50, -150, "resources/rooms/Straights/Horizontal"));
		rooms.add(new Room(50, 150, "resources/rooms/Straights/Horizontal"));
		rooms.add(new Room(-50, 150, "resources/rooms/Straights/Horizontal"));
		
		rooms.add(new Room(100, -150, "resources/rooms/Ends/Left"));
		rooms.add(new Room(100, 150, "resources/rooms/Ends/Left"));
		rooms.add(new Room(-100, -150, "resources/rooms/Ends/Right"));
		rooms.add(new Room(-100, 150, "resources/rooms/Ends/Right"));
		
		rooms.add(new Room(-150, 100, "resources/rooms/Ends/Top"));
		rooms.add(new Room(150, 100, "resources/rooms/Ends/Top"));
		rooms.add(new Room(-150, -100, "resources/rooms/Ends/Bottom"));
		rooms.add(new Room(150, -100, "resources/rooms/Ends/Bottom"));
		
	}
	
}
*/
