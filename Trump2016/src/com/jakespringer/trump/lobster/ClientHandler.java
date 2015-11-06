package com.jakespringer.trump.lobster;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private boolean disconnected;
    private DataInputStream streamIn;
    private int id;
    
    public ClientHandler(Socket sock, int i) throws IOException {
        socket = sock;
        disconnected = false;
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        id = i;
    }
    
    public boolean isConnected() {
        return !disconnected;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                String line = streamIn.readUTF();
                System.out.println("["+id+"] "+line);
            }
        } catch (IOException e) {
            disconnected = true;
        }
        
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
