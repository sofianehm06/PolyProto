package client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ClientUI extends JFrame {

    private Client client;
    private ClientUDP clientUDP;
    private ClientRMI clientRMI;
    private ClientJMS clientJMS;
    private ClientMulticast clientMulticast;
    private String protocole;

    // Couleurs
    private final Color BG_MAIN    = new Color(245, 247, 250);
    private final Color BG_CARD    = Color.WHITE;
    private final Color ACCENT     = new Color(67, 130, 238);
    private final Color ACCENT2    = new Color(76, 201, 160);
    private final Color DANGER     = new Color(239, 68, 68);
    private final Color TEXT_MAIN  = new Color(30, 30, 60);
    private final Color TEXT_MUTED = new Color(120, 130, 160);
    private final Color BORDER     = new Color(220, 225, 235);

    // Composants
    private JTextField champNom  = new JTextField();
    private JTextField champPrix = new JTextField();
    private JTextField champId   = new JTextField();
    private JTextArea  zoneLog   = new JTextArea();
    private JLabel     labelStatus;

    public ClientUI() {
        super("PolyProto");

        protocole = choisirProtocole();
        if (protocole == null) System.exit(0);

        try {
            if (protocole.equals("TCP")) {
                client = new Client("localhost", 5000);
                log("Connecte au serveur TCP");
            } else if (protocole.equals("UDP")) {
                clientUDP = new ClientUDP("localhost");
                log("Pret a envoyer en UDP");
            } else if (protocole.equals("RMI")) {
                clientRMI = new ClientRMI("localhost");
                log("Connecte au serveur RMI");
            } else if (protocole.equals("JMS")) {
                clientJMS = new ClientJMS();
                log("Connecte au broker JMS");
            }
        } catch (Exception e) {
            log("Erreur de connexion : " + e.getMessage());
        }

        clientMulticast = new ClientMulticast(msg ->
            SwingUtilities.invokeLater(() -> log("[Multicast] " + msg)));
        new Thread(clientMulticast).start();

        construireUI();
    }

    private String choisirProtocole() {
        String[] options = {"TCP", "UDP", "RMI", "JMS"};

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(20, 28, 20, 28));

        JLabel titre = new JLabel("Choisir le protocole");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titre.setForeground(TEXT_MAIN);

        JLabel sous = new JLabel("Selectionnez le mode de communication");
        sous.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sous.setForeground(TEXT_MUTED);

        JPanel titrePanel = new JPanel(new BorderLayout(0, 6));
        titrePanel.setBackground(BG_CARD);
        titrePanel.add(titre, BorderLayout.NORTH);
        titrePanel.add(sous, BorderLayout.CENTER);

        JComboBox<String> combo = new JComboBox<>(options);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(BG_CARD);
        combo.setForeground(TEXT_MAIN);
        combo.setPreferredSize(new Dimension(200, 36));

        panel.add(titrePanel, BorderLayout.NORTH);
        panel.add(combo, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(
            null, panel, "Connexion",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            return (String) combo.getSelectedItem();
        }
        return null;
    }

    private void construireUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 660);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
        setLayout(new BorderLayout(0, 0));

        add(creerHeader(), BorderLayout.NORTH);
        add(creerContenu(), BorderLayout.CENTER);
        add(creerFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel creerHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_CARD);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(14, 24, 14, 24)
        ));

        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setBackground(BG_CARD);

        JLabel titre = new JLabel("Gestion Produits");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titre.setForeground(TEXT_MAIN);

        JLabel badge = new JLabel("  " + protocole + "  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setBackground(ACCENT);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        badgePanel.setBackground(BG_CARD);
        badgePanel.add(badge);

        left.add(titre, BorderLayout.NORTH);
        left.add(badgePanel, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(BG_CARD);

        labelStatus = new JLabel("  Connecte");
        labelStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelStatus.setForeground(ACCENT2);
        right.add(labelStatus);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel creerContenu() {
        JPanel contenu = new JPanel(new GridBagLayout());
        contenu.setBackground(BG_MAIN);
        contenu.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.BOTH;
        gbc.weightx   = 1;
        gbc.gridx     = 0;

        gbc.gridy   = 0;
        gbc.weighty = 0;
        gbc.insets  = new Insets(0, 0, 16, 0);
        contenu.add(creerCardFormulaire(), gbc);

        gbc.gridy   = 1;
        gbc.weighty = 1;
        gbc.insets  = new Insets(0, 0, 0, 0);
        contenu.add(creerCardLog(), gbc);

        return contenu;
    }

    private JPanel creerCardFormulaire() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(18, 22, 18, 22)
        ));

        JLabel titre = new JLabel("Nouveau produit");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titre.setForeground(TEXT_MAIN);
        card.add(titre, BorderLayout.NORTH);

        // Les 3 champs
        JPanel champs = new JPanel(new GridLayout(1, 3, 14, 0));
        champs.setBackground(BG_CARD);
        champs.add(creerChamp("Nom du produit", champNom));
        champs.add(creerChamp("Prix (DA)", champPrix));
        champs.add(creerChamp("ID (supprimer)", champId));
        card.add(champs, BorderLayout.CENTER);

        // Boutons
        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        boutons.setBackground(BG_CARD);

        JButton btnSupprimer = creerBouton("Supprimer", DANGER, false);
        JButton btnLister    = creerBouton("Lister tous", TEXT_MUTED, true);
        JButton btnAjouter   = creerBouton("Ajouter", ACCENT, false);

        btnAjouter.addActionListener(e -> actionAjouter());
        btnLister.addActionListener(e -> actionLister());
        btnSupprimer.addActionListener(e -> actionSupprimer());

        boutons.add(btnSupprimer);
        boutons.add(btnLister);
        boutons.add(btnAjouter);

        card.add(boutons, BorderLayout.SOUTH);

        return card;
    }

    private JPanel creerChamp(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(BG_CARD);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(TEXT_MAIN);
        field.setBackground(BG_MAIN);
        field.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(0, 38));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                    new LineBorder(ACCENT, 1, true),
                    new EmptyBorder(8, 12, 8, 12)
                ));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(new CompoundBorder(
                    new LineBorder(BORDER, 1, true),
                    new EmptyBorder(8, 12, 8, 12)
                ));
            }
        });

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel creerCardLog() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(18, 22, 18, 22)
        ));

        JPanel headerLog = new JPanel(new BorderLayout());
        headerLog.setBackground(BG_CARD);

        JLabel titre = new JLabel("Activite");
        titre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titre.setForeground(TEXT_MAIN);

        JButton btnEffacer = new JButton("Effacer");
        btnEffacer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnEffacer.setForeground(TEXT_MUTED);
        btnEffacer.setBackground(BG_MAIN);
        btnEffacer.setBorder(new EmptyBorder(4, 10, 4, 10));
        btnEffacer.setFocusPainted(false);
        btnEffacer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEffacer.addActionListener(e -> zoneLog.setText(""));

        headerLog.add(titre, BorderLayout.WEST);
        headerLog.add(btnEffacer, BorderLayout.EAST);

        zoneLog.setEditable(false);
        zoneLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        zoneLog.setForeground(TEXT_MAIN);
        zoneLog.setBackground(BG_MAIN);
        zoneLog.setBorder(new EmptyBorder(12, 12, 12, 12));
        zoneLog.setLineWrap(true);

        JScrollPane scroll = new JScrollPane(zoneLog);
        scroll.setBorder(new LineBorder(BORDER, 1, true));
        scroll.getViewport().setBackground(BG_MAIN);

        card.add(headerLog, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JButton creerBouton(String texte, Color couleur, boolean outline) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 36));

        if (outline) {
            btn.setForeground(TEXT_MUTED);
            btn.setBackground(BG_CARD);
            btn.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)
            ));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setBackground(BG_MAIN); }
                public void mouseExited(MouseEvent e)  { btn.setBackground(BG_CARD); }
            });
        } else {
            btn.setForeground(Color.WHITE);
            btn.setBackground(couleur);
            btn.setBorder(new CompoundBorder(
                new LineBorder(couleur, 1, true),
                new EmptyBorder(6, 12, 6, 12)
            ));
            Color hover = couleur.darker();
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
                public void mouseExited(MouseEvent e)  { btn.setBackground(couleur); }
            });
        }

        return btn;
    }

    private JPanel creerFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_CARD);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER));

        JLabel lbl = new JLabel("PolyProto  —  TCP  UDP  Multicast  RMI  JMS");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT_MUTED);
        footer.add(lbl);

        return footer;
    }

    // ===== ACTIONS =====

    private void actionAjouter() {
        String nom  = champNom.getText().trim();
        String prix = champPrix.getText().trim();
        if (nom.isEmpty() || prix.isEmpty()) {
            log("[!] Remplis le nom et le prix !");
            return;
        }
        try {
            if (Double.parseDouble(prix) < 0) {
                log("[!] Le prix ne peut pas être négatif.");
                return;
            }
        } catch (NumberFormatException e) {
            log("[!] Le prix doit être un nombre (ex. 1500 ou 99.90).");
            return;
        }
        String reponse = envoyer("AJOUTER:" + nom + ":" + prix);
        log("[AJOUTER] " + nom + " — " + prix + " DA  =>  " + reponse);
        champNom.setText("");
        champPrix.setText("");
    }

    private void actionLister() {
        String reponse = envoyer("LISTER");
        if (reponse == null || reponse.startsWith("LISTE:vide")) {
            log("[LISTER] Aucun produit.");
        } else {
            String[] produits = reponse.replace("LISTE:", "").split(";");
            log("[LISTER] " + produits.length + " produit(s) :");
            for (String p : produits) {
                if (!p.isEmpty()) log("   -> " + p);
            }
        }
    }

    private void actionSupprimer() {
        String id = champId.getText().trim();
        if (id.isEmpty()) {
            log("[!] Entre un ID a supprimer !");
            return;
        }
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            log("[!] L'ID doit être un nombre entier.");
            return;
        }
        String reponse = envoyer("SUPPRIMER:" + id);
        log("[SUPPRIMER] ID:" + id + "  =>  " + reponse);
        champId.setText("");
    }

    private String envoyer(String message) {
        if (protocole.equals("TCP")) {
            return client.envoyer(message);
        } else if (protocole.equals("UDP")) {
            return clientUDP.envoyer(message);
        } else if (protocole.equals("JMS")) {
            return clientJMS.envoyer(message);
        } else {
            String[] parts = message.split(":");
            switch (parts[0]) {
                case "AJOUTER":
                    return clientRMI.ajouter(parts[1], Double.parseDouble(parts[2]));
                case "LISTER":
                    return clientRMI.listerTous();
                case "SUPPRIMER":
                    return clientRMI.supprimer(Integer.parseInt(parts[1]));
                default:
                    return "ERREUR:Commande inconnue";
            }
        }
    }

    private void log(String msg) {
        zoneLog.append(msg + "\n");
        zoneLog.setCaretPosition(zoneLog.getDocument().getLength());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        SwingUtilities.invokeLater(ClientUI::new);
    }
}