package com.jakespringer.trump.lobster;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private long currentTime;
    private long previousTime;
    private long elapsedAccum;
    private long keepAlive;
    
    private Socket socket;
    private boolean disconnected;
    private DataInputStream streamIn;
    private DataOutputStream streamOut;
    private int id;
    
    public ClientHandler(Socket sock, int i) throws IOException {
        socket = sock;
        disconnected = false;
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        id = i;
        
        currentTime = System.currentTimeMillis();
        previousTime = currentTime;
        elapsedAccum = 0;
        keepAlive = 10000; // ten seconds
    }
    
    public boolean isConnected() {
        return !disconnected;
    }
    
    @Override
    public void run() {
        currentTime = System.currentTimeMillis();
        previousTime = currentTime;
        
        try {
            while (!disconnected) {
                currentTime = System.currentTimeMillis();
                long elapsed = currentTime - previousTime;
                if (elapsed < 0) {
                    previousTime = currentTime;
                    continue;
                }
                
                elapsedAccum += elapsed;
                keepAlive -= elapsed;
                
                if (keepAlive < 0) {
                    disconnected = true;
                }
                
                if (elapsedAccum > 1000) {
                    streamOut.writeUTF("PING");
                    streamOut.flush();
                    elapsedAccum = 0;
                }
                
                if (streamIn.available() > 0) {
                    String line = streamIn.readUTF();   
                    
                    if (line.startsWith("PING")) {
                        keepAlive = 10000; // ten seconds
                    }
                    
                    System.out.println("[Client "+id+"] "+line);
                }
                
                previousTime = currentTime;
            }
            
            System.out.println("[INFO] Client " + id + " timed out and has been disconnected.");
        } catch (IOException e) {
            disconnected = true;
            System.out.println("[INFO] Client " + id + " disconnected and created the exception: " + e + ".");
        }
        
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
