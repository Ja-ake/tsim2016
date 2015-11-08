package com.jakespringer.trump.platfinder;

public class Instruction {
	// for jumping
	public final double verticalSpeed;
	public final double blocksToMove;
	
	public Instruction(double v, double b) {
		verticalSpeed = v;
		blocksToMove = b;
	}
}
