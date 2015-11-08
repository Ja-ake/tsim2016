package com.jakespringer.trump.game;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.World;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.math.Vec2;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../Reagne/natives").getAbsolutePath());

        Window.initialize(1200, 800, "Test");

        final World world = new World();

        world.addAndGet(new Walls()).loadImage();
        Player p = new Player();
        world.add(p);
        p.position.set(new Vec2(1000, 1500));

        Reagan.run(world);
    }
}
