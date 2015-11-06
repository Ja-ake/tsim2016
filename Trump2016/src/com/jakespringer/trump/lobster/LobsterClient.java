package com.jakespringer.trump.lobster;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class LobsterClient {
    private Socket socket;
    private DataInputStream console;
    private DataOutputStream streamOut;
    
    public LobsterClient(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            System.out.println("Connected: " + socket + ".");
            console = new DataInputStream(System.in);
            streamOut = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }
        String line = "";
        while (!line.equals(".bye")) {
            try {
                line = console.readLine();
                streamOut.writeUTF(line);
                streamOut.flush();
            } catch (IOException ioe) {
                System.out.println("Sending error: " + ioe.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        new LobsterClient("192.168.1.200", 55555);
    }
}
