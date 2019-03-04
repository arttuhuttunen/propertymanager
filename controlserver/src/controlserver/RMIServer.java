package controlserver;

import javafx.concurrent.Task;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class RMIServer extends UnicastRemoteObject{
    private int RMIport;

    public RMIServer (int RMIport) throws RemoteException {
        //Constructor
        //You will need Security manager to make RMI work
        //Remember to add security.policy to your run time VM options
        //-Djava.security.policy=[YOUR PATH HERE]\server.policy
        this.RMIport = RMIport;
        System.out.println("RMI server construction successful");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }


    }
    public String hello(String greet) throws RemoteException{
        return greet;
    }

    public void run() {
        try{
            System.out.println("Starting RMI server");
            Naming.rebind("//localhost/RMIServer", new RMIServer(RMIport));
            String test = "RMI transfer test";
            hello(test);
            /*RMIServer rmi = new RMIServer(RMIport);
            RMIServer send = (RMIServer) UnicastRemoteObject.exportObject(rmi,RMIport);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(test, send);*/
        } catch (Exception e) {e.printStackTrace();}
    }


}

