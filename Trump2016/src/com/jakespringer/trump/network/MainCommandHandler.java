package com.jakespringer.trump.network;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.World;
import com.jakespringer.trump.game.Robot;
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
		} else if (command[0].equals("JOINED")) {
			client.queued.offer("RED " + !NetworkedMain.buildMenu.team.get());
			Reagan.world().stream().forEach(e -> {
				if (e instanceof Robot) {
					Robot r = (Robot) e;
					submit(new RobotCreatedEvent(r.id, r.team));
				}
			});
		} else if (command[0].equals("RED")) {
			boolean red = Boolean.parseBoolean(command[1]);
			if (NetworkedMain.buildMenu != null) NetworkedMain.buildMenu.team.set(red);
			else NetworkedMain.bm = red;
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
