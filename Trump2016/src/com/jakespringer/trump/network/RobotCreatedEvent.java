package com.jakespringer.trump.network;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.game.Robot;
import com.jakespringer.trump.game.Statue;

public class RobotCreatedEvent implements GameEvent {

	public int id;
	public boolean red;
	
	public RobotCreatedEvent(int _id, boolean _red) {
		id = _id;
		red = _red;
	}
	
	@Override
	public String toMessage() {
		return "ROBOTCREATED" + " " + id + " " + red;
	}

	public static void handle(String... args) {
		Robot r = new Robot(Integer.parseInt(args[0]));
		r.team = Boolean.parseBoolean(args[1]);
		Reagan.world().addAndGet(r)
			.position.set(Statue.statues[Boolean.parseBoolean(args[1])?0:1].position.add(new Vec2(18.0, 18.0)));
	}
}
