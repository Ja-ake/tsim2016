package com.jakespringer.trump.test;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import com.jakespringer.reagan.gfx.Graphics2D;
import com.jakespringer.reagan.math.Color4;
import com.jakespringer.reagan.math.Vec2;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Walls extends AbstractEntity {

    public static Walls walls;

    public int width;
    public int height;
    public boolean[][] grid;
    public double wallSize = 20;
    public Vec2 offset;

    private static final String path = "levels/";
    private static final String type = ".png";

    @Override
    public void create() {
        walls = this;

        String fileName = "test";
        //Load image
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path + fileName + type));
        } catch (IOException ex) {
            throw new RuntimeException("Level " + fileName + " doesn't exist");
        }
        //Init tile grid
        width = image.getWidth();
        height = image.getHeight();
        grid = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = (image.getRGB(x, height - y - 1) == 0xFF000000);
            }
        }

        offset = new Vec2(wallSize * width / 2, wallSize * height / 2);
        onUpdate(dt -> {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (grid[x][y]) {
                        Graphics2D.fillRect(new Vec2(x * wallSize, y * wallSize).subtract(offset), new Vec2(wallSize, wallSize), Color4.BLACK);
                    }
                }
            }
        });
    }

    public static boolean collisionAt(Vec2 pos, Vec2 size) {
        Vec2 v = pos.add(walls.offset);
        Vec2 LL = v.subtract(size).divide(walls.wallSize);
        Vec2 UR = v.add(size).divide(walls.wallSize);
        for (int x = Math.max((int) LL.x, 0); x < Math.min(UR.x, walls.width); x++) {
            for (int y = Math.max((int) LL.y, 0); y < Math.min(UR.y, walls.height); y++) {
                if (walls.grid[x][y]) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Signal<Double> makeCollisionSystem(Signal<Vec2> position, Signal<Vec2> velocity, Vec2 size) {
        return Reagan.continuous.forEach(dt -> {
            Vec2 oldPos = position.get();
            position.edit(p -> p.add(velocity.get().multiply(dt)));
            if (Walls.collisionAt(position.get(), size)) {
                Vec2 diff = position.get().subtract(oldPos);
                position.set(oldPos);
                if (Walls.collisionAt(position.get(), size)) {
                    position.set(oldPos.add(diff));
                    return;
                }
                for (int i = 0; i < 10; i++) {
                    if (!Walls.collisionAt(position.get().add(new Vec2(diff.x * .1, 0)), size)) {
                        position.edit(p -> p.add(new Vec2(diff.x * .1, 0)));
                    } else {
                        velocity.edit(v -> v.withX(0));
                        break;
                    }
                }
                for (int i = 0; i < 10; i++) {
                    if (!Walls.collisionAt(position.get().add(new Vec2(0, diff.y * .1)), new Vec2(16, 16))) {
                        position.edit(p -> p.add(new Vec2(0, diff.y * .1)));
                    } else {
                        velocity.edit(v -> v.withY(0));
                        break;
                    }
                }
            }
        });
    }
    
    public static Vec2 gridToActual(int x, int y) {
        return new Vec2(x-walls.width/2, y-walls.width/2).multiply(walls.wallSize);
    }
}
