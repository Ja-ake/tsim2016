package com.jakespringer.trump.network;

import java.util.LinkedList;
import java.util.List;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.World;


public class GameState {
	private List<GameEvent> events = new LinkedList<>();
	
	public synchronized void submit(GameEvent e) {
		events.add(e);
	}
	
	public synchronized String flush() {
		if (events.isEmpty()) return null;
		
		StringBuilder b = new StringBuilder("GAMESTATE");
		for (GameEvent e : events) {
			b.append(" " + e.toMessage());
		}
		
		events.clear();
		
		return b.toString();
	}

	public void process(String[] args, World world) {
		Reagan.onMainThread(() -> {
			if (args[0].equals("GAMESTATE")) {
				for (int i = 1; i < args.length; /* nothing */) {
					switch (args[i]) {
					case "BLOCKCHANGED":
						BlockChangedEvent.handle(args[i + 1], args[i + 2], args[i + 3], args[i + 4]);
						i += 5;
						break;
					case "ROBOTCREATED":
						RobotCreatedEvent.handle(args[i+1], args[i+2]);
						i += 3;
						break;
					case "ROBOTDESTROYED":
						RobotDestroyedEvent.handle(args[i + 1]);
						i += 2;
						break;
					case "ROBOTSTATE":
						RobotStateEvent.handle(args[i + 1], args[i + 2], args[i + 3], args[i + 4], args[i + 5]);
						i += 6;
						break;
					case "PATHFINDINGALTERED":
						PathfindingAlteredEvent.handle(args[i+1], args[i+2], args[i+3]);
						i+=4;
						break;
					default:
						++i;
						break;
					}
				}
			}
		});
	}
}
