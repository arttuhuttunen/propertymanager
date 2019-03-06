package remoteserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface remoteInterface extends Remote {
     enum Mode {OFF, ON, NOTCONNECTED}
     Mode[] lightstatus = new Mode[10];

     String executeTask(String id)  throws RemoteException;
     String hello() throws RemoteException;

     String getLightstatus(int ID) throws RemoteException;

     void setLightstatus(String lightstatus, int ID) throws  RemoteException;

     String getTemperature() throws RemoteException;
     void setTemperature(String temperature) throws RemoteException;



}