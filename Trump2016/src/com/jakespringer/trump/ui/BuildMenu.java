package com.jakespringer.trump.ui;

import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.*;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import static com.jakespringer.reagan.math.Color4.WHITE;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.game.Robot;
import com.jakespringer.trump.game.Tile;
import com.jakespringer.trump.game.Tile.WallType;
import static com.jakespringer.trump.game.Tile.WallType.*;
import com.jakespringer.trump.game.Walls;
import com.jakespringer.trump.network.NetworkedMain;
import com.jakespringer.trump.network.PathfindingAlteredEvent;
import com.jakespringer.trump.platfinder.NodeGraph;
import com.jakespringer.trump.platfinder.NodeGraph.Connection;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

public class BuildMenu extends AbstractEntity {

    public static BuildMenu menu;

    public final Signal<Boolean> team;
    public Button selected;
    private List<Button> buttonList;
    private boolean canBuild;
    private double resources;

    public BuildMenu(boolean team) {
        this.team = new Signal(team);
        resources = 200;
        menu = this;
    }

    @Override
    public void create() {
        createButtons();

        FontContainer.add("Resources", "Calibri", Font.PLAIN, 30);

        onUpdate(dt -> {
            (team.get() ? Robot.redList : Robot.blueList).forEach(n -> {
                List<Connection> conn = (team.get() ? NodeGraph.red : NodeGraph.blue).findNearestPath(n.position.get(), Input.getMouse(), Robot.size);
                if (conn != null && !conn.isEmpty()) {
                    if (conn.get(conn.size() - 1).to.p.toVec2().subtract(Input.getMouse()).length() < 200) {
                        conn.forEach(c -> Graphics2D.drawLine(c.from.p.toVec2(), c.to.p.toVec2(), Color4.GREEN, 2));
                    }
                }
                Vec2 rgoal = team.get() ? Robot.redGoal : Robot.blueGoal;
                if (rgoal != null) {
                    List<Connection> list2 = (team.get() ? NodeGraph.red : NodeGraph.blue).findNearestPath(n.position.get(), rgoal, Robot.size);
                    if (list2 != null && !list2.isEmpty()) {
                        Vec2 goal = list2.get(list2.size() - 1).to.p.toVec2();//team.get() ? Robot.redGoal : Robot.blueGoal;
                        if (goal != null) {
                            if (rgoal.subtract(goal).length() > 100) {
                                Graphics2D.drawLine(goal.add(new Vec2(16, 16)), goal.add(new Vec2(-16, -16)), Color4.RED, 4);
                                Graphics2D.drawLine(goal.add(new Vec2(-16, 16)), goal.add(new Vec2(16, -16)), Color4.RED, 4);
                            } else {
                                Graphics2D.fillEllipse(goal, new Vec2(10, 10), Color4.RED, 20);
                            }
                        }
                    }
                }
            });

            Camera.setProjection2D(new Vec2(), new Vec2(1200, 800));
            Graphics2D.fillRect(new Vec2(), new Vec2(1200, 96), new Color4(.8, .8, .8));
            for (int i = 0; i < buttonList.size(); i++) {
                Button b = buttonList.get(i);
                b.LL = new Vec2(16 + i * 80, 16);
                b.draw();
            }
            Graphics2D.drawText("Resources: " + (int) resources, "Resources", new Vec2(950, 80), Color.black);
            Camera.setProjection2D(Window.LL(), Window.UR());

            if (!Input.getMouseScreen().containedBy(new Vec2(), new Vec2(1200, 96))) {
                if (selected != null) {
                    if (Input.getMouse().containedBy(new Vec2(1, 1).multiply(Walls.walls.wallSize), new Vec2(Walls.walls.width - 1, Walls.walls.height - 1).multiply(Walls.walls.wallSize))) {
                        Tile t = Walls.tileAt(Input.getMouse());
                        canBuild = selected.cost < resources
                                && (selected.wt != AIR)
                                ? (team.get() ? t.control > 0.5 : t.control < -0.5)
                                && (Walls.walls.grid[t.x + 1][t.y].type != AIR
                                || Walls.walls.grid[t.x - 1][t.y].type != AIR
                                || Walls.walls.grid[t.x][t.y + 1].type != AIR
                                || Walls.walls.grid[t.x][t.y - 1].type != AIR)
                                && !Robot.blueList.stream().anyMatch(r -> Walls.collideAABB(r.position.get(), Robot.size, t.center(), new Vec2(18, 18)))
                                && !Robot.redList.stream().anyMatch(r -> Walls.collideAABB(r.position.get(), Robot.size, t.center(), new Vec2(18, 18)))
                                : (team.get() ? t.control > 0.5 : t.control < -0.5) && t.type != AIR;

                        Graphics2D.fillRect(t.LL(), new Vec2(36, 36), canBuild ? Color4.GREEN : Color4.RED);
                    }
                }
            }
        });

        onUpdate(dt -> resources = Math.min(1000, resources + dt * 20));

        add(Input.whenMouse(0, true).forEach(() -> {
            if (Input.getMouseScreen().containedBy(new Vec2(), new Vec2(1200, 96))) {
                selected = null;
                buttonList.forEach(b -> {
                    if (b.mouseOver()) {
                        selected = b;
                    }
                });
            } else {
                if (selected != null) {
                    if (canBuild) {
                        resources -= selected.cost;
                        Tile t = Walls.tileAt(Input.getMouse());
                        t.change(selected.wt, selected.image);
                    }
                } else {
                    if (team.get()) {
                        Robot.redGoal = Input.getMouse();
                        if (NetworkedMain.networked) {
                            NetworkedMain.networkHandler.submit(
                                    new PathfindingAlteredEvent(Robot.redGoal.x, Robot.redGoal.y, true));
                        }
                    } else {
                        Robot.blueGoal = Input.getMouse();
                        if (NetworkedMain.networked) {
                            NetworkedMain.networkHandler.submit(
                                    new PathfindingAlteredEvent(Robot.blueGoal.x, Robot.blueGoal.y, false));
                        }
                    }
                }
            }
        }));

        add(Input.whenMouse(1, true).forEach(() -> {
            selected = null;
            //team.edit(t -> !t);
            //createButtons();
        }));
    }

    private void createButtons() {
        buttonList = new ArrayList<>();
        buttonList.add(new Button(WALL, "wood", 10));
        buttonList.add(new Button(AIR, null, 5));
        buttonList.add(new Button(BACKGROUND, "stoneBackground", 5));
        buttonList.add(new Button(team.get() ? RED_DOOR : BLUE_DOOR, team.get() ? "red_door" : "blue_door", 40));
        buttonList.add(new Button(team.get() ? RED_BRIDGE : BLUE_BRIDGE, team.get() ? "red_bridge" : "blue_bridge", 50));
        buttonList.add(new Button(SPIKE, "spikes", 50));
    }

    private class Button {

        public Vec2 LL;
        public final Vec2 size = new Vec2(64, 64);
        public final WallType wt;
        public final String image;
        public final int cost;

        public Button(WallType wt, String image, int cost) {
            this.wt = wt;
            this.image = image;
            this.cost = cost;
        }

        private void draw() {
            Color4 color = Color4.BLACK;
            if (mouseOver()) {
                color = Color4.BLUE;
            }
            if (this == selected) {
                color = Color4.GREEN;
            }
            Graphics2D.fillRect(LL, size, color);

            if (image != null) {
                glEnable(GL_TEXTURE_2D);
                WHITE.glColor();
                Texture tex = SpriteContainer.loadSprite(image);
                tex.bind();
                glBegin(GL_QUADS);
                Graphics2D.drawSpriteFast(tex, LL, LL.add(size.withY(0)), LL.add(size), LL.add(size.withX(0)));
                glEnd();
            } else {
                Graphics2D.fillRect(LL, size, WHITE);
            }

            if (resources < cost) {
                Graphics2D.fillRect(LL, size, Color4.RED.withA(.5));
            }

            Graphics2D.drawRect(LL, size, color);
            Graphics2D.drawText("" + cost, "Default", LL.add(size.multiply(new Vec2(.5, 0))), Color.black);
        }

        private boolean mouseOver() {
            return Input.getMouseScreen().containedBy(LL, LL.add(size));
        }
    }
}
