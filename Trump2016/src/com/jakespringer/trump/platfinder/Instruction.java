package com.jakespringer.trump.platfinder;

public class Instruction {
	// for jumping
	public final double verticalSpeed;
	public final double blocksToMove;
	public final double verticalBlocksBeforeMove;
	
	public Instruction(double v, double b, double vb) {
		verticalSpeed = v;
		blocksToMove = b;
		verticalBlocksBeforeMove = vb;
	}
	
	@Override
	public String toString() {
		return "{"+verticalSpeed+", "+blocksToMove+", "+verticalBlocksBeforeMove+"}";
	}
}
