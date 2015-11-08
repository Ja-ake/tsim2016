package com.jakespringer.trump.platfinder;

import java.security.InvalidParameterException;

import com.jakespringer.reagan.util.ImmutableTuple2;

public class Platfinder {
	private boolean[][] map;
	public PlatNode[][] nodeMap;
	private double jumpSpeed; // blocks
	private double moveSpeed; // blocks / time 
	private double gravity; // blocks / time^2
	
	private boolean[][][] possibilities;
	private Instruction[] possibileInstructions;
	
	public Platfinder(boolean[][] blocks, double jump, double move, double grav) {
		map = blocks.clone();
		nodeMap = new PlatNode[map.length][map[0].length];
		jumpSpeed = jump;
		moveSpeed = move;
		gravity = grav;
		
		cachePossibilities();
		createNodes();
		createGraph();
	}
	
	public void add(int x, int y) {
		if (x < 0 || y < 0 || x >= map.length || y >= map[x].length) {
			throw new InvalidParameterException("x and y must be in the bounds of the map");
		}
	}
	
	public void remove(int x, int y) {
		if (x < 0 || y < 0 || x >= map.length || y >= map[x].length) {
			throw new InvalidParameterException("x and y must be in the bounds of the map");
		}
	}
	
	private void cachePossibilities() {
		final double v = jumpSpeed;
		final double h = moveSpeed;
		final double g = gravity;
		
		final int width = (int) (h * 2 * (v/g));
		final int height = (int) ((v*v) / g);
		
		possibilities = new boolean[width*height][width][height];
		possibileInstructions = new Instruction[width*height];
//		for (int i=0; i<width; ++i) {
//			possibilities[i] = new boolean[height][width*height];
//			for (int j=0; j<height; ++j) {
//				possibilities[i][j] = new boolean[width*height];
////				Arrays.fill(possibilities[i][j], false);
//			}
//		}
		
		for (int i=0; i<width; ++i) {
			for (int j=0; j<height; ++j) {
				if (i==0 && j==0) continue;
				
				double vy = (i == 0 ? 0 : ((j*h)/i)) + (0.5)*((g*g*i)/h);
				if (vy > jumpSpeed) continue;
				
				int numPoints = width*2;
				for (double n=0; n<=numPoints; n+=1.0) {
					final double t = n*(width/numPoints);
					
					// upper and lower x and y
					final double lx = h*t;
					final double ly = vy*t - ((0.5)*(g*g));
					final double ux = lx+1.0;
					final double uy = ly+1.0;
					
					for (int x = (int) lx; x < ux; x++) {
						for (int y = (int) ly; y < uy; y++) {
							possibilities[j*width+i][x][y] = true;
						}
					}
					
				}
				
				Instruction instruct = new Instruction(vy, j);					
				possibileInstructions[j*width+i] = instruct;
			}
		}
	}
	
	private void createNodes() {
		// don't check edges, gets rid of edge cases (pun intended)
		for (int i=1; i<map.length-1; ++i) {
			for (int j=0; j<map[i].length-1; ++j) {
				if (map[i][j] && (!map[i+1][j] || !map[i-1][j]) && !map[i][j+1]) {
					addNodeAt(i, j+1);
					int k;
					for (k=j; k>=0 && !map[i+1][k]; --k) {
						
					}
					if (k>=0) addNodeAt(i+1, k+1);
					
					for (k=j; k>=0 && !map[i-1][k]; --k) {
						
					}
					if (k>=0) addNodeAt(i-1, k+1);
				}
			}
		}
	}
	
	private void createGraph() {
		for (int i=0; i<nodeMap.length; ++i) {
			for (int j=0; j<nodeMap[i].length; ++j) {
				PlatNode node = nodeMap[i][j];
				if (node != null) {
					for (int x=0; x<(possibilities.length); ++x) {
						for (int y=0; y<(possibilities[x].length); ++y) {
							for (int xmultpilier=-1; xmultpilier<2; xmultpilier+=2) {
								for (int ymultpilier=-1; ymultpilier<2; ymultpilier+=2) {
									if (i+x*xmultpilier < 0 || i+x*xmultpilier >= nodeMap.length 
											|| j+y*ymultpilier < 0 || j+y*ymultpilier >= nodeMap[0].length) continue;
									PlatNode other = nodeMap[i+x*xmultpilier][j+y*ymultpilier];
									if (other != null) {
										// first check if you can walk to it
										// TODO: do this
										
										// second: check if you can jump to it
										boolean[][] pattern = possibilities[y*possibilities[0].length+x];
										
										boolean collides = false;
										if (other.x >= node.x) {
											if (other.y >= node.y) {
												// upper right quadrant
												CHECK_COLLIDES:
												for (int f=0; f<pattern.length; ++f) {
													for (int g=0; g<pattern[f].length; ++g) {
														if (f+node.x < map.length && g+node.y < map[0].length) {
															if (pattern[f][g] && map[f+node.x][g+node.y]) {
																collides = true;
																break CHECK_COLLIDES;
															}
														}
													}
												}
											
												if (!collides) {
													// jump
													Instruction reference = possibileInstructions[y*possibilities[0].length+x];
													if (reference != null) {
														node.connections.add(new ImmutableTuple2<>(other, new Instruction(reference.verticalSpeed, reference.blocksToMove)));
													}
												}
											} else {
												// lower right quadrant
												CHECK_COLLIDES:
												for (int f=0; f<pattern.length; ++f) {
													for (int g=0; g<pattern[f].length; ++g) {
														if (f+node.x < map.length && -g+node.y < map[0].length) {
															if (pattern[f][g] && map[f+node.x][-g+node.y]) {
																collides = true;
																break CHECK_COLLIDES;
															}
														}
													}
												}
											
												if (!collides) {
													// jump
													Instruction reference = possibileInstructions[y*possibilities[0].length+x];
													if (reference != null) {
														node.connections.add(new ImmutableTuple2<>(other, new Instruction(reference.verticalSpeed, -reference.blocksToMove)));
													}
												}
											}
										} else {
											if (other.y >= node.y) {
												// upper left quadrant
												CHECK_COLLIDES:
												for (int f=0; f<pattern.length; ++f) {
													for (int g=0; g<pattern[f].length; ++g) {
														if (-f+node.x < map.length && g+node.y < map[0].length) {
															if (pattern[f][g] && map[-f+node.x][g+node.y]) {
																collides = true;
																break CHECK_COLLIDES;
															}
														}
													}
												}
											
												if (!collides) {
													// jump
													Instruction reference = possibileInstructions[y*possibilities[0].length+x];
													if (reference != null) {
														node.connections.add(new ImmutableTuple2<>(other, new Instruction(reference.verticalSpeed, reference.blocksToMove)));
													}
												}
											} else {
												// lower left quadrant
												CHECK_COLLIDES:
												for (int f=0; f<pattern.length; ++f) {
													for (int g=0; g<pattern[f].length; ++g) {
														if (-f+node.x < map.length && -g+node.y < map[0].length) {
															if (pattern[f][g] && map[-f+node.x][-g+node.y]) {
																collides = true;
																break CHECK_COLLIDES;
															}
														}
													}
												}
											
												if (!collides) {
													// jump
													Instruction reference = possibileInstructions[y*possibilities[0].length+x];
													if (reference != null) {
														node.connections.add(new ImmutableTuple2<>(other, new Instruction(reference.verticalSpeed, -reference.blocksToMove)));
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void addNodeAt(int x, int y) {
		if (nodeMap[x][y] == null) nodeMap[x][y] = new PlatNode(x, y);
	}
}
