package com.jakespringer.reagne.test;

import com.jakespringer.reagne.Reagne;
import com.jakespringer.reagne.game.World;
import com.jakespringer.reagne.gfx.Window;
import java.io.File;

public class PositionTest {

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../Reagne/natives").getAbsolutePath());

        final World world = new World();

        Window.initialize(1200, 800, "Test");

//        Input.whileKeyPressed(Keyboard.KEY_0).forEach(x -> System.out.println("Time elapsed: " + x));
        world.add(new Player());

        Reagne.run(world);
    }
}
