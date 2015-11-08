package com.jakespringer.trump.lobster;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import com.jakespringer.reagan.util.ImmutableTuple2;

/**
 * Trump had a quote about selling lobsters
 * over the Internet.
 */
public class LobsterServer {
    private Object lock = new Object();
    
    private ServerSocket server;
    private List<ImmutableTuple2<ClientHandler, Thread>> handlers;
    private int currentId;
    private int authoritarian = 0;
    
    public LobsterServer(int port) {
        currentId = 100; // arbitrary number
        handlers = new LinkedList<>();
        
        try {
            System.out.println("[INFO] Binding to port " + port + ".");
            server = new ServerSocket(port);
            System.out.println("[INFO] Server started: " + server + ".");
            
            Thread hc = new Thread(() -> {
                while (true) {
                    synchronized (lock) {
                        handlers.removeIf(x -> !x.left.isConnected());
                        boolean dictConnected = false;
                        for (ImmutableTuple2<ClientHandler, Thread> han : handlers) {
                        	if (authoritarian == han.left.id) dictConnected = true;
                        }
                        if (!dictConnected) authoritarian = 0;
                        
                        if (authoritarian == 0) {
                        	if (!handlers.isEmpty()) {
                        		authoritarian = handlers.get(0).left.id;
                        		for (ImmutableTuple2<ClientHandler, Thread> han : handlers) {
                            		handlers.get(0).left.queued.offer("DICTATOR " + authoritarian);
                        		}
                        	}
                        }
                    }
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException i) {
                    }
                }
            });
            
            hc.setDaemon(true);
            hc.start();
            
            Thread handl = new Thread(() -> {
            	while (true) {
            		synchronized (lock) {
            			for (ImmutableTuple2<ClientHandler, Thread> handler : handlers) {
            				String message;
            				while ((message = handler.left.received.poll()) != null) {
            					for (ImmutableTuple2<ClientHandler, Thread> handler2 : handlers) {
            						if (handler != handler2) {
            							handler2.left.queued.offer(message + " client." + handler.left.id);
            						}
            					}
            				}
            			}
            		}
            		
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException i) {
                    }
            	}
            });
            
            handl.setDaemon(true);
            handl.start();
            
            boolean done = false;
            while (!done) {
                try {
                    Socket socket = server.accept();
                    ClientHandler handle = new ClientHandler(socket, ++currentId);
                    handle.queued.offer("SETID "+currentId);
            		handle.queued.offer("DICTATOR " + authoritarian);
                    Thread thread = new Thread(handle);
                    synchronized (lock) {
                        handlers.add(new ImmutableTuple2<>(handle, thread));
                        thread.setDaemon(true);
                        thread.start();
                    }
                } catch (IOException e) {
                    System.out.println("[SEVERE] " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("[SEVERE] " + e);
        }
    }
}
