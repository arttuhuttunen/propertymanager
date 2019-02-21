package lightswitch;

import javafx.scene.effect.Light;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;


public class Lightswitch {
    private JPanel mainPanel;
    private JButton switchbutton;
    private JLabel statuslabel;
    private Mode lightstatus;
    private enum Mode {OFF, ON, NOTCONNECTED}
    private static int ID;

    public Lightswitch() {
        switchbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChange(ID);
            }
        });

        connectSwitch(ID);
    }

    protected void connectSwitch(int ID) {
        //TODO: Create an socket connection connection to server
        int port = 8080;
        try {
            Socket s = new Socket("localhost", port);
            OutputStream os = s.getOutputStream();
            String tempID = Integer.toString(ID);
            tempID += "\n";
            System.out.println(tempID);
            os.write(tempID.getBytes());
            os.flush();
            new ConnListener(s).start();
        } catch (IOException e) {e.printStackTrace();}
    }
    static class ConnListener extends Thread {
        private Socket socket;
        String tempString;
        private ConnListener(Socket s) {
            socket = s;
        }
        public void run() {
            try {
                long threadId = Thread.currentThread().getId();
                System.out.println("Thread n:o " + threadId + " started");
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream os = socket.getOutputStream();
                while (true) {
                    tempString = in.readLine();
                    Lightswitch ls = new Lightswitch();
                    ls.receiveStatus(tempString);
                }
            } catch (IOException e) {e.printStackTrace();}
        }
    }


    protected void sendChange(int ID) {} {
        //TODO: Send lightswitch action pressed to server

    }

    protected void receiveStatus(String statusStr) {
        //Set default to not connected
        Mode receivedMode = Mode.NOTCONNECTED;
        //TODO: receive status of the light from the server
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
