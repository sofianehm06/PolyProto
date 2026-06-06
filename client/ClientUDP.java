package client;

import java.net.*;

public class ClientUDP {

    private DatagramSocket socket;
    private InetAddress adresse;
    private static final int PORT = 5001;

    public ClientUDP(String host) throws Exception {
        socket  = new DatagramSocket();
        adresse = InetAddress.getByName(host);
    }

    public String envoyer(String message) {
        try {
            // 1. Envoyer le message
            byte[] messageBytes = message.getBytes();
            DatagramPacket paquet = new DatagramPacket(
                messageBytes,
                messageBytes.length,
                adresse,
                PORT
            );
            socket.send(paquet);

            // 2. Attendre la réponse
            byte[] buffer = new byte[1024];
            DatagramPacket reponse = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(3000); // timeout 3 secondes
            socket.receive(reponse);

            return new String(reponse.getData(), 0, reponse.getLength());

        } catch (SocketTimeoutException e) {
            return "ERREUR:Pas de réponse du serveur (timeout)";
        } catch (Exception e) {
            return "ERREUR:" + e.getMessage();
        }
    }

    public void fermer() {
        socket.close();
    }
}