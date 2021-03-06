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
    private final List<String> templateHTML;


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
        char separator = File.separatorChar;
        File file = new File("RemoteServer"+ separator +"src"+ separator + "remoteserver"+ separator +"index.html");
        FileReader fileReader;
        templateHTML = new ArrayList<>();
        try {
            String line;
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                templateHTML.add(line);
            }
            System.out.println("Template read to memory successful");
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    private List<String> getTemplateHTML() {
        return templateHTML;
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

                //Parses post request and perform operation based on element
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

            String mime = "text/html";
            String stringToSend = htmlEditor();

            //Sending HTML-file
            Headers h = t.getResponseHeaders();
            h.set("Content type", mime);
            byte [] bytearray  = new byte [stringToSend.length()];
            InputStream is = new ByteArrayInputStream(stringToSend.getBytes());
            BufferedInputStream bs = new BufferedInputStream(is);
            bs.read(bytearray, 0, bytearray.length); //Reads file to bitstream
            t.sendResponseHeaders(200, stringToSend.length());
            os = t.getResponseBody();

            os.write(bytearray, 0, bytearray.length);
            os.close();

        }

        /*Purpose of this method is to replace '$' marked keywords in template html loaded in initialization,
        and replace them with proper values
         */
        private String htmlEditor() {

            try {
                List<String> templateString = master.getTemplateHTML();
                StringBuilder lines = new StringBuilder();

                for (String line : templateString) {
                    if (line.contains("$temperature")) {
                        line = line.replace("$temperature", master.getTemperature());
                    }
                    //Loop changes button values to light values
                    for (int i = 1; i < 10; i++) {
                        if(line.contains("$value" + i)) {
                            line = line.replace("$value" + i, master.getLightstatus(i));
                        }
                    }

                    lines.append(line);
                }

                return lines.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}

