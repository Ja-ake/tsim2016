package com.jakespringer.trump.network;

import java.io.File;
import java.io.IOException;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.World;
import com.jakespringer.reagan.gfx.FontContainer;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.math.Vec2;
import com.jakespringer.trump.game.Walls;
import com.jakespringer.trump.lobster.LobsterClient;
import com.jakespringer.trump.platfinder.NodeGraph;
import com.jakespringer.trump.ui.BuildMenu;
import com.jakespringer.trump.ui.ViewController;

public class NetworkedMain {
	
	public static LobsterClient client;
	public static MainCommandHandler networkHandler;
	public static boolean networked = false;
	
    public static void main(String[] args) throws IOException {
        System.setProperty("org.lwjgl.librarypath", new File("../Reagne/natives").getAbsolutePath());

        Window.initialize(1200, 800, "Test");
        final World world = new World();
        FontContainer.create();

        client = new LobsterClient("localhost", 55555);   
        client.commandDispatch = networkHandler = new MainCommandHandler(client);

        Thread d = new Thread(client::run);
        d.setDaemon(true);
        d.start();
        
        networked = true;

        world.addAndGet(new Walls()).loadImage();
        world.addAndGet(new BuildMenu(true));
        world.addAndGet(new ViewController()).position.set(new Vec2(1000, 1500));
        
        NodeGraph.red = new NodeGraph(Walls.walls.grid, true);
        NodeGraph.blue = new NodeGraph(Walls.walls.grid, false);


        Reagan.run(world);
    }
}
