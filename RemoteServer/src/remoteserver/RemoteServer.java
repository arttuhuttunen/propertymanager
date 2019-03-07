package remoteserver;


import java.net.InetSocketAddress;

public class RemoteServer {

    public RemoteServer() {
        //TODO: create and start your servers, make connection needed

    }

    public static void main(String[] args) {
        long threadID;
        threadID = Thread.currentThread().getId();
        System.out.println("Thread n:o " + threadID + " started");
        try {
            RMIClient rm = new RMIClient();
            WWWServer ws = new WWWServer(new InetSocketAddress((8000)));
            rm.start();
            ws.RMImaster = rm;
            ws.run();

        } catch (Exception e) {e.printStackTrace();}
    }

}
