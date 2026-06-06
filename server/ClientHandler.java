package server;

import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Database database;

    public ClientHandler(Socket socket, Database database) {
        this.socket = socket;
        this.database = database;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true)
        ) {

            String message;
            while ((message = in.readLine()) != null) {

                System.out.println("[Client] " + socket.getInetAddress() + " → " + message);
                String reponse = Protocole.traiter(database, message);
                out.println(reponse);
                System.out.println("[Serveur] → " + reponse);
            }

        } catch (IOException e) {
            System.out.println("Client déconnecté : " + socket.getInetAddress());
        }
    }
}