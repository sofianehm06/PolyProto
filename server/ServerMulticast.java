package server;

import java.net.*;

public class ServerMulticast {

    private static final String GROUPE_MULTICAST = "230.0.0.1";
    private static final int PORT = 5002;

    public static void main(String[] args) {
        System.out.println("=== Serveur Multicast démarré ===");
        System.out.println("Groupe : " + GROUPE_MULTICAST + ":" + PORT);

        try (DatagramSocket socket = new DatagramSocket()) {

            int compteur = 1;

            while (true) {
                // Attendre 5 secondes puis envoyer un message à tout le groupe
                Thread.sleep(5000);

                String message = "NOTIFICATION: Produit #" + compteur + " ajouté sur le serveur !";
                byte[] messageBytes = message.getBytes();

                InetAddress groupe = InetAddress.getByName(GROUPE_MULTICAST);
                DatagramPacket paquet = new DatagramPacket(
                    messageBytes,
                    messageBytes.length,
                    groupe,
                    PORT
                );

                socket.send(paquet);
                System.out.println("[Multicast envoyé] → " + message);
                compteur++;
            }

        } catch (Exception e) {
            System.out.println("Erreur multicast : " + e.getMessage());
        }
    }
}