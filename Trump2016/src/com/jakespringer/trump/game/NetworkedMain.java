package com.jakespringer.trump.game;

import java.io.File;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.World;
import com.jakespringer.reagan.gfx.FontContainer;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.ui.BuildMenu;
import com.jakespringer.trump.ui.ViewController;

public class NetworkedMain {

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../Reagne/natives").getAbsolutePath());

        Window.initialize(1200, 800, "Test");

        final World world = new World();
        
        FontContainer.create();

        world.addAndGet(new Walls()).loadImage();
        world.addAndGet(new BuildMenu(true));
        world.addAndGet(new ViewController()).position.set(new Vec2(1000, 1500));

//        Player p = new Player();
//        world.add(p);
//        p.position.set(new Vec2(1000, 1500));

        Reagan.run(world);
    }
}
