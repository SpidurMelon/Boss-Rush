package nl.br.map.floorfilling;

import java.awt.Point;

import nl.br.entities.MoveableEntity;

public class Corner extends Point {
	/**
	 * 0=TopRight,1=BottomRight,2=BottomLeft,3=TopLeft
	 */
	private int type;
	
	public Corner(int x, int y, int type) {
		super(x, y);
		this.type = type;
	}
	public Point makeRoom(int width, int height) {
		if (type == 0) {
			return new Point(x+width, y-height);
		} else if (type == 1) {
			return new Point(x+width, y+height);
		} else if (type == 2) {
			return new Point(x-width, y+height);
		} else if (type == 3) {
			return new Point(x-width, y-height);
		} else {
			return new Point(0, 0);
		}
	}
}
