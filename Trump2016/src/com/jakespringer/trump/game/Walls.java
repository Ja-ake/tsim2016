package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.Signal;
import com.jakespringer.reagan.game.AbstractEntity;
import static com.jakespringer.reagan.gfx.Graphics2D.drawSpriteFast;
import com.jakespringer.reagan.gfx.SpriteContainer;
import com.jakespringer.reagan.math.Color4;
import static com.jakespringer.reagan.math.Color4.WHITE;
import com.jakespringer.reagan.math.Vec2;
import static com.jakespringer.trump.game.Tile.WallType.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.imageio.ImageIO;
import static org.lwjgl.opengl.GL11.*;

public class Walls extends AbstractEntity {

    public static Walls walls;

    public int width;
    public int height;
    public Tile[][] grid;
    public double wallSize = 36;
    public double[] zoneControl;

    private static final String path = "levels/";
    private static final String type = ".png";

    @Override
    public void create() {
        walls = this;

        onUpdate(dt -> {
            glEnable(GL_TEXTURE_2D);
            WHITE.glColor();

            SpriteContainer.all().forEach(tex -> {
                tex.bind();
                glBegin(GL_QUADS);

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        Tile t = grid[i][j];
                        if (t.image != tex) {
                            continue;
                        }
                        drawSpriteFast(tex, t.LL(), t.LR(), t.UR(), t.UL());
                    }
                }
                glEnd();
            });
            glDisable(GL_TEXTURE_2D);
            glBegin(GL_QUADS);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Tile t = grid[i][j];
                    double control = zoneControl[t.zone - 1];
                    Color4 c = control > 0 ? Color4.RED : Color4.BLUE;
                    if (Math.abs(control) > .5) {
                        c = c.withA(Math.abs(control) / 5 + .1);
                    } else {
                        c = c.withA(Math.abs(control) / 5);
                    }
                    c.glColor();
                    t.LL().glVertex();
                    t.LR().glVertex();
                    t.UR().glVertex();
                    t.UL().glVertex();
                }
            }
            glEnd();
        });
    }

    public void loadImage() {
        String fileName = "level";
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
        grid = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = loadTile(x, y, image.getRGB(x, height - y - 1));
            }
        }

        BufferedImage zoneImage = null;
        try {
            zoneImage = ImageIO.read(new File(path + fileName + "_zones" + type));
        } catch (IOException ex) {
            throw new RuntimeException("Level " + fileName + "_zones" + " doesn't exist");
        }
        int maxZone = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y].zone == 0) {
                    if (zoneImage.getRGB(x, height - y - 1) + 0x1000000 != 0) {
                        grid[x][y].zone = ++maxZone;
                        Queue<Tile> toZone = new LinkedList();
                        toZone.add(grid[x][y]);
                        while (!toZone.isEmpty()) {
                            Tile t = toZone.poll();
                            if (zoneImage.getRGB(t.x, height - t.y - 1) + 0x1000000 != 0) {
                                for (int i = Math.max(t.x - 1, 0); i <= Math.min(t.x + 1, width - 1); i++) {
                                    for (int j = Math.max(t.y - 1, 0); j <= Math.min(t.y + 1, height - 1); j++) {
                                        if (grid[i][j].zone == 0) {
                                            grid[i][j].zone = maxZone;
                                            toZone.add(grid[i][j]);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        zoneControl = new double[maxZone];
    }

    public boolean[][] loadText() throws IOException {
        //Load image
        List<String> text = Files.readAllLines(Paths.get("levels/text_level.txt"));
        //Init tile grid
        width = text.get(0).length();
        height = text.size();
        grid = new Tile[width][height];
        boolean[][] r = new boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                r[x][y] = text.get(height - y - 1).charAt(x) == 'X';
                grid[x][y] = r[x][y] ? new Tile(x, y, WALL, "stone") : new Tile(x, y, AIR, null);
            }
        }
        return r;
    }

    private Tile loadTile(int x, int y, int color) {
        color += 0x1000000; //Because it works
        switch (color) {
            case 0x0: //Black
                return new Tile(x, y, WALL, "stone"); //Normal wall
            case 0xFF0000: //Red
                return new Tile(x, y, RED_DOOR, "red_door"); //Red door
            case 0xFF: //Blue
                return new Tile(x, y, BLUE_DOOR, "blue_door"); //Red door
            case 0xFF00: //Green
                return new Tile(x, y, GRAY_DOOR, "gray_door"); //Red door
            case 0x808080: //Gray
                return new Tile(x, y, BEDROCK, "bedrock"); //Unbreakable wall
            case 0xC0C0C0: //Light gray
                return new Tile(x, y, BACKGROUND, "stoneBackground"); //Background wall
            case 0xFFFF00: //Yellow
                Statue s = Reagan.world().addAndGet(new Statue());
                s.position = new Vec2(x, y).multiply(wallSize);
                return new Tile(x, y, BACKGROUND, "stoneBackground"); //Background wall
            default: //Anything else, inc. white
                return new Tile(x, y, AIR, null); //Nothing
        }
    }

    public static Tile tileAt(Vec2 pos) {
        return walls.grid[(int) (pos.x / walls.wallSize)][(int) (pos.y / walls.wallSize)];
    }

    public static List<Tile> tilesAt(Vec2 pos, Vec2 size) {
        Vec2 v = pos;//.add(walls.offset);
        Vec2 LL = v.subtract(size).divide(walls.wallSize);
        Vec2 UR = v.add(size).divide(walls.wallSize);
        List<Tile> r = new LinkedList();
        for (int x = Math.max((int) LL.x, 0); x < Math.min(UR.x, walls.width); x++) {
            for (int y = Math.max((int) LL.y, 0); y < Math.min(UR.y, walls.height); y++) {
                r.add(walls.grid[x][y]);
            }
        }
        return r;
    }

    //Collision code
    public static boolean collideAABB(Vec2 pos1, Vec2 size1, Vec2 pos2, Vec2 size2) {
        return pos1.subtract(size1).quadrant(pos2.add(size2)) == 1 && pos2.subtract(size2).quadrant(pos1.add(size1)) == 1;
    }

    public static boolean collisionAt(Vec2 pos, Vec2 size, boolean team) {
        return tilesAt(pos, size).stream().anyMatch(t -> t.isSolid(team));
    }

    public static Signal<Integer> makeCollisionSystem(Signal<Vec2> position, Signal<Vec2> velocity, Vec2 size, boolean team) {
        return new Signal<>(0).sendOn(Reagan.continuous, (dt, n) -> {
            n = 0;
            Vec2 oldPos = position.get();
            position.edit(p -> p.add(velocity.get().multiply(dt)));
            if (Walls.collisionAt(position.get(), size, team)) {
                Vec2 diff = position.get().subtract(oldPos);
                position.set(oldPos);
                if (Walls.collisionAt(position.get(), size, team)) {
                    position.set(oldPos.add(diff));
                    return -1;
                }
                for (int i = 0; i < 10; i++) {
                    if (!Walls.collisionAt(position.get().add(new Vec2(diff.x * .1, 0)), size, team)) {
                        position.edit(p -> p.add(new Vec2(diff.x * .1, 0)));
                    } else {
                        velocity.edit(v -> v.withX(0));
                        n += 1;
                        break;
                    }
                }
                for (int i = 0; i < 10; i++) {
                    if (!Walls.collisionAt(position.get().add(new Vec2(0, diff.y * .1)), size, team)) {
                        position.edit(p -> p.add(new Vec2(0, diff.y * .1)));
                    } else {
                        velocity.edit(v -> v.withY(0));
                        n += 2;
                        break;
                    }
                }
            }
            return n;
        });
    }
}
