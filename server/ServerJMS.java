package server;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ServerJMS {

    private static final String URL   = "tcp://localhost:61616";
    private static final String FILE  = "ProduitQueue";
    private static Database database  = new Database();

    public static void main(String[] args) {
        System.out.println("=== Serveur JMS démarré ===");
        System.out.println("En attente de messages sur la file : " + FILE);

        try {
            // 1. Connexion au broker ActiveMQ
            ConnectionFactory factory = new ActiveMQConnectionFactory(URL);
            Connection connection = factory.createConnection();
            connection.start();

            // 2. Créer une session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 3. Se connecter à la file d'attente
            Destination destination = session.createQueue(FILE);

            // 4. Créer un consommateur
            MessageConsumer consumer = session.createConsumer(destination);

            System.out.println("Serveur JMS prêt ✓");

            // 5. Écouter les messages
            while (true) {
                Message message = consumer.receive();

                if (message instanceof TextMessage) {
                    String texte = ((TextMessage) message).getText();
                    System.out.println("[JMS reçu] → " + texte);

                    String reponse = Protocole.traiter(database, texte);
                    System.out.println("[JMS réponse] → " + reponse);
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur JMS : " + e.getMessage());
        }
    }
}