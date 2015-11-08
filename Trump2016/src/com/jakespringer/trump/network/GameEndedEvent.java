package com.jakespringer.trump.network;

public class GameEndedEvent implements GameEvent {

	public boolean redWon;
	
	public GameEndedEvent(boolean rwon) {
		redWon = rwon;
	}
	
	@Override
	public String toMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void handle(String[] args) {
		
	}

}
