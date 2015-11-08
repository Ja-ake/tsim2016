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
		Reagan.world().addAndGet(new Robot(Integer.parseInt(args[0])))
			.position.set(Statue.statues[Boolean.parseBoolean(args[1])?0:1].position);
	}
}
