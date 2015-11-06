package com.jakespringer.trump.test;

import java.io.File;
import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.World;
import com.jakespringer.reagan.gfx.Window;

public class PositionTest {

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../Reagan/natives").getAbsolutePath());

        final World world = new World();

        Window.initialize(1200, 800, "Test");

//        Input.whileKeyPressed(Keyboard.KEY_0).forEach(x -> System.out.println("Time elapsed: " + x));
        world.add(new Player());

        Reagan.run(world);
    }
}
