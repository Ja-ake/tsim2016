package com.jakespringer.reagne;

import java.util.LinkedList;
import java.util.Queue;
import com.jakespringer.reagne.game.World;
import com.jakespringer.reagne.util.Command;

public class Reagne {
    private static Queue<Command> commandQueue = new LinkedList<>();
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
     * A stream that fires once at the start of when {@link Reagne#run()} is
     * called.
     */
    public static final Signal<Object> initialize = new Signal<>(new Object());

    /**
     * Queues a command for executing during the next update loop.
     * 
     * @param command
     *            the command to execute
     */
    public static void queueCommand(Command command) {
        if (command == null)
            throw new NullPointerException();
//        commandQueue.add(command);
        command.act();
    }

    /**
     * Run the game loop.
     */
    public static void run() {
        // initialize the game
        initialize.send(initialize.get());
        dispatchCommands();

        // run the game loop
        long currentTime = System.nanoTime();
        long previousTime = currentTime;
        while (running) {
            currentTime = System.nanoTime();
            double deltaTime = ((double) currentTime - (double) previousTime) * 0.000001;
            continuous.send(Double.valueOf(deltaTime));
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

    /**
     * Returns the default resource folder.
     * 
     * @return the resource folder
     */
    public static String getResourceFolder() {
        return "./";
    }

    private Reagne() {} // disable construction of Reagens

    private static void dispatchCommands() {
//        while (!commandQueue.isEmpty()) {
//            commandQueue.remove().act();
//        }
    }
}
