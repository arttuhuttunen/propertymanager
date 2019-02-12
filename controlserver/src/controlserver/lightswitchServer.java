package controlserver;

import com.sun.security.ntlm.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class lightswitchServer extends Thread {
    private String IP;
    private int port;
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
            new connectionHandler(cs).start();

        } catch (IOException e) {e.printStackTrace();}
    }
    static class connectionHandler extends Thread{
        private Socket server;
        private connectionHandler(Socket cs) {
            server = cs;
        }
        public void run(){
            try {
                PrintWriter out = new PrintWriter(server.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                System.out.println("Incoming connection from " + server.getInetAddress() + "with port " + server.getPort());
            } catch (IOException e) {e.printStackTrace();}
        }
    }
    protected void sendStatus(int ID, Boolean value) {
        int tempID = ID;
        Boolean tempValue = value;

    }
}


