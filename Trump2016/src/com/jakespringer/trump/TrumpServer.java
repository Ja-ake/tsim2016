package com.jakespringer.trump;

import com.jakespringer.trump.lobster.LobsterServer;

public class TrumpServer {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> new LobsterServer(55555));
        t.start();
        t.join();
    }
}
