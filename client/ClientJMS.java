package client;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ClientJMS {

    private static final String URL  = "tcp://localhost:61616";
    private static final String FILE = "ProduitQueue";

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    public ClientJMS() throws Exception {
        // 1. Connexion au broker
        ConnectionFactory factory = new ActiveMQConnectionFactory(URL);
        connection = factory.createConnection();
        connection.start();

        // 2. Créer une session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 3. Se connecter à la file
        Destination destination = session.createQueue(FILE);

        // 4. Créer un producteur
        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }

    public String envoyer(String message) {
        try {
            TextMessage textMessage = session.createTextMessage(message);
            producer.send(textMessage);
            return "OK:Message envoyé dans la file JMS";
        } catch (Exception e) {
            return "ERREUR:" + e.getMessage();
        }
    }

    public void fermer() {
        try {
            connection.close();
        } catch (Exception e) {
            System.out.println("Erreur fermeture JMS : " + e.getMessage());
        }
    }
}