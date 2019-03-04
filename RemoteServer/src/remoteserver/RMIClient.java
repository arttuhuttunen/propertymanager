package remoteserver;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIClient extends Thread{

    remoteInterface comp;
    private static remoteInterface look_up;
    public RMIClient() throws MalformedURLException, RemoteException, NotBoundException {

        //Security manager is needed. Remember policy file and VM parameter again.
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        //TODO: RMI client connection


    }
    public void run() {
        try {
            look_up = (remoteInterface) Naming.lookup("//localhost/RMIServer");
            System.out.println(look_up.hello());
        } catch (Exception e) {e.printStackTrace();}
    }


   //TODO: Create needed requests to control server


}


