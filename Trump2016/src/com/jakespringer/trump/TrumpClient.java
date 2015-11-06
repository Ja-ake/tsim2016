package com.jakespringer.trump;

import java.io.DataInputStream;
import java.io.IOException;
import com.jakespringer.trump.lobster.LobsterClient;
import com.jakespringer.trump.lobster.LobsterServer;

public class TrumpClient {
    public static void main(String[] args) throws IOException {  
//        Thread t = new Thread(() -> new LobsterServer(55555));
//        t.start();
        
        LobsterClient client = new LobsterClient("192.168.1.200", 55555);
        DataInputStream scanner = new DataInputStream(System.in);
        
        Thread d = new Thread(client::run);
        d.start();
        
        boolean done = false;
        while (!done) {
            while (scanner.available() > 0) {
                client.queued.offer(scanner.readLine());
            }
            
//            String msg;
//            while ((msg = client.received.poll()) != null) {
//            }
        }
        
        scanner.close();
    }
}
