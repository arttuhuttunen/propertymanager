package controlserver;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.security.ntlm.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

public class lightswitchServer extends Thread {
    private String IP;
    private int port;
    private Socket[] sockets = new Socket[8];

    public lightswitchServer(String IP, int port) {
      //constructor, create server here and bind it to IP & port
        this.IP = IP;
        this.port = port;
    }

    public void run() {
        //And here you should listen to the server socket
        System.out.println("Lightswitchserver started");
        try {
            long threadId = Thread.currentThread().getId();
            System.out.println("Thread n:o " + threadId + " running");
            ServerSocket SS = new ServerSocket(port);
            Socket cs = SS.accept();
            PrintWriter out = new PrintWriter(cs.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            System.out.println("Incoming connection from " + cs.getInetAddress() + " with port " + cs.getPort());
        } catch (IOException e) {e.printStackTrace();}
    }

    protected void sendStatus(int ID, Boolean value) {
        int tempID = ID;
        Boolean tempValue = value;
    }
}


