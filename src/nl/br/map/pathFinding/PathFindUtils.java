package nl.br.map.pathFinding;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import en.lib.math.MathUtils;
import nl.br.map.floorfilling.Corner;
import nl.br.map.floorfilling.Room;
import nl.br.panels.DrawPanel;

public class PathFindUtils {
	
	private static PointPath getShortestPath(Point startPosition, ArrayList<PointPath> paths) {
		PointPath result = null;
		for (PointPath p:paths) {
			if (result == null) {
				result = p;
			} else if (p.getLength(startPosition) < result.getLength(startPosition)) {
				result = p;
			}
		}
		return result;
	}
	
	private static ArrayList<Corner> getSeenCorners(Point p, ArrayList<Corner> pool) {
		ArrayList<Corner> result = new ArrayList<Corner>();
		for (Corner c2:pool) {
			if (!DrawPanel.lineIntersectsArea(new Line2D.Double(p, c2.makeRoom(3, 3)))) {
				result.add(c2);
			}
		}
		return result;
	}
	
	public static Corner getClosestCorner(Point goal, ArrayList<Corner> corners) {
		if (!corners.isEmpty()) {
			Corner result = corners.get(0);
			for (Corner c:corners) {
				if (MathUtils.distPoints(c, goal) < MathUtils.distPoints(result, goal)) {
					result = c;
				}
			}
			return result;
		} else {
			return null;
		}
	}

	public static Point getPathSegment(Point a, Point b, int width, int height) {
		ArrayList<Room> relevantRooms = DrawPanel.currentFloor.getRooms(new Line2D.Double(a.x, a.y, b.x, b.y));
		ArrayList<Corner> corners = new ArrayList<Corner>();
		for (Room r:relevantRooms) {
			corners.addAll(r.corners);
		}
		if (DrawPanel.currentFloor.isInAnyCollisionArea(b.x, b.y) || !DrawPanel.lineIntersectsArea(new Line2D.Double(a, b))) {
			return b;
		} else {
			ArrayList<Corner> cornersASees = getSeenCorners(a, corners);
			if (cornersASees.size() > 0) {
				return getClosestCorner(b, cornersASees).makeRoom(width, height);
			} else {
				//System.err.println("A corner couldnt see other corners, probably due to width and height not being set correctly: DrawPanel." + Thread.currentThread().getStackTrace()[2].getLineNumber());
				return null;
			}
		}
	}
	
	/**
	 * 
	 * @param a
	 * Point 1
	 * @param b
	 * Point 2
	 * @param width
	 * Width of the entity, for fitting purposes
	 * @param height
	 * Height of the entity, for fitting purposes
	 * @return
	 * A path from a to b, while avoiding blocks.
	 * Or a direct line when either a or b is inside of a block.
	 * Or the closest point to b that a could see if something goes wrong halfway-through
	 */
	public static PointPath getPath(Point a, Point b, int width, int height) {
		PointPath result = new PointPath();
		Point firstPathSegment = getPathSegment(a, b, width, height);
		if (firstPathSegment != null) {
			result.add(firstPathSegment);
		} else {
			result.add(b);
			return result;
		}
		while (true) {
			Point nextPathSegment = getPathSegment(result.getFinalPoint(), b, width, height);
			if (nextPathSegment != null && !result.contains(nextPathSegment)) {
				result.add(nextPathSegment);
			} else {
				return result;
			}
			if (nextPathSegment == b) {
				return result;
			}
		}
	}
}
