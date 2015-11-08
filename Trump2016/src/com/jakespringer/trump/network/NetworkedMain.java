package com.jakespringer.trump.network;

import java.io.File;
import java.io.IOException;

import com.jakespringer.reagan.Reagan;
import com.jakespringer.reagan.game.World;
import com.jakespringer.reagan.gfx.FontContainer;
import com.jakespringer.reagan.gfx.Window;
import com.jakespringer.reagan.util.Sounds;
import com.jakespringer.trump.game.Menu;
import com.jakespringer.trump.lobster.LobsterClient;
import com.jakespringer.trump.ui.BuildMenu;

public class NetworkedMain {
	
	public static LobsterClient client;
	public static MainCommandHandler networkHandler;
	public static boolean networked = false;
	public static BuildMenu buildMenu;
	public static boolean bm = true;
	
	public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File("../Reagne/natives").getAbsolutePath());

        Window.initialize(1200, 800, "Test");

        FontContainer.create();
        
        final World world = new World();
        world.add(new Menu());
        Reagan.run(world);
	}
	
    public static void run() {
    	try {
	        buildMenu = BuildMenu.menu;
    		bm = buildMenu.team.get();
	        setupNetwork();
    	} catch (IOException e) {
    		e.printStackTrace();
        	Sounds.stopAll();
        	System.exit(0);
    	}
    }
    
   public static void setupNetwork() throws IOException {
       client = new LobsterClient("10.144.221.50", 55555);   
       client.commandDispatch = networkHandler = new MainCommandHandler(client);

       client.queued.offer("JOINED");
       
       Thread d = new Thread(client::run);
       d.setDaemon(true);
       d.start();
       
       networked = true;
   }
}
