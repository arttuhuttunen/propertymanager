package remoteserver;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.rmi.RemoteException;
import java.util.*;

import com.sun.corba.se.spi.activation.Server;
import com.sun.deploy.net.HttpResponse;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class WWWServer {

    private HttpServer server;
    RMIClient RMImaster;


    public WWWServer(InetSocketAddress address) {
        //TODO: Create http-server instance and run it
        WWWHandler www = new WWWHandler();
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {e.printStackTrace();}
        WWWHandler WWW = new WWWHandler();
        WWW.master = this;
        server.createContext("/", WWW);
        long threadID;
        threadID = Thread.currentThread().getId();
        System.out.println("Thread n:o " + threadID + " started");

    }
    public void run() {
        server.start();
    }

    public void setLightstatus(int lightID, String value) throws RemoteException {
        RMImaster.sendLightstatus(value, lightID);
    }

    public void setTemperature(String temperature) throws RemoteException{
        RMImaster.sendTemperature(temperature);
    }

    public String getTemperature() throws RemoteException{
        return RMImaster.getTemperature();
    }
    public String getLightstatus(int lightID) throws RemoteException{
        return RMImaster.getLightstatus(lightID);
    }

    //TODO: Create handlers for requests
    static class WWWHandler implements HttpHandler {
        WWWServer master;
        private void redirectToIndex(HttpExchange t) throws IOException {
            Headers headers = t.getResponseHeaders();
            headers.add("Content-type", "text/html");
            headers.add("Location", "/");
            t.sendResponseHeaders(303, 0);
            t.close();
        }

        public void handle(HttpExchange t)  throws IOException{

            //Thread printing procedure for debugging
            long threadID;
            threadID = Thread.currentThread().getId();
            System.out.println("Thread n:o " + threadID + " started");
            OutputStream os;

            if(t.getRequestMethod().equals("POST")) {


                InputStream io = t.getRequestBody();
                BufferedReader in = new BufferedReader(new InputStreamReader(io));
                String inputString = in.readLine();
                String attribute = inputString.substring(0, inputString.indexOf('='));

                //Parces post request and perform operation based on element
                if (attribute.contains("ls")) {
                    int lsInt = Integer.parseInt(attribute.substring(2));

                    if (master.getLightstatus(lsInt).equals("OFF")) {
                        master.setLightstatus(lsInt, "ON");

                        redirectToIndex(t);
                    } else if (master.getLightstatus(lsInt).equals("ON")) {
                        master.setLightstatus(lsInt, "OFF");
                        redirectToIndex(t);
                    } else {
                        master.setLightstatus(lsInt, "ON");
                    }
                }
                if (attribute.contains("temperature")) {
                    String temperature = inputString.substring(inputString.indexOf('=') + 1);
                    master.setTemperature(temperature);
                    redirectToIndex(t);
                }
            }


            //HTML file loading
            File file = new File("RemoteServer\\src\\remoteserver\\index.html");
            String mime = "text/html";
            htmlEditor(file);
            File newFile = new File("RemoteServer\\src\\remoteserver\\index2.html");



            //Sending HTML-file
            Headers h = t.getResponseHeaders();
            h.set("Content type", mime);
            byte [] bytearray  = new byte [(int)newFile.length()];
            FileInputStream fs = new FileInputStream(newFile);
            BufferedInputStream bs = new BufferedInputStream(fs);
            bs.read(bytearray, 0, bytearray.length);
            t.sendResponseHeaders(200, file.length());
            os = t.getResponseBody();
            os.write(bytearray, 0, bytearray.length);
            os.close();

        }

        //Purpose of this method is to create new HTML-file on disk, which will be sent to user
        //This is one kind of way to create dynamic website without using javascript or .jsp
        private void htmlEditor(File html) {

            try {
                String line;
                List<String> lines = new ArrayList<String>();
                FileReader fileReader = new FileReader(html);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("$temperature")) {
                        line = line.replace("$temperature", master.getTemperature());
                    }
                    //Loop changes button values to light values
                    for (int i = 1; i < 10; i++) {
                        if(line.contains("$value" + i)) {
                            line = line.replace("$value" + i, master.getLightstatus(i));
                        }
                    }
                    lines.add(line);
                }
                fileReader.close();
                bufferedReader.close();

                FileWriter fileWriter = new FileWriter("RemoteServer\\src\\remoteserver\\index2.html");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                for (String s : lines) {
                    bufferedWriter.write(s);
                }
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (Exception e) {e.printStackTrace();}
        }
    }

}

