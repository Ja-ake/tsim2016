package com.jakespringer.reagne.net;

public class SimpleClient {
    private final String host;
    private final int port;
    private final byte[] sendBuffer;
    private final byte[] receiveBuffer;
    
    public SimpleClient(final String hostname, final int portnumber) {
        host = hostname;
        port = portnumber;
        sendBuffer = new byte[1024];
        receiveBuffer = new byte[1024];
    }
}
