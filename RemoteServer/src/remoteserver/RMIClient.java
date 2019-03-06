package remoteserver;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Locale;

public class RMIClient extends Thread implements remoteInterface{

    remoteInterface comp;
    remoteInterface stub;
    public RMIClient() throws MalformedURLException, RemoteException, NotBoundException {

        //Security manager is needed. Remember policy file and VM parameter again.
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        //TODO: RMI client connection


    }

    public String getLightstatus(int ID) throws RemoteException {
        return stub.getLightstatus(ID);
    }

    public void setLightstatus(String lightstatus, int ID) throws RemoteException {

    }

    public String getTemperature() throws RemoteException {
        return stub.getTemperature();
    }

    public void setTemperature(String temperature) throws RemoteException {

    }

    public String executeTask(String id) {
        return null; //placeholder return
    }
    public String hello() {
        return null;
    }

    public void sendLightstatus(String status, int ID) throws RemoteException{
        stub.setLightstatus(status, ID);
    }

    protected void sendTemperature(String temperature) throws RemoteException {
        stub.setTemperature(temperature);
    }


    public void run() {
        try {
            Registry registry = LocateRegistry.getRegistry(8888);
            System.out.println(registry.list());
            stub = (remoteInterface) registry.lookup("RMIServer");
            String response = stub.hello();
            System.out.println(response);
        } catch (Exception e) {e.printStackTrace();}
    }


   //TODO: Create needed requests to control server


}


