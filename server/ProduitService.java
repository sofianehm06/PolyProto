package server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProduitService extends Remote {

    String ajouter(String nom, double prix) throws RemoteException;
    String listerTous()                      throws RemoteException;
    String supprimer(int id)                 throws RemoteException;
}