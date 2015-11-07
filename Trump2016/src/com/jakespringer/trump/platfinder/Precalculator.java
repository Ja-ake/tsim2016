package com.jakespringer.trump.platfinder;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class Precalculator {
    private boolean[][] blockArray;
    private double jump = -1, gravity = -1, xSpeed = -1;
    public PlatfinderGraph graph;

    public Precalculator() {
        graph = new PlatfinderGraph();
    }

    public void setBlockArray(boolean[][] ba) {
        blockArray = ba;
    }

    public void setJumpSpeed(double j) {
        jump = j;
    }

    public void setGravitySpeed(double g) {
        gravity = g;
    }

    public void setMoveSpeed(double m) {
        xSpeed = m;
    }

    public void precalculate() {
        if (!(jump >= 0 && gravity >= 0 && xSpeed >= 0))
            return;

        createNodes();
        createConnections();
    }

    private void createNodes() {
        for (int x = 0; x < blockArray.length - 1; x++) {
            for (int y = 0; y < blockArray[x].length - 1; y++) {
                System.out.println("hi");
                if (!checkPlayerCollision(x, y) && checkPlayerCollision(x, y - 1)) {
                    if (!checkPlayerCollision(x - 1, y) && !checkPlayerCollision(x - 1, y - 1)) {
                        // left edge
                        graph.create(x, y);

                        int y2 = 0;
                        for (y2 = y - 1; !checkPlayerCollision(x - 1, y2) && y2 >= 0; y2--);
                        graph.create(x - 1, y2 + 1);
                    }

                    if (!checkPlayerCollision(x + 1, y) && !checkPlayerCollision(x + 1, y - 1)) {
                        // right edge
                        graph.create(x, y);

                        int y2 = 0;
                        for (y2 = y - 1; !checkPlayerCollision(x + 1, y2) && y2 >= 0; y2--);
                        graph.create(x + 1, y2 + 1);
                    }
                }
            }
        }
    }

    private void createConnections() {
        int i =0;
        List<Node> nodeList = graph.getNodeList();
        for (Node self : nodeList) {
            for (Node other : nodeList) {
                if (self != other) {
                    System.out.println(++i);
                    createConnection(self, other);
                }
            }
        }
    }

    private void createConnection(Node sn, Node fn) {
        if (checkWalkPath(sn.x, sn.y, fn.x, fn.y)) {
            NodeConnector nctr = new NodeConnector();
            if (fn.x > sn.x) {
                Instruction instr0 = new Instruction();
                instr0.type = Instruction.Type.MOVE_RIGHT;
                instr0.amount = Math.abs(fn.x - sn.x);
                nctr.instructions.add(instr0);
                sn.connections.put(fn, nctr);
            } else {
                Instruction instr0 = new Instruction();
                instr0.type = Instruction.Type.MOVE_LEFT;
                instr0.amount = Math.abs(fn.x - sn.x);
                nctr.instructions.add(instr0);
                sn.connections.put(fn, nctr);
            }

            return;
        }

        if (fn.x > sn.x) {
            try {
                if (checkFallPath(sn.x + 1, sn.y, fn.x, fn.y)) {
                    NodeConnector nctr = new NodeConnector();

                    Instruction instr0 = new Instruction();
                    instr0.type = Instruction.Type.MOVE_RIGHT;
                    instr0.amount = 1;

                    Instruction instr1 = new Instruction();
                    instr1.type = Instruction.Type.FALL;
                    instr1.delay = getFallMoveDelay(sn.x + 1, sn.y, fn.x, fn.y);

                    Instruction instr2 = new Instruction();
                    instr2.type = Instruction.Type.MOVE_RIGHT;
                    instr2.amount = Math.abs(fn.x - (sn.x + 1));

                    nctr.instructions.add(instr0);
                    nctr.instructions.add(instr1);
                    nctr.instructions.add(instr2);
                    sn.connections.put(fn, nctr);

                    return;
                }
            } catch (IllegalMathException e) {

            }
        } else {
            try {
                if (checkFallPath(sn.x - 1, sn.y, fn.x, fn.y)) {
                    NodeConnector nctr = new NodeConnector();

                    Instruction instr0 = new Instruction();
                    instr0.type = Instruction.Type.MOVE_LEFT;
                    instr0.amount = 1;

                    Instruction instr1 = new Instruction();
                    instr1.type = Instruction.Type.FALL;
                    instr1.delay = getFallMoveDelay(sn.x - 1, sn.y, fn.x, fn.y);

                    Instruction instr2 = new Instruction();
                    instr2.type = Instruction.Type.MOVE_LEFT;
                    instr2.amount = Math.abs(fn.x - (sn.x - 1));

                    nctr.instructions.add(instr0);
                    nctr.instructions.add(instr1);
                    nctr.instructions.add(instr2);
                    sn.connections.put(fn, nctr);

                    return;
                }
            } catch (IllegalMathException e) {

            }
        }

        if (fn.x > sn.x) {
            try {
                if (checkJumpPath(sn.x, sn.y, fn.x, fn.y)) {
                    NodeConnector nctr = new NodeConnector();

                    Instruction instr0 = new Instruction();
                    instr0.type = Instruction.Type.JUMP;
                    instr0.delay = getJumpMoveDelay(sn.x, sn.y, fn.x, fn.y);

                    Instruction instr1 = new Instruction();
                    instr1.type = Instruction.Type.MOVE_RIGHT;
                    instr1.amount = Math.abs(fn.x - sn.x);

                    nctr.instructions.add(instr0);
                    nctr.instructions.add(instr1);
                    sn.connections.put(fn, nctr);

                    return;
                }
            } catch (IllegalMathException e) {

            }
        } else {
            try {
                if (checkJumpPath(sn.x, sn.y, fn.x, fn.y)) {
                    NodeConnector nctr = new NodeConnector();

                    Instruction instr0 = new Instruction();
                    instr0.type = Instruction.Type.JUMP;
                    instr0.delay = getJumpMoveDelay(sn.x, sn.y, fn.x, fn.y);

                    Instruction instr1 = new Instruction();
                    instr1.type = Instruction.Type.MOVE_LEFT;
                    instr1.amount = Math.abs(fn.x - sn.x);

                    nctr.instructions.add(instr0);
                    nctr.instructions.add(instr1);
                    sn.connections.put(fn, nctr);

                    return;
                }
            } catch (IllegalMathException e) {

            }
        }
    }

    public boolean export(String filename) {
        if (graph == null)
            return false;

        return true;
    }

    private boolean checkPlayerCollision(int x, int y) {
        if (x > 0 && x < blockArray.length && y > 0 && y < blockArray[x].length) {
            return blockArray[x + 0][y + 0]; // == blockArray[x+1][y+0] ==
                                             // blockArray[x+1][y+1] ==
                                             // blockArray[x+0][y+1] ==
                                             // false;
        } else
            return true;
    }

    private double getJumpHeight() {
        // return ((jump / gravity) * (jump / 2));
        return ((jump * jump) / (2 * gravity)); // more efficient
    }

    private double getJumpHeight(double time) {
        return (jump * time) + ((-gravity * time * time) / 2);
    }

    private double getTimeInAir(int yi, int yf) {
        double deltay = yf - yi;
        return (jump + Math.sqrt((2 * (-gravity) * deltay) + (jump * jump))) / gravity;
    }

    private double getTimeInAirGoing(int yi, int yf) {
        double deltay = yf - yi;
        return (jump - Math.sqrt((2 * (-gravity) * deltay) + (jump * jump))) / gravity;
    }

    private double getJumpMoveDelay(int xi, int yi, int xf, int yf) throws IllegalMathException {
        if (yf - yi > getJumpHeight()) {
            throw new IllegalMathException();
        }

        double timeInAir = getTimeInAir(yi, yf);
        double reqSpeedX = ((double) xf - (double) xi) / timeInAir;
        if (!(xSpeed >= Math.abs(reqSpeedX))) {
            throw new IllegalMathException();
        }

        return timeInAir - Math.abs(((double) xf - (double) xi) / xSpeed);
    }

    private double getFallMoveDelay(int xi, int yi, int xf, int yf) throws IllegalMathException {
        double deltax = xf - xi;
        double deltay = yf - yi;

        if (deltay > 0) {
            throw new IllegalMathException();
        }

        double timeToFall = Math.sqrt(Math.abs((2 * deltay) / gravity));

        if (Math.abs(deltax) > timeToFall * xSpeed) {
            throw new IllegalMathException();
        }

        double fallMoveDelay = timeToFall - (deltax / xSpeed);
        return fallMoveDelay;
    }

    private boolean checkWalkPath(int xi, int yi, int xf, int yf) {
        if (yi != yf)
            return false;

        if (xi < xf) {
            for (; xi <= xf; xi++) {
                if (checkPlayerCollision(xi, yi) || !checkPlayerCollision(xi, yi - 1))
                    return false;
            }
        } else {
            for (; xi >= xf; xi--) {
                if (checkPlayerCollision(xi, yi) || !checkPlayerCollision(xi, yi - 1))
                    return false;
            }
        }

        return true;
    }

    private boolean checkJumpPath(int xi, int yi, int xf, int yf) throws IllegalMathException {
        double moveDelay = getJumpMoveDelay(xi, yi, xf, yf);
        double direction;
        if (xf != xi)
            direction = (xf - xi) / Math.abs(xf - xi);
        else
            direction = 1;
        double[] pointA = { xi, yi + getJumpHeight(moveDelay) };
        double[] pointB = { xi + (getTimeInAirGoing(0, (int) getJumpHeight()) * xSpeed * direction), yi + getJumpHeight() };
        double[] pointC = { xf, yf };
        QuadCurve2D.Double jumpParabola = new QuadCurve2D.Double(pointA[0], pointA[1], pointB[0], pointB[1], pointC[0], pointC[1]);
        Area jumpArea = new Area(jumpParabola);
        jumpArea.add(new Area(new Line2D.Double(xi, yi, pointA[0], pointA[1])));
        Area blockArea = new Area();
        for (int i = (int) Math.floor(jumpArea.getBounds2D().getMinX()); i < (int) Math.ceil(jumpArea.getBounds2D().getMaxX()); i++) {
            for (int j = (int) Math.floor(jumpArea.getBounds2D().getMinY()); j < (int) Math.ceil(jumpArea.getBounds2D().getMaxY()); j++) {
                if (checkPlayerCollision(i, j))
                    blockArea.add(new Area(new Rectangle.Double(i, j, 1, 1)));
            }
        }

        jumpArea.intersect(blockArea);
        return jumpArea.isEmpty();
    }

    private boolean checkFallPath(int xi, int yi, int xf, int yf) throws IllegalMathException {
        double moveDelay = getFallMoveDelay(xi, yi, xf, yf);
        double[] pointA = { xi, yi };
        double[] pointB = { xi, yi + ((-gravity * moveDelay * moveDelay) / 2) };
        double[] pointC = { xf, yf };
        Line2D initialMovement = new Line2D.Double(pointA[0], pointA[1], pointB[0], pointB[1]);
        Line2D finalMovement = new Line2D.Double(pointB[0], pointB[1], pointC[0], pointC[1]);
        Area movement = new Area();

        if (xi <= xf) {
            for (int i = xi; i <= xf; i++) {
                if (yi <= yf) {
                    for (int j = yi; j <= yf; j++) {
                        if (initialMovement.intersects(new Rectangle2D.Double(i, j, 1, 1))) {
                            movement.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                        } else if (finalMovement.intersects(new Rectangle2D.Double(i, j, 1, 1))) {
                            movement.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                        }
                    }
                } else {
                    for (int j = yf; j <= yi; j++) {
                        if (initialMovement.intersects(new Rectangle2D.Double(i, j, 1, 1))) {
                            movement.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                        } else if (finalMovement.intersects(new Rectangle2D.Double(i, j, 1, 1))) {
                            movement.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                        }
                    }
                }
            }
        } else {
            for (int i = xf; i <= xi; i++) {
                if (yi <= yf) {
                    for (int j = yi; j <= yf; j++) {
                        if (initialMovement.intersects(new Rectangle2D.Double(i, j, 1, 1))) {
                            movement.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                        } else if (finalMovement.intersects(new Rectangle2D.Double(i, j, 1, 1))) {
                            movement.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                        }
                    }
                } else {
                    for (int j = yf; j <= yi; j++) {
                        if (initialMovement.intersects(new Rectangle2D.Double(i, j, 1, 1))) {
                            movement.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                        } else if (finalMovement.intersects(new Rectangle2D.Double(i, j, 1, 1))) {
                            movement.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                        }
                    }
                }
            }
        }

        Area blockArea = new Area();

        if (xi <= xf) {
            for (int i = xi; i <= xf; i++) {
                if (yi <= yf) {
                    for (int j = yi; j <= yf; j++) {
                        if (checkPlayerCollision(i, j))
                            blockArea.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                    }
                } else {
                    for (int j = yf; j <= yi; j++) {
                        if (checkPlayerCollision(i, j))
                            blockArea.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                    }
                }
            }
        } else {
            for (int i = xf; i <= xi; i++) {
                if (yi <= yf) {
                    for (int j = yi; j <= yf; j++) {
                        if (checkPlayerCollision(i, j))
                            blockArea.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                    }
                } else {
                    for (int j = yf; j <= yi; j++) {
                        if (checkPlayerCollision(i, j))
                            blockArea.add(new Area(new Rectangle.Double(i, j, 1, 1)));
                    }
                }
            }
        }

        movement.intersect(blockArea);

        return movement.isEmpty();
    }
}
