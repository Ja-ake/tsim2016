package com.jakespringer.trump.ui;

import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.*;
import com.jakespringer.reagan.input.Input;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.game.Robot;
import com.jakespringer.trump.game.Tile;
import com.jakespringer.trump.game.Tile.WallType;
import static com.jakespringer.trump.game.Tile.WallType.*;
import com.jakespringer.trump.game.Walls;
import com.jakespringer.trump.platfinder.NodeGraph;
import org.newdawn.slick.Color;

public class BuildMenu extends AbstractEntity {

    private final Vec2 buildPosition;
    private final Vec2 buildSize;
    private final Vec2 destroyPosition;
    private final Vec2 destroySize;
    private Color4 buildColor;
    private Color4 destroyColor;
    private Texture texture;
    private int numWood = 128;
    private int numDestroy = 128;
    private boolean canBuild = false;
    private boolean canDestroy = false;
    private boolean buildSelection = false;
    private boolean destroySelection = false;
    private boolean redSide;

//	private Signal<Vec2> displayPosition;
//	private Signal<Vec2> displaySize;
    public BuildMenu(boolean red) {
        buildPosition = new Vec2(33, 32);
        buildSize = new Vec2(4, 4);
        destroyPosition = new Vec2(33 + 64, 32);
        destroySize = new Vec2(4, 4);
        buildColor = Color4.BLUE;
        destroyColor = Color4.BLUE;
        redSide = red;
    }

    @Override
    public void create() {
        texture = SpriteContainer.loadSprite("wood");

        onUpdate(dt -> {
            Camera.setProjection2D(new Vec2(), new Vec2(1200, 800));
            Graphics2D.drawSprite(texture, buildPosition, buildSize, 0, Color4.WHITE);
            Graphics2D.drawRect(buildPosition.subtract(buildSize.multiply(8)), buildSize.multiply(16), buildColor);
            Graphics2D.drawText("" + numWood, "Default", buildPosition.subtract(buildSize.withX(0).multiply(4)), Color.white);
            Graphics2D.fillRect(destroyPosition.subtract(destroySize.multiply(8)), destroySize.multiply(16), Color4.WHITE);
            Graphics2D.drawRect(destroyPosition.subtract(destroySize.multiply(8)), destroySize.multiply(16), destroyColor);
            Graphics2D.drawText("" + numDestroy, "Default", destroyPosition.subtract(destroySize.withX(0).multiply(4)), Color.black);
            Camera.setProjection2D(Window.LL(), Window.UR());

            Tile t = Walls.tileAt(Input.getMouse());
            double zoneControl = Walls.walls.zoneControl[t.zone - 1];

            if (buildSelection) {
                canBuild = (redSide ? zoneControl > 0.5 : zoneControl < -0.5)
                        && (Walls.walls.grid[t.x + 1][t.y].type != AIR
                        || Walls.walls.grid[t.x - 1][t.y].type != AIR
                        || Walls.walls.grid[t.x][t.y + 1].type != AIR
                        || Walls.walls.grid[t.x][t.y - 1].type != AIR)
                        && !Robot.blueList.stream().anyMatch(r -> Walls.collideAABB(r.position.get(), Robot.size, t.center(), new Vec2(18, 18)))
                        && !Robot.redList.stream().anyMatch(r -> Walls.collideAABB(r.position.get(), Robot.size, t.center(), new Vec2(18, 18)));
                Graphics2D.fillRect(t.LL(), new Vec2(36, 36), canBuild ? Color4.GREEN : Color4.RED);
            } else if (destroySelection) {
                canDestroy = (redSide ? zoneControl > 0.5 : zoneControl < -0.5) && t.type != AIR;
                Graphics2D.fillRect(t.LL(), new Vec2(36, 36), canDestroy ? Color4.GREEN : Color4.RED);
            }
        });

        add(Input.whenMouse(0, true).forEach(() -> {
            Vec2 pos = Input.getMouseScreen();

            Tile t = Walls.tileAt(Input.getMouse());

            Vec2 bs8 = buildSize.multiply(8);
            Vec2 ds8 = destroySize.multiply(8);

            //System.out.println("Mouse: " + pos);
            //System.out.println(buildPosition.subtract(bs8) + " " + buildPosition.add(bs8));
            if (pos.containedBy(buildPosition.subtract(bs8), buildPosition.add(bs8))) {
                //System.out.println("ok");
                buildColor = buildColor.equals(Color4.BLUE) ? Color4.RED : Color4.BLUE;
                buildSelection = !buildSelection;
                destroySelection = false;
                destroyColor = Color4.BLUE;
            } else if (pos.containedBy(destroyPosition.subtract(ds8), destroyPosition.add(ds8))) {
                destroyColor = destroyColor.equals(Color4.BLUE) ? Color4.RED : Color4.BLUE;
                destroySelection = !destroySelection;
                buildSelection = false;
                buildColor = Color4.BLUE;
            } else if (canBuild && buildSelection && numWood > 0) {
                updateTile(t, WALL, "wood");
                --numWood;
            } else if (canDestroy && destroySelection && numDestroy > 0) {
                updateTile(t, AIR, null);
                --numDestroy;
            }
        }));
    }

    public void updateTile(Tile t, WallType wt, String image) {
        t.type = wt;
        t.image = image == null ? null : SpriteContainer.loadSprite(image);
        NodeGraph.red.update();
        NodeGraph.blue.update();
    }
}
