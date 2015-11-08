package com.jakespringer.trump.network;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.trump.game.Robot;

public class RobotDestroyedEvent implements GameEvent {

	public int id;
	
	public RobotDestroyedEvent(int _id) {
		id = _id;
	}
	
	@Override
	public String toMessage() {
		return "ROBOTDESTROYED" + " " + id;
	}

	public static void handle(String... args) {
		int id = Integer.parseInt(args[0]);
		Reagan.world().removeIf(e -> {
			if (e instanceof Robot) {
				Robot r = (Robot) e;
				return (r.id == id);
			}
			
			return false;
		});
	}
}
