package com.jakespringer.trump.platfinder;

import com.jakespringer.reagan.util.ImmutableTuple2;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Platfinder {

    private boolean[][] map;
    public PlatNode[][] nodeMap;
    private List<PlatNode> nodeList = new LinkedList<>();
    private double jumpSpeed; // blocks / time
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

    public List<Instruction> getShortestPath(int x1, int y1, int x2, int y2) {
//		PlatNode begin = nodeMap[x1][y1];
//		PlatNode end = nodeMap[x2][y2];
//
//		if (begin == null || end == null) return null;
//		List<Instruction> instructionSet = new LinkedList<>();
//
//		List<PlatNode> S = new ArrayList<PlatNode>();
//		S.add(begin);
//		double[] distances = new double[nodeList.size()];
//		List<PlatNode> V = (List<PlatNode>) subList(nodeList, S);
//		ArrayList<ArrayList<PlatNode>> paths = new ArrayList<>();
//		for (int i = 0; i < nodeList.size(); i++) {
//			distances[i] = length(begin, nodeList.get(i));
//			paths.add(new ArrayList<PlatNode>());
//			paths.get(i).add(nodeList.get(i));
//		}
//
//		while (S.size() < nodeList.size()/* && !S.contains(end*/) {
//			List<PlatNode> VmS = (List<PlatNode>) subList(V, S);
//			PlatNode n = VmS.get(0);
//			for (PlatNode c : VmS) {
//				double sc = distances[V.indexOf(c)];
//				double sn = distances[V.indexOf(n)];
//				if (sc < sn) {
//					n = c;
//				}
//			}
//			S.add(n);
//			VmS = (List<PlatNode>) subList(V, S);
//			for (PlatNode v : VmS) {
//				if (distances[nodeList.indexOf(v)] > distances[nodeList.indexOf(n)] + length(n, v)) {
//					distances[nodeList.indexOf(v)] = distances[nodeList.indexOf(n)] + length(n, v);
//					ArrayList<PlatNode> vList = new ArrayList<>();
//					vList.add(v);
//					paths.set(nodeList.indexOf(v), (ArrayList<PlatNode>) combineList(paths.get(nodeList.indexOf(n)), vList));
//				}
//			}
//		}
//
//		PlatNode prev = begin;
//		for (PlatNode np : paths.get(nodeList.indexOf(end))) {
//			Instruction inst = null;
//			for (ImmutableTuple2<PlatNode, Instruction> pn : prev.connections) {
//				System.out.println();
//				if (pn.left.x == np.x && pn.left.y == np.y) {
//					inst = pn.right;
//				}
//			}
//			if (inst == null) throw new NullPointerException();
//			instructionSet.add(inst);
//			prev = np;
//		}
//
//		return instructionSet;
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

    private double length(PlatNode n1, PlatNode n2) {
        return Math.sqrt(((n2.x - n1.x) * (n2.x - n1.x)) + ((n2.y - n1.y) * (n2.y - n1.y)));
    }

    private void cachePossibilities() {
        final double v = jumpSpeed;
        final double h = moveSpeed;
        final double g = gravity;

        final int width = (int) (h * 2 * (v / g));
        final int height = (int) ((v * v) / g);

        possibilities = new boolean[width * height][width][height];
        possibileInstructions = new Instruction[width * height];
//		for (int i=0; i<width; ++i) {
//			possibilities[i] = new boolean[height][width*height];
//			for (int j=0; j<height; ++j) {
//				possibilities[i][j] = new boolean[width*height];
////				Arrays.fill(possibilities[i][j], false);
//			}
//		}

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (i == 0 && j == 0) {
                    continue;
                }

                double vy = (i == 0 ? 0 : ((j * h) / i)) + (0.5) * ((g * i) / h);
                if (vy > jumpSpeed) {
                    continue;
                }

                int numPoints = width * 2;
                for (double n = 0; n <= numPoints; n += 1.0) {
                    final double t = n * (width / numPoints);

                    // upper and lower x and y
                    final double lx = h * t;
                    final double ly = vy * t - ((0.5) * (g * g));
                    final double ux = lx + 1.0;
                    final double uy = ly + 1.0;

                    for (int x = (int) lx; x < ux; x++) {
                        for (int y = (int) ly; y < uy; y++) {
                            possibilities[j * width + i][x][y] = true;
                        }
                    }
                }

                Instruction instruct = new Instruction(vy, j, 0);
                possibileInstructions[j * width + i] = instruct;
            }
        }
    }

    private void createNodes() {
        // don't check edges, gets rid of edge cases (pun intended)
        for (int i = 1; i < map.length - 1; ++i) {
            for (int j = 1; j < map[i].length - 1; ++j) {
                if (map[i][j] && (!map[i + 1][j] || !map[i - 1][j]) && (!map[i + 1][j + 1] || !map[i - 1][j + 1])) {
                    addNodeAt(i, j + 1);
                    int k;
                    for (k = j; k >= 0 && !map[i + 1][k]; --k) {
                    }
                    if (k >= 0 && k != j && !map[i + 1][j + 1]) {
                        addNodeAt(i + 1, k + 1);
                    }

                    for (k = j; k >= 0 && !map[i - 1][k]; --k) {
                    }
                    if (k >= 0 && k != j && !map[i - 1][j + 1]) {
                        addNodeAt(i - 1, k + 1);
                    }
                }

                if (map[i][j] && !map[i][j + 1]) {
                    addNodeAt(i, j + 1);
                }
            }
        }
    }

    private void createGraph() {
        for (int i = 0; i < nodeMap.length; ++i) {
            for (int j = 0; j < nodeMap[i].length; ++j) {
                PlatNode node = nodeMap[i][j];
                if (node != null) {
                    for (int x = 0; x < (possibilities[0].length); ++x) {
                        for (int y = 0; y < (possibilities[x][0].length); ++y) {
                            for (int xmultpilier = -1; xmultpilier < 2; xmultpilier += 2) {
                                for (int ymultpilier = -1; ymultpilier < 2; ymultpilier += 2) {
                                    if (i + x * xmultpilier < 0 || i + x * xmultpilier >= nodeMap.length
                                            || j + y * ymultpilier < 0 || j + y * ymultpilier >= nodeMap[0].length) {
                                        continue;
                                    }
                                    PlatNode other = nodeMap[i + x * xmultpilier][j + y * ymultpilier];
                                    if (other != null && (other.x != node.x || other.y != node.y)) {
                                        // first check if you can walk to it
                                        boolean canWalk = true;
                                        for (int k = 0; (other.x > node.x) ? (k < other.x - node.x) : (k > other.x - node.x); k += (other.x > node.x) ? 1 : -1) {
                                            if (!map[node.x + k][node.y - 1]) {
                                                canWalk = false;
                                            }
                                        }

                                        if (canWalk) {
                                            // TODO: implement
                                        }

                                        // second: check if you can jump to it
//										System.out.println(possibilities[0].length);
                                        boolean[][] pattern = possibilities[y * possibilities[0].length + x];
//										System.out.println((y*possibilities[0].length+x) + " " + pattern);
                                        for (int k = 0; k < possibilities[0][0].length - y; ++k) {
                                            if (node.y + k > map[0].length - 1) {
                                                break;
                                            }
                                            if (!checkCollision(1, pattern, node.x, node.y + k, other.x, other.y)) {
                                                Instruction ist = possibileInstructions[y * possibilities[0].length + x];
                                                if (ist != null) {
                                                    node.connections.add(new ImmutableTuple2<>(other, new Instruction(ist.verticalSpeed, ist.blocksToMove, k)));
                                                    break;
                                                }
                                            }
                                        }

                                        for (int k = 0; k < possibilities[0][0].length - y; ++k) {
                                            if (node.y + k > map[0].length - 1) {
                                                break;
                                            }
                                            if (!checkCollision(2, pattern, node.x, node.y + k, other.x, other.y)) {
                                                Instruction ist = possibileInstructions[(y + k) * possibilities[0].length + x];
                                                if (ist != null) {
                                                    node.connections.add(new ImmutableTuple2<>(other, new Instruction(ist.verticalSpeed, ist.blocksToMove, k)));
                                                    break;
                                                }
                                            }
                                        }

                                        for (int k = 0; k < possibilities[0][0].length - y; ++k) {
                                            if (node.y + k > map[0].length - 1) {
                                                break;
                                            }
                                            if (!checkCollision(3, pattern, node.x, node.y + k, other.x, other.y)) {
                                                Instruction ist = possibileInstructions[(y + k) * possibilities[0].length + x];
                                                if (ist != null) {
                                                    node.connections.add(new ImmutableTuple2<>(other, new Instruction(ist.verticalSpeed, -ist.blocksToMove, k)));
                                                    break;
                                                }
                                            }
                                        }

                                        for (int k = 0; k < possibilities[0][0].length - y; ++k) {
                                            if (node.y + k > map[0].length - 1) {
                                                break;
                                            }
                                            if (!checkCollision(4, pattern, node.x, node.y + k, other.x, other.y)) {
                                                Instruction ist = possibileInstructions[(y + k) * possibilities[0].length + x];
                                                if (ist != null) {
                                                    node.connections.add(new ImmutableTuple2<>(other, new Instruction(ist.verticalSpeed, -ist.blocksToMove, k)));
                                                    break;
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

    private boolean checkCollision(int plane, boolean[][] pattern, int x1, int y1, int x2, int y2) {
        boolean collides = false;
        if (x2 > x1) {
            if (y2 >= y1 && plane == 1) {
                // upper right quadrant
                CHECK_COLLIDES:
                for (int f = 0; f < pattern.length; ++f) {
                    for (int g = 0; g < pattern[f].length; ++g) {
                        if (f + x1 < map.length && g + y1 < map[0].length) {
                            if (pattern[f][g] && map[f + x1][g + y1]) {
                                collides = true;
                                break CHECK_COLLIDES;
                            }
                        }
                    }
                }
            } else if (plane == 2) {
                // lower right quadrant
                CHECK_COLLIDES:
                for (int f = 0; f < pattern.length; ++f) {
                    for (int g = 0; g < pattern[f].length; ++g) {
                        if (f + x1 < map.length && -g + y1 < map[0].length) {
                            if (pattern[f][g] && map[f + x1][-g + y1]) {
                                collides = true;
                                break CHECK_COLLIDES;
                            }
                        }
                    }
                }
            }
        } else {
            if (y2 >= y1 && plane == 3) {
                // upper left quadrant
                CHECK_COLLIDES:
                for (int f = 0; f < pattern.length; ++f) {
                    for (int g = 0; g < pattern[f].length; ++g) {
                        if (-f + x1 < map.length && g + y1 < map[0].length) {
                            if (pattern[f][g] && map[-f + x1][g + y1]) {
                                collides = true;
                                break CHECK_COLLIDES;
                            }
                        }
                    }
                }
            } else if (plane == 4) {
                // lower left quadrant
                CHECK_COLLIDES:
                for (int f = 0; f < pattern.length; ++f) {
                    for (int g = 0; g < pattern[f].length; ++g) {
                        if (-f + x1 < map.length && -g + y1 < map[0].length) {
                            if (pattern[f][g] && map[-f + x1][-g + y1]) {
                                collides = true;
                                break CHECK_COLLIDES;
                            }
                        }
                    }
                }
            }
        }

        return collides;
    }

    private void addNodeAt(int x, int y) {
        if (nodeMap[x][y] == null) {
            nodeMap[x][y] = new PlatNode(x, y);
            nodeList.add(nodeMap[x][y]);
        }
    }
}
