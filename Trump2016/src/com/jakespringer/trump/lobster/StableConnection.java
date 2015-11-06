package com.jakespringer.trump.lobster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class StableConnection {
    private long currentTime;
    private long previousTime;
    private long elapsedAccum;
    private long keepAlive;
    private boolean disconnected;
    
    private Socket socket;
    private DataInputStream streamIn;
    private DataOutputStream streamOut;
    
    public boolean isDisconnected() {
        return disconnected;
    }
    
    public void run() {
        currentTime = System.currentTimeMillis();
        previousTime = currentTime;
    }
}
