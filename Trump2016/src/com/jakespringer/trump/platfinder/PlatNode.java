package com.jakespringer.trump.platfinder;

import java.util.ArrayList;
import java.util.List;

import com.jakespringer.reagan.util.ImmutableTuple2;

public class PlatNode {
	public final int x;
	public final int y;
	
	public final List<ImmutableTuple2<PlatNode, Instruction>> connections = new ArrayList<>();
	
	public PlatNode() {
		x = 0;
		y = 0;
	}
	
	public PlatNode(int _x, int _y) {
		x = _x;
		y = _y;
	}
}
