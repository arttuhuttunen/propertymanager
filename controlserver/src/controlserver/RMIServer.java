package controlserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer extends UnicastRemoteObject  {

public RMIServer (int RMIport) throws RemoteException {
    //Constructor
    //You will need Security manager to make RMI work
    //Remember to add security.policy to your run time VM options
    //-Djava.security.policy=[YOUR PATH HERE]\server.policy
    if (System.getSecurityManager() == null) {
        System.setSecurityManager(new SecurityManager());
    }


}


}

