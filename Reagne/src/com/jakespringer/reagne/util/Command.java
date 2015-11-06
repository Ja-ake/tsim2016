package com.jakespringer.reagne.util;

/**
 * Functional interface for a function that takes no arguments and produces no
 * result.
 */
public interface Command {
    
    /**
     * Called to run the command.
     */
    public void act();
}
