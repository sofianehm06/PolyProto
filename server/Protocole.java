package server;

/**
 * Décodage du protocole texte partagé par tous les serveurs de messages
 * (TCP, UDP, JMS). Centraliser ce code en un seul endroit évite la
 * duplication et garantit que les trois transports interprètent les
 * requêtes exactement de la même façon.
 *
 * <p>Format des requêtes (une ligne de texte) :</p>
 * <pre>
 *   AJOUTER:nom:prix     ex. AJOUTER:Clavier:1500
 *   LISTER
 *   SUPPRIMER:id         ex. SUPPRIMER:3
 * </pre>
 *
 * <p>Format des réponses :</p>
 * <pre>
 *   OK:message
 *   ERREUR:message
 *   LISTE:1 | Clavier | 1500.0 DA;2 | Souris | 800.0 DA;
 * </pre>
 */
public final class Protocole {

    /** Classe utilitaire : pas d'instanciation. */
    private Protocole() {}

    /**
     * Décode une requête texte, exécute l'opération correspondante sur la
     * base et renvoie la réponse à transmettre au client. Toute requête
     * mal formée (argument manquant, prix non numérique, commande
     * inconnue) renvoie une réponse {@code ERREUR:...} au lieu de lever
     * une exception : un client fautif ne doit jamais faire tomber le
     * serveur.
     *
     * @param db       base de données partagée (thread-safe)
     * @param requete  ligne reçue du client
     * @return la réponse à renvoyer (jamais {@code null})
     */
    public static String traiter(Database db, String requete) {
        if (requete == null || requete.trim().isEmpty()) {
            return "ERREUR:Requête vide";
        }

        String[] parts = requete.split(":");
        String commande = parts[0].trim().toUpperCase();

        try {
            switch (commande) {
                case "AJOUTER":   // AJOUTER:nom:prix
                    return db.ajouter(parts[1].trim(), Double.parseDouble(parts[2].trim()));
                case "LISTER":
                    return db.listerTous();
                case "SUPPRIMER": // SUPPRIMER:id
                    return db.supprimer(Integer.parseInt(parts[1].trim()));
                default:
                    return "ERREUR:Commande inconnue (" + commande + ")";
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return "ERREUR:Requête mal formée — argument(s) manquant(s)";
        } catch (NumberFormatException e) {
            return "ERREUR:Valeur numérique invalide";
        }
    }
}
