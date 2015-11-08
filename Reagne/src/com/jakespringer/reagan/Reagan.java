package com.jakespringer.reagan;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jakespringer.reagan.game.World;
import com.jakespringer.reagan.util.Command;
import com.jakespringer.reagan.util.Event;

public class Reagan {

    public static double timeMult = 1;

    private static Queue<Command> commandQueue = new LinkedList<>();
    private static Queue<Command> mainThread = new ConcurrentLinkedQueue<>();
    private static boolean running = true;
    private static World theWorld;

    ///
    /// Public member variables
    ///
    /**
     * A stream that fires every tick. Will stream the delta time between each
     * tick.
     */
    public static final Signal<Double> continuous = new Signal<>(0.0);

    /**
     * A stream that fires once at the start of when {@link Reagan#run()} is
     * called.
     */
    public static final Signal<Object> initialize = new Signal<>(new Object());

    /**
     * Queues a command for executing during the next update loop.
     *
     * @param command the command to execute
     */
    public static void queueCommand(Command command) {
        if (command == null) {
            throw new NullPointerException();
        }
//        commandQueue.add(command);
        command.act();
    }
    
    public static void onMainThread(Command command) {
    	if (command == null) {
    		throw new NullPointerException();
    	}
    	
    	mainThread.offer(command);
    }
    
    public static Event periodic(double seconds) {
    	class MyDouble { public double me; }
    	final MyDouble accumulatedTime = new MyDouble();
    	accumulatedTime.me = 0.0;
    	return (Event) continuous.forEach(dt -> accumulatedTime.me+=dt).filter(x -> (accumulatedTime.me > seconds)).forEach(x -> accumulatedTime.me = 0).asEvent();
    }

    /**
     * Run the game loop.
     */
    public static void run() {
        // initialize the game
        initialize.set(initialize.get());
        dispatchCommands();

        // run the game loop
        long currentTime = System.nanoTime();
        long previousTime = currentTime;
        while (running) {
            currentTime = System.nanoTime();
            double deltaTime = ((double) currentTime - (double) previousTime) * 0.000000001 * timeMult;
            continuous.set(deltaTime);
            dispatchCommands();
            previousTime = currentTime;
        }
    }

    /**
     * Run the game loop and supply a world.
     */
    public static void run(World wld) {
        theWorld = wld;
        run();
    }

    /**
     * Requests the game to stop.
     */
    public static void stop() {
        running = false;
    }

    /**
     * Gets the instance of World associated with the engine.
     */
    public static World world() {
        return theWorld;
    }

    public static void setWorld(World w) {
        theWorld = w;
    }

    /**
     * Returns the default resource folder.
     *
     * @return the resource folder
     */
    public static String getResourceFolder() {
        return "./";
    }

    private Reagan() {
    } // disable construction of Reagens

    private static void dispatchCommands() {
//        while (!commandQueue.isEmpty()) {
//            commandQueue.remove().act();
//        }
    	
    	int i=0; // don't allow anything to lock up main thread, spread it out
    	while (!mainThread.isEmpty() && (++i) < 16) {
    		mainThread.remove().act();
    	}
    }
}
