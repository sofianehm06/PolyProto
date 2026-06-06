#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>

#pragma comment(lib, "ws2_32.lib")

#define PORT 6000
#define BUFFER_SIZE 1024
#define MAX_PRODUITS 100

// Structure produit
typedef struct {
    int id;
    char nom[50];
    double prix;
} Produit;

// "Base de donnees" — tableau de produits
Produit produits[MAX_PRODUITS];
int nb_produits = 0;
int compteur_id = 1;

// ===== Fonctions base de donnees =====

void ajouter(char *nom, double prix, char *reponse) {
    if (nb_produits >= MAX_PRODUITS) {
        strcpy(reponse, "ERREUR:Tableau plein");
        return;
    }
    produits[nb_produits].id   = compteur_id++;
    strcpy(produits[nb_produits].nom, nom);
    produits[nb_produits].prix = prix;
    nb_produits++;
    strcpy(reponse, "OK:Produit ajoute avec succes");
}

void lister(char *reponse, int taille_max) {
    if (nb_produits == 0) {
        snprintf(reponse, taille_max, "LISTE:vide");
        return;
    }
    // snprintf borne chaque écriture : impossible de déborder le buffer
    // même si le catalogue dépasse la capacité de la réponse.
    int pos = snprintf(reponse, taille_max, "LISTE:");
    for (int i = 0; i < nb_produits; i++) {
        if (pos >= taille_max) break;  // plus de place disponible
        pos += snprintf(reponse + pos, taille_max - pos,
                        "%d | %s | %.2f DA;",
                        produits[i].id,
                        produits[i].nom,
                        produits[i].prix);
    }
}

void supprimer(int id, char *reponse) {
    for (int i = 0; i < nb_produits; i++) {
        if (produits[i].id == id) {
            // Decaler les elements
            for (int j = i; j < nb_produits - 1; j++) {
                produits[j] = produits[j + 1];
            }
            nb_produits--;
            strcpy(reponse, "OK:Produit supprime");
            return;
        }
    }
    strcpy(reponse, "ERREUR:Produit introuvable");
}

// ===== Traitement requete =====

void traiter_requete(char *message, char *reponse) {
    printf("[Client] -> %s\n", message);

    char arg1[50], arg2[20];

    if (strncmp(message, "AJOUTER:", 8) == 0) {
        // Format : AJOUTER:nom:prix
        if (sscanf(message, "AJOUTER:%49[^:]:%19s", arg1, arg2) != 2) {
            strcpy(reponse, "ERREUR:Format attendu AJOUTER:nom:prix");
        } else {
            char *fin;
            double prix = strtod(arg2, &fin);
            if (*fin != '\0') {
                strcpy(reponse, "ERREUR:Prix invalide");
            } else {
                ajouter(arg1, prix, reponse);
            }
        }

    } else if (strncmp(message, "LISTER", 6) == 0) {
        lister(reponse, BUFFER_SIZE);

    } else if (strncmp(message, "SUPPRIMER:", 10) == 0) {
        // Format : SUPPRIMER:id
        sscanf(message, "SUPPRIMER:%19s", arg1);
        int id = atoi(arg1);
        supprimer(id, reponse);

    } else {
        strcpy(reponse, "ERREUR:Commande inconnue");
    }

    printf("[Serveur] -> %s\n", reponse);
}

// ===== Gestion client =====

void gerer_client(SOCKET client_socket) {
    char buffer[BUFFER_SIZE];
    char reponse[BUFFER_SIZE];

    while (1) {
        memset(buffer, 0, BUFFER_SIZE);
        int bytes = recv(client_socket, buffer, BUFFER_SIZE - 1, 0);
        
        if (bytes <= 0) {
            printf("Client deconnecte.\n");
            break;
        }

        // Supprimer \n et \r
        buffer[strcspn(buffer, "\r\n")] = 0;

        memset(reponse, 0, BUFFER_SIZE);
        traiter_requete(buffer, reponse);

        strcat(reponse, "\n");
        send(client_socket, reponse, strlen(reponse), 0);
    }

    closesocket(client_socket);
}

// ===== Main =====

int main() {
    WSADATA wsa;
    SOCKET server_socket, client_socket;
    struct sockaddr_in server_addr, client_addr;
    int client_len = sizeof(client_addr);

    // Initialiser Winsock
    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
        printf("Erreur : initialisation de Winsock impossible.\n");
        return 1;
    }

    // Creer le socket
    server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket == INVALID_SOCKET) {
        printf("Erreur : creation du socket impossible (code %d).\n", WSAGetLastError());
        WSACleanup();
        return 1;
    }

    // Configurer l'adresse
    server_addr.sin_family      = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port        = htons(PORT);

    // Bind
    if (bind(server_socket, (struct sockaddr*)&server_addr, sizeof(server_addr)) == SOCKET_ERROR) {
        printf("Erreur : bind impossible sur le port %d (deja utilise ? code %d).\n",
               PORT, WSAGetLastError());
        closesocket(server_socket);
        WSACleanup();
        return 1;
    }

    // Listen
    listen(server_socket, 5);

    printf("=== Serveur TCP (C) demarre sur le port %d ===\n", PORT);
    printf("En attente de clients...\n");

    while (1) {
        client_socket = accept(server_socket, 
                               (struct sockaddr*)&client_addr, 
                               &client_len);
        printf("Nouveau client connecte !\n");

        // Gerer le client (sans thread pour simplifier)
        gerer_client(client_socket);
    }

    closesocket(server_socket);
    WSACleanup();
    return 0;
}