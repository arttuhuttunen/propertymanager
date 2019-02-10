package controlserver;

import com.sun.security.ntlm.Server;

import java.io.IOException;
import java.net.ServerSocket;

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
            ServerSocket SS = new ServerSocket(port);
            SS.accept();
        } catch (IOException e) {e.printStackTrace();}
        }


}
