package server;

import java.net.*;

public class ServerUDP {

    private static final int PORT = 5001;
    private static Database database = new Database();

    public static void main(String[] args) {
        System.out.println("=== Serveur UDP démarré sur le port " + PORT + " ===");

        try (DatagramSocket socket = new DatagramSocket(PORT)) {

            byte[] buffer = new byte[1024];

            while (true) {
                // 1. Attendre un paquet entrant
                DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquet);

                // 2. Lire le message
                String message = new String(paquet.getData(), 0, paquet.getLength());
                System.out.println("[Client] " + paquet.getAddress() + " → " + message);

                // 3. Traiter la requête
                String reponse = Protocole.traiter(database, message);
                System.out.println("[Serveur] → " + reponse);

                // 4. Renvoyer la réponse au même client
                byte[] reponseBytes = reponse.getBytes();
                DatagramPacket paquetReponse = new DatagramPacket(
                    reponseBytes,
                    reponseBytes.length,
                    paquet.getAddress(),
                    paquet.getPort()
                );
                socket.send(paquetReponse);
            }

        } catch (Exception e) {
            System.out.println("Erreur serveur UDP : " + e.getMessage());
        }
    }
}