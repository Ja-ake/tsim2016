package com.jakespringer.trump.platfinder.old;

import java.util.HashMap;
import java.util.Map;

public class Node {
	public Node() {
		
	}
	
	@Override
    public boolean equals(Object other) {
		if (other instanceof Node) {
			Node on = (Node) other;
			if (on.x == x && on.y == y) return true;
		}
		
		return false;
	}
	
	public Map<Node, NodeConnector> connections = new HashMap<Node, NodeConnector>();
	public int x = 0, y = 0;
}
