package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ProduitServiceImpl extends UnicastRemoteObject implements ProduitService {

    private Database database;

    public ProduitServiceImpl(Database database) throws RemoteException {
        super();
        this.database = database;
    }

    @Override
    public String ajouter(String nom, double prix) throws RemoteException {
        System.out.println("[RMI] ajouter(" + nom + ", " + prix + ")");
        return database.ajouter(nom, prix);
    }

    @Override
    public String listerTous() throws RemoteException {
        System.out.println("[RMI] listerTous()");
        return database.listerTous();
    }

    @Override
    public String supprimer(int id) throws RemoteException {
        System.out.println("[RMI] supprimer(" + id + ")");
        return database.supprimer(id);
    }
}