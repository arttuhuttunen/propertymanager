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
    private enum Mode {OFF, ON, NOTCONNECTED}
    //private Mode[] lightstatus = new Mode[10];


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


        //Sets light statuses for testing purposes before actually getting light statuses
        /*for (int i = 1; i < 10; i++) {
            if (i % 2 == 0) {
                lightstatus[i] = Mode.ON;
            } else {
                lightstatus[i] = Mode.OFF;
            }
        }*/

    }
    public void run() {
        server.start();
    }

    public void setLightstatus(int lightID, Mode value) throws RemoteException {
        //lightstatus[lightID] = value;
        RMImaster.sendLightstatus(value.toString(), lightID);
    }
    public void setTemperature(String temperature) throws RemoteException{
        RMImaster.sendTemperature(temperature);
    }
    public String getTemperature() throws RemoteException{
        System.out.println("Method getTemperature() started");
        //System.out.println(RMImaster.getTemperature());
        System.out.println("Local getTemperature() value is : " + RMImaster.getTemperature());
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
                System.out.println(inputString);
                String attribute = inputString.substring(0, inputString.indexOf('='));
                System.out.println(attribute);
                if (attribute.contains("ls")) {
                    System.out.println("LS debug");
                    int lsInt = Integer.parseInt(attribute.substring(2));
                    System.out.println("Ls test with id" + lsInt);
                    if (master.getLightstatus(lsInt) == "ON") {
                        master.setLightstatus(lsInt, Mode.OFF);
                        System.out.println(master.getLightstatus(lsInt));
                        redirectToIndex(t);
                    } else if (master.getLightstatus(lsInt) == "OFF") {
                        master.setLightstatus(lsInt, Mode.ON);
                        System.out.println(master.getLightstatus(lsInt));
                        redirectToIndex(t);
                    }
                }
                if (attribute.contains("temperature")) {
                    String temperature = inputString.substring(inputString.indexOf('=') + 1);
                    master.setTemperature(temperature);
                    redirectToIndex(t);
                }
                /*Headers headers = t.getResponseHeaders();
                headers.add("Content-type", "text/html");
                headers.add("Location", "/");
                t.sendResponseHeaders(303, 0);
                t.close();*/



            }


            //HTML file loading
            File file = new File("RemoteServer\\src\\remoteserver\\index.html");
            String mime = "text/html";
            System.out.println("Loading file from " + file.getPath());
            htmlEditor(file);
            System.out.println("Bug test 2: HTML editor finished");
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
            System.out.println("Sending html file...");

        }
        private void htmlEditor(File html) {
            //This loop assigns test values to buttons



            try {
                System.out.println("Bug test 1, HTML editor started");
                String line;
                List<String> lines = new ArrayList<String>();
                FileReader fileReader = new FileReader(html);
                System.out.println("fileReader initialized");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                System.out.println("bufferedReader started");
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("$testvalue")) {
                        line = line.replace("$testvalue", "If you see this, html file replacing works properly");
                    }
                    if (line.contains("$temperature")) {
                        line = line.replace("$temperature", master.getTemperature());
                    }
                    System.out.println("Temperature parsed and reaplaced");
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

