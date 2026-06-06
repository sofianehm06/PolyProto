package client;

import java.net.*;
import java.io.*;

public class Client {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String envoyer(String message) {
        try {
            out.println(message);
            return in.readLine();
        } catch (IOException e) {
            return "ERREUR:Connexion perdue";
        }
    }

    public void fermer() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Erreur fermeture : " + e.getMessage());
        }
    }
}