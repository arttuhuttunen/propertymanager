package controlserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class lightswitchServer extends Thread {
    private String IP;
    private int port;
    private Socket[] sockets = new Socket[9];
    ControlServer master;

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
            System.out.println("Thread n:o " + threadId + " started");
            int tempID;
            ServerSocket SS = new ServerSocket(port);
            while (true) {
                Socket cs = SS.accept();
                System.out.println("Incoming connection from " + cs.getInetAddress() + " with port " + cs.getPort());
                BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
                tempID = Integer.parseInt(in.readLine());
                System.out.println("Light with id " + tempID + " connected");
                sockets[tempID - 1] = cs;
                ConnHandler ch = new ConnHandler(sockets[tempID - 1], tempID);
                ch.master = this;
                ch.start();
            }
        } catch (IOException e) {e.printStackTrace();}
    }
    static class ConnHandler extends Thread {
        private Socket client;
        lightswitchServer master;
        String tempString;
        int ID;
        protected ConnHandler(Socket s, int ID) {
            client = s;
            this.ID = ID;
        }
        public void run() {
            try {
                long threadId = Thread.currentThread().getId();
                System.out.println("Thread n:o " + threadId + " started");
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                while (true) {
                    tempString = in.readLine();
                    master.receiveStatus(tempString, ID);
                }
            } catch (IOException e) {
                System.out.println("Connection lost to lightswitch ID " + ID);
                master.sockets[ID - 1] = null;
            }
        }
    }

    protected void receiveStatus (String status, int ID) {
        master.setLightstatus(ID, status);
    }

    protected void sendStatus(int ID, Boolean value) {
        Socket tempSocket = sockets[ID - 1];
        String valueToSend;
        if (value) {
            valueToSend = "ON\n";
        } else {
            valueToSend = "OFF\n";
        }
        try {
            PrintWriter out = new PrintWriter(tempSocket.getOutputStream(), true);
            out.write(valueToSend);
            System.out.println("Sending value to light id " + ID + " with value " + valueToSend);
            out.flush();
        } catch (IOException e) {e.printStackTrace();}
    }
}


