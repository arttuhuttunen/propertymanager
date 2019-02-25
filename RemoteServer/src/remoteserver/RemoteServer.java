package remoteserver;
//import java.util.rmi;
//import java.util;
//import java.rmi.server;

import java.net.InetSocketAddress;

public class RemoteServer {

    public RemoteServer() {
        //TODO: create and start your servers, make connection needed

        /*getClientHost();
        getLog();
        setLog(OutputStream out);
        public static String getClientHost()
                            throws ServerNotActiveException;
        public static void setLog(OutputStream out);
        public static PrintStream getLog();
*/

    }

    public static void main(String[] args) {
        RemoteServer RS = new RemoteServer();
        WWWServer ws = new WWWServer(new InetSocketAddress((8000)));
        ws.run();
    }

}
