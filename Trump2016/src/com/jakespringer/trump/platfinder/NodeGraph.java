package com.jakespringer.trump.platfinder;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.game.World;
import com.jakespringer.reagan.gfx.FontContainer;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.reagan.util.Mutable;
import com.jakespringer.trump.game.Menu;
import com.jakespringer.trump.game.Tile;
import static com.jakespringer.trump.game.Tile.WallType.SPIKE;
import com.jakespringer.trump.game.Walls;
import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class NodeGraph extends AbstractEntity {

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../Reagne/natives").getAbsolutePath());

        Window.initialize(1200, 800, "Test");

        FontContainer.create();

        final World world = new World();
        world.add(new Menu());
        Reagan.run(world);
    }

    public boolean debug;

    @Override
    public void create() {
        add(Input.whenMouse(2, true).forEach($ -> debug = !debug));
        onUpdate(dt -> {
            if (debug) {
                nodeList.forEach(n -> Graphics2D.fillEllipse(n.p.toVec2(), new Vec2(4, 4), new Color4(1, 1, 0), 8));
                Node n = get(nodeGrid, new Point(Input.getMouse()));
                if (n != null) {
                    n.from.forEach(c -> Graphics2D.drawLine(c.from.p.toVec2(), c.to.p.toVec2(), new Color4(1, 1, 0), 2));
                    n.from.forEach(c -> Graphics2D.fillEllipse(c.to.p.toVec2(), new Vec2(4, 4), Color4.GREEN, 8));
                    //n.from.forEach(c -> Graphics2D.drawText(c.instructions.time + "", c.to.p.toVec2()));
                }
            }

//            if (Menu.buildMenu != null && Menu.buildMenu.selected == null) {
//                Robot.redList.forEach(n -> {
//                    List<Connection> conn = NodeGraph.red.findNearestPath(n.position.get(), Input.getMouse(), Robot.size);
//                    if (conn != null) {
//                        conn.stream().filter(k -> k != null).forEach(k -> Graphics2D.drawLine(k.from.p.toVec2(), k.to.p.toVec2(), Color4.GREEN, 4));
//                    }
//                });
//            }
        });
    }

    public static class Point {

        public final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point(Vec2 v) {
            this((int) (v.x / Walls.walls.wallSize), (int) (v.y / Walls.walls.wallSize));
        }

        public Point add(Point p) {
            return new Point(x + p.x, y + p.y);
        }

        @Override
        public boolean equals(Object other) {
            return x == ((Point) other).x && y == ((Point) other).y;
        }

        public Point multiply(int mx, int my) {
            return new Point(x * mx, y * my);
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        public Vec2 toVec2() {
            return new Vec2(x + .5, y + .5).multiply(Walls.walls.wallSize);
        }
    }

    public static class Node {

        public final Point p;
        public List<Connection> from, to;

        public Node(Point p) {
            this.p = p;
            from = new LinkedList();
            to = new LinkedList();
        }
    }

    public static class Connection {

        public final Node from, to;
        public final Instructions instructions;

        public Connection(Node from, Node to, Instructions instructions) {
            this.from = from;
            this.to = to;
            this.instructions = instructions;
        }
    }

    public static class Instructions {

        public final double jumpSpeed;
        public final double jumpDelay;
        public final double time;

        public Instructions(double jumpSpeed, double jumpDelay, double time) {
            this.jumpSpeed = jumpSpeed;
            this.jumpDelay = jumpDelay;
            this.time = time;
        }
    }

    public static NodeGraph red;
    public static NodeGraph blue;

    public final Tile[][] tileGrid;
    public final boolean team;
    public Boolean[][] grid;
    public Node[][] nodeGrid;
    public List<Node> nodeList;
    public List<Connection> connectionList;
    public int width, height;

    public NodeGraph(Tile[][] tileGrid, boolean team) {
        this.tileGrid = tileGrid;
        this.team = team;
        update();
    }

    private void addConnection(Node n1, Node n2, Instructions instructions) {
        Connection c = new Connection(n1, n2, instructions);
        n1.from.add(c);
        n2.to.add(c);
        connectionList.add(c);
    }

    private void addNode(int x, int y) {
        Node n = new Node(new Point(x, y));
        nodeGrid[x][y] = n;
        nodeList.add(n);
    }

    private void createConnections() {
        nodeList.forEach(n1 -> nodeList.stream().filter(n2 -> n2 != n1).forEach(n2 -> loadConnection(n1, n2)));
    }

    private void createNodes() {
        forEachNoEdges((x, y) -> {
            if (!grid[x][y]) {
                if (grid[x][y - 1]) {
                    if (tileGrid[x][y].type != SPIKE) {
                        addNode(x, y);
                    }
                }
            }
        });
    }

    public List<Connection> findPath(Vec2 pos, Vec2 goal, Vec2 size) {
        Node start = Walls.tilesAt(pos, size).stream().map(t -> get(nodeGrid, new Point(t.x, t.y))).filter(n -> n != null)
                .sorted(Comparator.comparingDouble(t -> t.p.toVec2().subtract(pos).lengthSquared())).findFirst().orElse(null);
        Node end = nodeList.stream().filter(n -> n.p.toVec2().subtract(goal).lengthSquared() < 150 * 150)
                .sorted(Comparator.comparingDouble(n -> n.p.toVec2().subtract(goal).lengthSquared())).findFirst().orElse(null);
        if (start == null || end == null) {
            return null;
        }
        Set<Node> closedSet = new HashSet();

        final Map<Node, Double> best_cost = new HashMap();
        best_cost.put(start, 0.);
        Map<Node, Connection> best_parent = new HashMap();

        Function<Node, Double> heuristic = n -> Walls.walls.wallSize / 150 * Math.abs(end.p.x - n.p.x);

        ToDoubleFunction<Node> expected_cost = n -> {
            Double cost = best_cost.get(n);
            if (cost == null) {
                cost = 99999.;
            }
            return cost + heuristic.apply(n);
        };

        //PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(expected_cost).reversed());
        LinkedList<Node> openSet = new LinkedList();
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Collections.sort(openSet, Comparator.comparingDouble(expected_cost));
            Node current = openSet.poll();
            if (current == end) {
                List<Connection> path = new LinkedList();
                Mutable<Connection> c = new Mutable(best_parent.get(current));
                while (c.o != null) {
                    path.add(0, c.o);
                    c.o = best_parent.get(c.o.from);
                }
                return path;
            }
            closedSet.add(current);
            current.from.stream().filter(c -> !closedSet.contains(c.to)).forEach(c -> {
                double possible_cost = best_cost.get(current) + c.instructions.time;
                Double current_cost = best_cost.get(c.to);
                if (current_cost == null) {
                    openSet.add(c.to);
                    best_parent.put(c.to, c);
                    best_cost.put(c.to, possible_cost);
                } else if (possible_cost < current_cost) {
                    best_parent.put(c.to, c);
                    best_cost.put(c.to, possible_cost);
                }
            });
        }
        return null;
    }

    public List<Connection> findNearestPath(Vec2 pos, Vec2 goal, Vec2 size) {
        List<Connection> attempt = findPath(pos, goal, size);
        if (attempt != null) {
            return attempt;
        }

        Node start = Walls.tilesAt(pos, size).stream().map(t -> get(nodeGrid, new Point(t.x, t.y))).filter(n -> n != null)
                .sorted(Comparator.comparingDouble(t -> t.p.toVec2().subtract(pos).lengthSquared())).findFirst().orElse(null);
        if (start == null) {
            return null;
        }

        Node end = findClosestEndPathRec(start, goal, start, new LinkedList<Node>());
        if (end.p.toVec2().subtract(goal).length() > 200) {
            return null;
        }
        return findPath(pos, end.p.toVec2(), size);
    }

    private Node findClosestEndPathRec(Node start, Vec2 target, Node closest, LinkedList<Node> backtrace) {
        backtrace.add(start);

        double len = start.p.toVec2().subtract(target).lengthSquared();
        double cur = closest.p.toVec2().subtract(target).lengthSquared();
        if (len < cur) {
            closest = start;
        }

        List<Node> toCompare = new LinkedList<>();
        for (Connection next : start.from) {
            if (backtrace.contains(next.to)) {
                continue;
            }
            backtrace.add(next.to);
            Node close = findClosestEndPathRec(next.to, target, closest, backtrace);
            if (close != null) {
                toCompare.add(close);
            }
        }

        try {
            return toCompare.stream().min((a, b) -> {
                double len1 = a.p.toVec2().subtract(target).lengthSquared();
                double len2 = b.p.toVec2().subtract(target).lengthSquared();
                if (len1 > len2) {
                    return 1;
                }
                if (len1 == len2) {
                    return 0;
                }
                return -1;
            }).get();
        } catch (Exception e) {
            return closest;
        }
    }

    private void forEach(BiConsumer<Integer, Integer> f) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                f.accept(x, y);
            }
        }
    }

    private void forEachNoEdges(BiConsumer<Integer, Integer> f) {
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                f.accept(x, y);
            }
        }
    }

    private <O> O get(O[][] a, Point p) {
//        System.out.println(p);
        return a[p.x][p.y];
    }

    private void loadConnection(Node n1, Node n2) {
        int dx = Math.abs(n1.p.x - n2.p.x);
        int dy = n2.p.y - n1.p.y;
        Instructions i = loadInstructions(dx, Math.max(0, dy));
        if (i != null) {
            if (loadPath(dx, dy).stream().map(p -> p.multiply(n1.p.x < n2.p.x ? 1 : -1, 1)).allMatch(p -> !get(grid, n1.p.add(p)))) {
                addConnection(n1, n2, i);
            }
        }
    }

    private Instructions loadInstructions(int dx, int dy) {
        double t1 = dx * Walls.walls.wallSize / 150;
        double v1 = 1500 * t1 / 2;
        double v0 = Math.sqrt(2 * 1500 * dy * Walls.walls.wallSize + v1 * v1);
        double t0 = (v0 - v1) / 1500;
        if (v0 > 600) {
            return null;
        }
        if (dy == 0 && dx <= 1) {
            v0 = 0;
            t0 = -.000001;
        }
        return new Instructions(v0, t0, t0 + t1);
    }

    private List<Point> loadPath(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return new LinkedList();
        }
        List<Point> r;
        if (dy < 0) {
            r = loadPath(dx, dy + 1);
        } else if (dx > 0) {
            r = loadPath(dx - 1, dy);
        } else {
            r = loadPath(dx, dy - 1);
        }
        r.add(new Point(dx, dy));
        return r;
    }

    public void update() {
        width = tileGrid.length;
        height = tileGrid[0].length;
        grid = new Boolean[width][height];
        nodeGrid = new Node[width][height];
        nodeList = new LinkedList();
        connectionList = new LinkedList();

        forEach((x, y) -> grid[x][y] = tileGrid[x][y].isSolid(team));

        createNodes();
        createConnections();
    }
}
