package com.jakespringer.trump.network;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.World;
import com.jakespringer.trump.lobster.CommandHandler;
import com.jakespringer.trump.lobster.LobsterClient;

public class MainCommandHandler implements CommandHandler {

	private LobsterClient client;
	private GameState state;
	
	public MainCommandHandler(LobsterClient c) {
		client = c;
		state = new GameState();
		Reagan.periodic(0.2).forEach(() -> {
			String stateString = state.flush();
			if (stateString != null) client.queued.offer(stateString);
		});
	}

	@Override
	public void handle(int id, String... command) {
		final World world = Reagan.world();
		if (command[0].equals("GAMESTATE")) {
			state.process(command, world);
		}
	}
	
	public void submit(GameEvent event) {
		if (!client.dictator) {
			if (event instanceof GameEndedEvent) return;
			if (event instanceof RobotCreatedEvent) return;
			if (event instanceof RobotStateEvent) return;
			if (event instanceof RobotDestroyedEvent) return;
		}
		
		state.submit(event);
	}
}
