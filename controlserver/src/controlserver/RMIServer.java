package controlserver;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import remoteserver.remoteInterface;



public class RMIServer extends UnicastRemoteObject implements remoteInterface {
    private int RMIport;
    ControlServer master;

    public RMIServer (int RMIport) throws RemoteException {
        //Constructor
        //You will need Security manager to make RMI work
        //Remember to add security.policy to your run time VM options
        //-Djava.security.policy=[YOUR PATH HERE]\server.policy
        super();
        this.RMIport = RMIport;
        System.out.println("RMI server construction successful");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }


    }
    public String hello() throws RemoteException{
        try {
            System.out.println("Incoming RMI Connection from " + UnicastRemoteObject.getClientHost());
        } catch (ServerNotActiveException S) {
            System.out.println("RMI Connection error");}
        return "RMI Connection successful";
    }

    public String executeTask(String id) throws RemoteException {
        return null;
    }

    public String getLightstatus(int ID) throws RemoteException {
        return master.getLightstatus(ID);
    }

    public void setLightstatus(String lightstatus, int ID) throws RemoteException {
        master.setLightstatus(ID, lightstatus);
    }

    public String getTemperature() throws RemoteException {
        return master.getTemperature();
    }

    public void setTemperature(String temperature) throws RemoteException {
        master.setTemperature(temperature);
    }

    public void run() {
        try{
            System.out.println("Starting RMI server");
            Registry registry = LocateRegistry.createRegistry(8888);
            registry.rebind("RMIServer", this);


        } catch (Exception e) {e.printStackTrace();}
    }


}

