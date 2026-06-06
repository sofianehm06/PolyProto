package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRMI {

    private static final int PORT = 5003;

    public static void main(String[] args) {
        System.out.println("=== Serveur RMI démarré sur le port " + PORT + " ===");

        try {
            Database database = new Database();
            ProduitService service = new ProduitServiceImpl(database);

            // Créer le registry RMI (l'annuaire)
            Registry registry = LocateRegistry.createRegistry(PORT);

            // Enregistrer le service sous le nom "ProduitService"
            registry.rebind("ProduitService", service);

            System.out.println("Service enregistré sous le nom : ProduitService");
            System.out.println("En attente d'appels clients...");

        } catch (Exception e) {
            System.out.println("Erreur serveur RMI : " + e.getMessage());
        }
    }
}