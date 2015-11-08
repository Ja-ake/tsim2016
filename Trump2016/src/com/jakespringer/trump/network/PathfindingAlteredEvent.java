package com.jakespringer.trump.network;

import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.game.Robot;

public class PathfindingAlteredEvent implements GameEvent {
	
	public double x;
	public double y;
	public boolean red;
	
	public PathfindingAlteredEvent(double _x, double _y, boolean _red) {
		x = _x;
		y = _y;
		red = _red;
	}
	
	@Override
	public String toMessage() {
		return "PATHFINDINGALTERED" + " " + x + " " + y + " " + red;
	}

	public static void handle(String... args) {
		if (Boolean.parseBoolean(args[2])) {
			Robot.redGoal = new Vec2(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
		} else {
			Robot.blueGoal = new Vec2(Double.parseDouble(args[0]), Double.parseDouble(args[1]));
		}
	}
}
