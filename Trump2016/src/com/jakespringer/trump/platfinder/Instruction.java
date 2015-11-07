package com.jakespringer.trump.platfinder;

public class Instruction {
	public static enum Type {
		MOVE_LEFT, MOVE_RIGHT,
		JUMP, FALL;
	}
	
	public Type type;
	public double amount;
	public double delay;
	
	public Instruction() {
	}
	
	public Instruction(Instruction other) {
	    type = other.type;
	    amount = other.amount;
	    delay = other.delay;
	}
	
	@Override
	public Instruction clone() {
	    return new Instruction(this);
	}
}
