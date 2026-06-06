package server;

import common.Produit;
import java.util.ArrayList;

public class Database {

    private ArrayList<Produit> produits = new ArrayList<>();
    private int compteur = 1;

    public synchronized String ajouter(String nom, double prix) {
        if (nom == null || nom.trim().isEmpty()) {
            return "ERREUR:Le nom du produit est obligatoire";
        }
        if (prix < 0) {
            return "ERREUR:Le prix ne peut pas être négatif";
        }
        produits.add(new Produit(compteur++, nom.trim(), prix));
        return "OK:Produit ajouté avec succès";
    }

    public synchronized String listerTous() {
        if (produits.isEmpty()) return "LISTE:vide";
        StringBuilder sb = new StringBuilder("LISTE:");
        for (Produit p : produits) {
            sb.append(p.toString()).append(";");
        }
        return sb.toString();
    }

    public synchronized String supprimer(int id) {
        boolean trouve = produits.removeIf(p -> p.getId() == id);
        return trouve ? "OK:Produit supprimé" : "ERREUR:Produit introuvable";
    }
}