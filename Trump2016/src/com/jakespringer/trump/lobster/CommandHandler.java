package com.jakespringer.trump.lobster;

public interface CommandHandler {
	public void handle(int id, String... command);
}
