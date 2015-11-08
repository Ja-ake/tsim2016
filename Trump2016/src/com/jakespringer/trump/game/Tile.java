package com.jakespringer.trump.game;

import com.jakespringer.reagan.gfx.SpriteContainer;
import com.jakespringer.reagan.gfx.Texture;
import com.jakespringer.reagan.math.Vec2;
import static com.jakespringer.trump.game.Tile.WallType.*;
import com.jakespringer.trump.platfinder.NodeGraph;

public class Tile {

    public enum WallType {

        AIR, WALL, BACKGROUND, RED_DOOR, BLUE_DOOR, GRAY_DOOR, BEDROCK;
    }

    private static final Vec2 offset = new Vec2(size() * Walls.walls.width / 2, size() * Walls.walls.height / 2);

    public final int x, y;
    public WallType type;
    public Texture image;
    public int zone;

    public Tile(int x, int y, WallType type, String image) {
        this.x = x;
        this.y = y;
        this.type = type;
        if (image != null) {
            this.image = SpriteContainer.loadSprite(image);
        }
    }

    public boolean isSolid(boolean teamRed) {
        if (teamRed) {
            return type == WALL || type == BEDROCK || type == BLUE_DOOR;
        } else {
            return type == WALL || type == BEDROCK || type == RED_DOOR;
        }
    }

    public static double size() {
        return Walls.walls.wallSize;
    }

    public Vec2 center() {
        return new Vec2((x + .5) * size(), (y + .5) * size());
    }

    public void change(WallType wt, String image) {
        boolean isSolidRed = isSolid(true);
        boolean isSolidBlue = isSolid(false);
        type = wt;
        this.image = image == null ? null : SpriteContainer.loadSprite(image);
        if (isSolid(true) != isSolidRed) {
            NodeGraph.red.update();
        }
        if (isSolid(false) != isSolidBlue) {
            NodeGraph.blue.update();
        }
    }

    public Vec2 LL() {
        return new Vec2(x * size(), y * size());
    }

    public Vec2 LR() {
        return new Vec2(x * size() + size(), y * size());
    }

    public Vec2 UL() {
        return new Vec2(x * size(), y * size() + size());
    }

    public Vec2 UR() {
        return new Vec2(x * size() + size(), y * size() + size());
    }
}
