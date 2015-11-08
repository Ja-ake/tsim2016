package com.jakespringer.trump.platfinder;

import com.jakespringer.reagan.util.ImmutableTuple2;

public class PlatTest {
	public static void main(String[] args) {
		boolean[][] map = {{ false, false, false, false, false },
						   { false, true , false, false, false },
						   { false, false, false, false, false },
						   { false, true , false, true , false },
						   { false, true , false, true , false }};
		
		Platfinder finder = new Platfinder(map, 3, 1, 1);

		for (int i=0; i<finder.nodeMap.length; ++i) {
			for (int j=0; j<finder.nodeMap[i].length; ++j) {
				if (finder.nodeMap[i][j] != null) {
					for (ImmutableTuple2<PlatNode, Instruction> inst : finder.nodeMap[i][j].connections) {
						System.out.println("("+inst.left.x + ", "+inst.left.y+") -> " + "("+i+", "+j+"): "+inst.right.blocksToMove + " " + inst.right.verticalSpeed);
					}
				}
			}
		}
	}
}
