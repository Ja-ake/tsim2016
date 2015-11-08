package com.jakespringer.trump.platfinder.old;

import java.util.ArrayList;
import java.util.List;

public class PlatfinderGraph {
	private ArrayList<Node> nodes = new ArrayList<Node>();

	public PlatfinderGraph() {

	}

	public Node create(int newX, int newY) {
		for (Node n : nodes) {
			if (n.x == newX && n.y == newY)
				return n;
		}

		Node newNode = new Node();
		newNode.x = newX;
		newNode.y = newY;
		nodes.add(newNode);
		return newNode;
	}

	public List<Node> getNodeList() {
		return nodes;
	}

	public List<NodeConnector> getShortestPath(Node start, Node end) {
		List<NodeConnector> path = new ArrayList<NodeConnector>();
		if (!(nodes.contains(start) && nodes.contains(end)))
			return path;

		List<Node> S = new ArrayList<Node>();
		S.add(start);
		double[] distances = new double[nodes.size()];
		List<Node> V = (List<Node>) subList(nodes, S);
		ArrayList<ArrayList<Node>> paths = new ArrayList<ArrayList<Node>>();
		for (int i = 0; i < nodes.size(); i++) {
			distances[i] = length(start, nodes.get(i));
			paths.add(new ArrayList<Node>());
			paths.get(i).add(nodes.get(i));
		}

		while (S.size() < nodes.size()/* && !S.contains(end*/) {
			List<Node> VmS = (List<Node>) subList(V, S);
			Node n = VmS.get(0);
			for (Node c : VmS) {
				double sc = distances[V.indexOf(c)];
				double sn = distances[V.indexOf(n)];
				if (sc < sn) {
					n = c;
				}
			}
			S.add(n);
			VmS = (List<Node>) subList(V, S);
			for (Node v : VmS) {
				if (distances[nodes.indexOf(v)] > distances[nodes.indexOf(n)] + length(n, v)) {
					distances[nodes.indexOf(v)] = distances[nodes.indexOf(n)] + length(n, v);
					ArrayList<Node> vList = new ArrayList<Node>();
					vList.add(v);
					paths.set(nodes.indexOf(v), (ArrayList<Node>) combineList(paths.get(nodes.indexOf(n)), vList));
				}
			}
		}

		Node prev = start;
		for (Node np : paths.get(nodes.indexOf(end))) {
			path.add(prev.connections.get(np));

			prev = np;
		}

		return path;
	}

	private <E> List<E> subList(List<E> li, List<E> lr) {
		List<E> newLi = new ArrayList<E>(li);
		List<E> newLr = new ArrayList<E>(lr);
		for (int i = 0; i < newLr.size(); i++) {
			if (newLi.contains(newLr.get(i))) {
				newLi.remove(newLr.get(i));
				i--;
			}
		}

		return newLi;
	}

	private <E> List<E> combineList(List<E> l1, List<E> l2) {
		List<E> r = new ArrayList<E>(l1);
		for (int i = 0; i < l2.size(); i++) {
			r.add(l2.get(i));
		}
		return r;
	}

	private double length(Node n1, Node n2) {   
		if (n1.connections.get(n2) == null) {
			return Double.POSITIVE_INFINITY;
		}

		return Math.sqrt(((n2.x - n1.x) * (n2.x - n1.x)) + ((n2.y - n1.y) * (n2.y - n1.y)));
	}
}
