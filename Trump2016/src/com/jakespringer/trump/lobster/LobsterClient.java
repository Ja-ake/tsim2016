package com.jakespringer.trump.lobster;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LobsterClient {
    private Socket socket;
    private ServerHandler handler;
    private Thread thread;
    private int id;
    
    public final ConcurrentLinkedQueue<String> queued;
    public final ConcurrentLinkedQueue<String> received;

    public LobsterClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        handler = new ServerHandler(socket);
        
        queued = handler.queued;
        received = handler.received;
        
        thread = new Thread(handler);
        thread.setDaemon(true);
        thread.start();
    }
    
    public void run() {
        boolean start = false;
        while (handler.isConnected() || !start) {
            if (handler.isConnected()) start = true;
            
            String msg = received.poll();
            if (msg != null) {
                System.out.println(msg);
                String[] args = msg.split("\\s");
                switch (args[0]) {
                case "SETID":
                    if (args.length == 1) {
                        try {
                            id = Integer.parseInt(args[1]);
                            System.out.println("id = "+id);
                        } catch (NumberFormatException e) {
                        }
                    }
                    break;
                }
            } else try {
                Thread.sleep(1);
            } catch (InterruptedException e) { }
        }
    }
    
    public void stop() {
        handler.disconnect();
    }
    
    @Override
    public void finalize() {
        // not guarenteed to be called, but doesn't hurt
        stop();
    }
}
