package lightswitch;

import javafx.scene.effect.Light;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;


public class Lightswitch {
    private JPanel mainPanel;
    private JButton switchbutton;
    private JLabel statuslabel;
    private Mode lightstatus;
    private enum Mode {OFF, ON, NOTCONNECTED}
    private static int ID;
    private Socket s;

    public Lightswitch() {
        switchbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChange(ID);
            }
        });

        connectSwitch(ID);
    }

    private Socket getSocket() {
        return s;
    }

    protected void connectSwitch(int ID) {
        //TODO: Create an socket connection connection to server
        int port = 8080;
        while (true) {
            try {
                s = new Socket("localhost", port);
                OutputStream os = s.getOutputStream();
                String tempID = Integer.toString(ID);
                tempID += "\n";
                System.out.println("My ID is: " + tempID);
                os.write(tempID.getBytes());
                os.flush();
                ConnListener cl = new ConnListener(s);
                cl.master = this;
                cl.start();
                break;
            } catch (IOException e) {
                System.out.println("Error with connection, trying to reconnect in 10 seconds...");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {ie.printStackTrace();}
            }
        }
    }
    class ConnListener extends Thread {
        private Socket socket;
        Lightswitch master;
        String tempString;
        private ConnListener(Socket s) {
            socket = s;
        }
        public void run() {
            try {
                long threadId = Thread.currentThread().getId();
                System.out.println("Connectionlistener started");
                System.out.println("Thread n:o " + threadId + " started");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream os = socket.getOutputStream();
                while (true) {
                    System.out.println("Waiting for status change...");
                    //This try-catch block checks if server connection crashes
                    //After notifying about crash, it resets socket and tries to reconnect
                    try {
                        tempString = in.readLine();
                    } catch (SocketException s) {
                        System.out.println("Connection to server lost, please restart switch");
                        master.s = null;
                        master.connectSwitch(ID);
                        break;
                    }
                    System.out.println(tempString);
                    master.receiveStatus(tempString);
                }
            } catch (IOException e) {e.printStackTrace();}
        }
    }


    protected void sendChange(int ID)  {
        //TODO: Send lightswitch action pressed to server
        String status;
        Socket tempSocket = this.getSocket();
        try{
            PrintWriter out = new PrintWriter(tempSocket.getOutputStream(), true);
            if (lightstatus == Mode.ON) {
                setLightStatus(Mode.OFF);
                status = "OFF\n";
            } else if (lightstatus == Mode.OFF) {
                setLightStatus(Mode.ON);
                status = "ON\n";
            } else  {
                connectSwitch(ID);
                status = "ON\n";
            }
            out.write(status);
            out.flush();
            System.out.println("Sending status " + status + " to server");
        } catch (IOException e) {e.printStackTrace();}

    }

    protected void receiveStatus(String statusStr) {
        //Set default to not connected
        Mode receivedMode = Mode.NOTCONNECTED;
        //TODO: receive status of the light from the server
        System.out.println(statusStr);
        if (statusStr.equals("ON")) {
            receivedMode = Mode.ON;
        } else if (statusStr.equals("OFF")) {
            receivedMode = Mode.OFF;
        } else {throw new IllegalStateException();}


        //Update view to correspond the received status
        setLightStatus(receivedMode);
    }


//Setter for the status of the light
// Modes are OFF, ON, NOTCONNECTED
    public void setLightStatus(Mode input) {
            if (input == Mode.ON) {
                lightstatus = Mode.ON;
                statuslabel.setText("Lights on");
                statuslabel.setBackground(Color.green);
            }
            else if (input == Mode.OFF){
                lightstatus = Mode.OFF;
                statuslabel.setText("Lights off");
                statuslabel.setBackground(Color.red);
            }
            else if (input == Mode.NOTCONNECTED){
                lightstatus = Mode.NOTCONNECTED;
                statuslabel.setText("Not connected");
                statuslabel.setBackground(Color.yellow);
            }
    }

    public static void main(String[] args) {
        //No need to edit main.
        //ID number is read from th first command line parameter
        if (args.length >0) {
            try {
                ID = Integer.parseInt(args[0]);
            } catch (Exception ex) {
                System.err.println("Error reading arguments");
                ID = 0;
            }
        }
        else {ID = 0; }

        JFrame frame = new JFrame("Lightswitch");
        frame.setContentPane(new Lightswitch().mainPanel);
        frame.setTitle("Lightswitch");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
