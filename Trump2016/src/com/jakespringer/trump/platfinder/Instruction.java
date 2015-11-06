package com.jakespringer.trump.platfinder;

public class Instruction {
	public enum Type {
		MOVE_LEFT, MOVE_RIGHT,
		JUMP, FALL;
	}
	
	public Type type;
	public double amount;
	public double delay;
}
