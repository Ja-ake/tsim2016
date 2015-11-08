package com.jakespringer.trump.ui;

import static com.jakespringer.reagan.math.Color4.WHITE;
import static com.jakespringer.trump.game.Tile.WallType.AIR;
import static com.jakespringer.trump.game.Tile.WallType.BLUE_BRIDGE;
import static com.jakespringer.trump.game.Tile.WallType.BLUE_DOOR;
import static com.jakespringer.trump.game.Tile.WallType.RED_BRIDGE;
import static com.jakespringer.trump.game.Tile.WallType.RED_DOOR;
import static com.jakespringer.trump.game.Tile.WallType.SPIKE;
import static com.jakespringer.trump.game.Tile.WallType.WALL;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;

import java.util.ArrayList;
import java.util.List;

import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Camera;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.gfx.SpriteContainer;
import com.jakespringer.reagan.gfx.Texture;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.game.Robot;
import com.jakespringer.trump.game.Tile;
import com.jakespringer.trump.game.Tile.WallType;
import com.jakespringer.trump.game.Walls;
import com.jakespringer.trump.network.NetworkedMain;
import com.jakespringer.trump.network.PathfindingAlteredEvent;

public class BuildMenu extends AbstractEntity {

    private boolean team;
    private Button selected;
    private List<Button> buttonList;
    private boolean canBuild;

    public BuildMenu(boolean team) {
        this.team = team;
        buttonList = new ArrayList<>();
    }

    @Override
    public void create() {
        buttonList.add(new Button(WALL, "wood"));
        buttonList.add(new Button(AIR, null));
        buttonList.add(new Button(team ? RED_DOOR : BLUE_DOOR, team ? "red_door" : "blue_door"));
        buttonList.add(new Button(team ? RED_BRIDGE : BLUE_BRIDGE, team ? "red_bridge" : "blue_bridge"));
        buttonList.add(new Button(SPIKE, "spikes"));

        onUpdate(dt -> {
            Camera.setProjection2D(new Vec2(), new Vec2(1200, 800));
            Graphics2D.fillRect(new Vec2(), new Vec2(1200, 96), new Color4(.8, .8, .8));
            for (int i = 0; i < buttonList.size(); i++) {
                Button b = buttonList.get(i);
                b.LL = new Vec2(16 + i * 80, 16);
                b.draw();
            }
            Camera.setProjection2D(Window.LL(), Window.UR());

            if (!Input.getMouseScreen().containedBy(new Vec2(), new Vec2(1200, 96))) {
                if (selected != null) {
                    Tile t = Walls.tileAt(Input.getMouse());
                    double zoneControl = Walls.walls.zoneControl[t.zone - 1];
                    canBuild = (selected.wt != AIR)
                            ? (team ? zoneControl > 0.5 : zoneControl < -0.5)
                            && (Walls.walls.grid[t.x + 1][t.y].type != AIR
                            || Walls.walls.grid[t.x - 1][t.y].type != AIR
                            || Walls.walls.grid[t.x][t.y + 1].type != AIR
                            || Walls.walls.grid[t.x][t.y - 1].type != AIR)
                            && !Robot.blueList.stream().anyMatch(r -> Walls.collideAABB(r.position.get(), Robot.size, t.center(), new Vec2(18, 18)))
                            && !Robot.redList.stream().anyMatch(r -> Walls.collideAABB(r.position.get(), Robot.size, t.center(), new Vec2(18, 18)))
                            : (team ? zoneControl > 0.5 : zoneControl < -0.5) && t.type != AIR;

                    Graphics2D.fillRect(t.LL(), new Vec2(36, 36), canBuild ? Color4.GREEN : Color4.RED);
                }
            }
        });

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
                        Tile t = Walls.tileAt(Input.getMouse());
                        t.change(selected.wt, selected.image);
                    }
                } else {
                    if (team) {
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
    }

    private class Button {

        public Vec2 LL;
        public final Vec2 size = new Vec2(64, 64);
        public final WallType wt;
        public final String image;

        public Button(WallType wt, String image) {
            this.wt = wt;
            this.image = image;
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

            Graphics2D.drawRect(LL, size, color);
        }

        private boolean mouseOver() {
            return Input.getMouseScreen().containedBy(LL, LL.add(size));
        }
    }
}
