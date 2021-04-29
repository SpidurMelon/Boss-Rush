package nl.br.map.pathFinding;

import java.awt.Point;
import java.util.ArrayList;

import en.lib.math.MathUtils;
import en.lib.math.Vector;

public class PointPath extends ArrayList<Point> {
	
	public Point getFinalPoint() {
		return this.get(this.size()-1);
	}
	
	public double getLength(Point startPosition) {
		double length = 0;
		length += MathUtils.distPoints(startPosition, this.get(0));
		for (int i = 0; i < this.size()-1; i++) {
			length += MathUtils.distPoints(this.get(i), this.get(i+1));
		}
		return length;
	}
	
	public Point getPoint(Point startPosition, double walkSpeed, double walkAccuracy, double deltaPassed) {
		double totalDistanceToWalk = deltaPassed*walkSpeed;
		double toWalk = totalDistanceToWalk;
		
		if (toWalk > MathUtils.distPoints(startPosition, this.get(0))) {
			toWalk -= MathUtils.distPoints(startPosition, this.get(0));
		} else {
			Vector lastLine = new Vector(Vector.getVectorDirection(startPosition, this.get(0)), toWalk);
			return new Point((int)(startPosition.x+lastLine.getXComp()), (int)(startPosition.y+lastLine.getYComp()));
		}
		
		//TODO Factor in walkAccuracy into toWalk
		for (int i = 0; i < this.size()-1; i++) {
			if (toWalk > MathUtils.distPoints(this.get(i), this.get(i+1))) {
				toWalk -= MathUtils.distPoints(this.get(i), this.get(i+1));
			} else {
				Vector lastLine = new Vector(Vector.getVectorDirection(this.get(i), this.get(i+1)), toWalk);
				return new Point((int)(this.get(i).x+lastLine.getXComp()), (int)(this.get(i).y+lastLine.getYComp()));
			}
		}
		
		return null;
	}
}
