package com.jakespringer.trump.network;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.reagan.util.Mutable;
import com.jakespringer.trump.game.Robot;

public class RobotStateEvent implements GameEvent {

	public int id;
	public double x;
	public double y;
	public double xvel;
	public double yvel;
	public boolean red;
	
	public RobotStateEvent(int _id, double _x, double _y, double _xvel, double _yvel, boolean _red) {
		id = _id;
		x = _x;
		y = _y;
		xvel = _xvel;
		yvel = _yvel;
		red = _red;
	}
	
	@Override
	public String toMessage() {
		return "ROBOTSTATE" + " " + id + " " + x + " " + y + " " + xvel + " " + yvel + " " + red;
	}

	public static void handle(String... args) {
		int id = Integer.parseInt(args[0]);
		double x = Double.parseDouble(args[1]);
		double y = Double.parseDouble(args[2]);
		double xvel = Double.parseDouble(args[3]);
		double yvel = Double.parseDouble(args[4]);
		boolean red = Boolean.parseBoolean(args[5]);
		
		Mutable<Boolean> placed = new Mutable<>(false);
		Reagan.world().stream().forEach(e -> {
			if (e instanceof Robot) {
				Robot r = (Robot)e;
				if (r.id == id) {
					placed.o = true;
					r.position.set(new Vec2(x,y));
					r.velocity.set(new Vec2(xvel, yvel));
				}
			}
		});
		
		if (!placed.o) {
			Robot r = new Robot();
			r.id = id;
			r.position.set(new Vec2(x, y));
			r.velocity.set(new Vec2(xvel, yvel));
			r.team = red;
		}
	}
}
