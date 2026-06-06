package server;

import java.net.*;
import java.io.*;

public class Server {

    private static final int PORT = 5000;
    private static Database database = new Database();

    public static void main(String[] args) {
        System.out.println("=== Serveur TCP démarré sur le port " + PORT + " ===");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + clientSocket.getInetAddress());

                Thread t = new Thread(new ClientHandler(clientSocket, database));
                t.start();
            }

        } catch (IOException e) {
            System.out.println("Erreur serveur : " + e.getMessage());
        }
    }
}