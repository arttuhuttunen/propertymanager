package remoteserver;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.Buffer;

import com.sun.corba.se.spi.activation.Server;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class WWWServer {

    private HttpServer server;

    public WWWServer(InetSocketAddress address) {
        //TODO: Create http-server instance and run it
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {e.printStackTrace();}
        server.createContext("/", new WWWHandler());
        long threadID;
        threadID = Thread.currentThread().getId();
        System.out.println("Thread n:o " + threadID + " started");

    }
    public void run() {
        server.start();
    }


    //TODO: Create handlers for requests
    static class WWWHandler implements HttpHandler {
        public void handle(HttpExchange t)  throws IOException{
            //Thread printing procedure for debugging
            long threadID;
            threadID = Thread.currentThread().getId();
            System.out.println("Thread n:o " + threadID + " started");




            File file = new File("RemoteServer\\src\\remoteserver\\index.html");
            String mime = "text/html";
            System.out.println("Loading file from " + file.getPath());
            Headers h = t.getResponseHeaders();
            h.set("Content type", mime);
            String response = "WWW server up and running";
            byte [] bytearray  = new byte [(int)file.length()];
            FileInputStream fs = new FileInputStream(file);
            BufferedInputStream bs = new BufferedInputStream(fs);
            bs.read(bytearray, 0, bytearray.length);
            t.sendResponseHeaders(200, file.length());
            OutputStream os = t.getResponseBody();
            os.write(bytearray, 0, bytearray.length);
            System.out.println("Sending html file...");


            if(t.getRequestMethod().equals("get")){
                String resp = "This is GET response test";
                t.sendResponseHeaders(200, resp.getBytes().length);
                os = t.getResponseBody();
                os.write(resp.getBytes());

            }
        }
    }

}
