package client;

import java.net.*;

public class ClientMulticast implements Runnable {

    private static final String GROUPE_MULTICAST = "230.0.0.1";
    private static final int PORT = 5002;

    private JTextAreaCallback callback;

    public interface JTextAreaCallback {
        void log(String message);
    }

    public ClientMulticast(JTextAreaCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(PORT)) {

            InetAddress groupe = InetAddress.getByName(GROUPE_MULTICAST);
            socket.joinGroup(groupe);
            callback.log("[Multicast] Rejoint le groupe " + GROUPE_MULTICAST);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquet);
                String message = new String(paquet.getData(), 0, paquet.getLength());
                callback.log("[Multicast reçu] " + message);
            }

        } catch (Exception e) {
            callback.log("[Multicast] Erreur : " + e.getMessage());
        }
    }
}