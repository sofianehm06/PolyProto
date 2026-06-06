package client;

import server.ProduitService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRMI {

    private ProduitService service;

    public ClientRMI(String host) throws Exception {
        // Chercher l'annuaire RMI sur le serveur
        Registry registry = LocateRegistry.getRegistry(host, 5003);

        // Récupérer l'objet distant par son nom
        service = (ProduitService) registry.lookup("ProduitService");

        System.out.println("Service RMI trouvé ✓");
    }

    public String ajouter(String nom, double prix) {
        try {
            return service.ajouter(nom, prix);
        } catch (Exception e) {
            return "ERREUR:" + e.getMessage();
        }
    }

    public String listerTous() {
        try {
            return service.listerTous();
        } catch (Exception e) {
            return "ERREUR:" + e.getMessage();
        }
    }

    public String supprimer(int id) {
        try {
            return service.supprimer(id);
        } catch (Exception e) {
            return "ERREUR:" + e.getMessage();
        }
    }
}