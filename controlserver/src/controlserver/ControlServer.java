package controlserver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;


public class ControlServer {

    //GUI variables, Do not edit
    private JTextField temperature;
    private JPanel mainPanel;
    private JLabel temperatureLabel;
    private JButton light7;
    private JButton light8;
    private JButton light9;
    private JButton light4;
    private JButton light1;
    private JButton light5;
    private JButton light2;
    private JButton light6;
    private JButton light3;
    //End of GUI variables
    private enum Mode {OFF, ON, NOTCONNECTED}
    private Mode[] lightstatus = new Mode[10];
    private ConcurrentHashMap<Integer, JButton> lights;

    lightswitchServer ls = new lightswitchServer("localhost", 8080);

    public ControlServer() {
        //constructor
            light1.addActionListener(new buttonAction());
            light2.addActionListener(new buttonAction());
            light3.addActionListener(new buttonAction());
            light4.addActionListener(new buttonAction());
            light5.addActionListener(new buttonAction());
            light6.addActionListener(new buttonAction());
            light7.addActionListener(new buttonAction());
            light8.addActionListener(new buttonAction());
            light9.addActionListener(new buttonAction());

            lights = new ConcurrentHashMap();
            lights.put(1, light1);
            lights.put(2, light2);
            lights.put(3, light3);
            lights.put(4, light4);
            lights.put(5, light5);
            lights.put(6, light6);
            lights.put(7, light7);
            lights.put(8, light8);
            lights.put(9, light9);

        temperature.setText("23");
        startServers();

    }

    private class buttonAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton tempB = (JButton)e.getSource();
            int ID = Integer.parseInt(tempB.getName());
            toggleLightstatus(ID);
        }

    }

    public void setLightstatus (int ID, String status){
        int arrayid = ID -1;
        if (status.equals("ON")) {
            lights.get(ID).setText("Light "+ ID +" ON");
            lightstatus[arrayid] = Mode.ON;
            System.out.println("Setting serverside lightstatus " + status + " to lamp id " + ID);
            sendLightStatus(ID, lightstatus[arrayid]);
        } else if (status.equals("OFF")) {
            lights.get(ID).setText("Light "+ ID +" OFF");
            lightstatus[arrayid] = Mode.OFF;
            System.out.println("Setting serverside lightstatus " + status + " to lamp id " + ID);
            sendLightStatus(ID, lightstatus[arrayid]);
        }
        mainPanel.updateUI();
    }
    public void setTemperature(String temperature) {
        this.temperature.setText(temperature);
        mainPanel.updateUI();
    }

    public void toggleLightstatus(int ID) {
        int arrayid = ID -1;
        if(lightstatus[arrayid] == Mode.ON) {
            lights.get(ID).setText("Light "+ ID +" OFF");
            lightstatus[arrayid] = Mode.OFF;
            sendLightStatus(ID, Mode.OFF);
        }
        else if (lightstatus[arrayid] == Mode.OFF) {
            lights.get(ID).setText("Light "+ ID +" ON");
            lightstatus[arrayid] = Mode.ON;
            sendLightStatus(ID, Mode.ON);
        }
        else {
            lights.get(ID).setText("Light "+ ID +" ON");
            lightstatus[arrayid] = Mode.ON;
            sendLightStatus(ID, Mode.ON);
        }
    }

    public void sendLightStatus(int ID, Mode input) {
        //TODO: Send change to lightswitches
        Boolean valueForSending;
        if (input == Mode.ON) {
            valueForSending = true;
        } else if (input == Mode.OFF) {
            valueForSending = false;
        } else {
            throw new IllegalArgumentException();
        }
        ls.sendStatus(ID, valueForSending);
    }


    //Getter for Lightstatus
    public String getLightstatus(int ID) {
        return lightstatus[ID - 1].toString();
    }


    //Getter for temperature
    public String getTemperature() {
        return temperature.getText();
    }

    private void startServers() {
        //TODO: Start your RMI- and socket-servers here
        ls.master = this;
        ls.start();
        for (int i = 0; i < 10; i++) {
            lightstatus[i] = Mode.OFF;
        }
        try {
            RMIServer rmi = new RMIServer(8888);
            rmi.master = this;
            rmi.run();
        } catch (RemoteException r) {r.printStackTrace();}



    }


    public static void main(String[] args) {
        //No need to edit main method, start your servers in  startServers() method

        JFrame frame = new JFrame("Controlserver");
        frame.setContentPane(new ControlServer().mainPanel);
        frame.setTitle("Controlserver");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        long threadId = Thread.currentThread().getId();
        System.out.println("Thread n:o " + threadId + " started");
    }

}

