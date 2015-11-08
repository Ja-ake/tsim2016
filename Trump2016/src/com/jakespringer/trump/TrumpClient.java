package com.jakespringer.trump;

import java.io.DataInputStream;
import java.io.IOException;

import com.jakespringer.trump.lobster.LobsterClient;
import com.jakespringer.trump.lobster.LobsterServer;

public class TrumpClient {
    public static void main(String[] args) {  
//        Thread t = new Thread(() -> new LobsterServer(55555));
//        t.start();
    	Thread d = null;
    	DataInputStream scanner = null;
        while (true) {
        	try {
		        LobsterClient client = new LobsterClient("localhost", 55555);
		        scanner = new DataInputStream(System.in);
		        
		        d = new Thread(client::run);
		        d.start();
		        
		        System.out.println("Connected to server.");
		        
		        client.commandDispatch = (i, s) -> {
		        	System.out.print("Recv-"+i+": ");
		        	for (String str : s) {
		        		System.out.print(str + " ");
		        	}
		        	System.out.println();
		        };
		        
		        boolean done = false;
		        while (!done) {
		            while (scanner.available() > 0) {
		            	String msg = scanner.readLine();
		                client.queued.offer(msg);
		                System.out.println("Sent: " + msg);
		            }
		        }
		        
		        scanner.close();
        	} catch (IOException e) {
        		if (d != null && d.isAlive()) d.stop();
        		if (scanner != null)
					try {
						scanner.close();
					} catch (IOException e2) {
					}
        		
        		
        		System.out.println("An error occurred, retrying in 3000ms");
        		try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
				}
        	}
        }
    }
}
