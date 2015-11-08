package com.jakespringer.trump.test;

import com.jakespringer.trump.game.Walls;
import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.World;
import com.jakespringer.reagan.gfx.Window;
import java.io.File;

public class PlatfinderTest {

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../Reagne/natives").getAbsolutePath());

        final World world = new World();
        Window.initialize(1200, 800, "Test");

//        Input.whileKeyPressed(Keyboard.KEY_0).forEach(x -> System.out.println("Time elapsed: " + x));
        world.add(new Walls());
//        world.add(new Walker(Walls.walls.grid));

        Reagan.run(world);
    }
}
