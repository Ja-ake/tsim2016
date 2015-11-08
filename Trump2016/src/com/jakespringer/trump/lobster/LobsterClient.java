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
    
    public CommandHandler commandDispatch;
    public boolean dictator = false;

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
//                System.out.println(msg);
                String[] args = msg.split("\\s");
                int messageId = 0;
                if (args[args.length-1].startsWith("client.")) {
                	 messageId = Integer.parseInt(args[args.length-1].replace("client.", ""));
                	 String[] argsCopy = args.clone();
                	 args = new String[argsCopy.length-1];
                	 for (int i=0; i<args.length; ++i) {
                		 args[i] = argsCopy[i];
                	 }
                }
                switch (args[0]) {
                case "SETID":
                    try {
                        id = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                    }
                    break;
                case "DICTATOR":
                	try {
                		dictator = Integer.parseInt(args[1]) == id ? true : false;
                    } catch (NumberFormatException e) {
                    }
                default:
                	if (commandDispatch != null) commandDispatch.handle(messageId, args);
                	break;
                }
            } else try {
                Thread.sleep(1);
            } catch (InterruptedException e) { }
        }
    }
    
    public int getId() {
    	return id;
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
