package controlserver;

import javafx.concurrent.Task;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import remoteserver.remoteInterface;



public class RMIServer extends UnicastRemoteObject implements remoteInterface {
    private int RMIport;

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
        System.out.println("Remote method hello() called");
        return "RMI connection test completed successfully.";
    }

    public String executeTask(String id) throws RemoteException {
        return null;
    }

    public void run() {
        try{
            System.out.println("Starting RMI server");
            //remoteInterface stub = (remoteInterface) UnicastRemoteObject.exportObject(this, RMIport);
            //RMIServer rmi = new RMIServer(1099);
            Registry registry = LocateRegistry.createRegistry(8888);
            registry.rebind("RMIServer", this);


        } catch (Exception e) {e.printStackTrace();}
    }


}

