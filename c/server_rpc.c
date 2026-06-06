#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>

#pragma comment(lib, "ws2_32.lib")

#define PORT 6003
#define BUFFER_SIZE 1024
#define MAX_PRODUITS 100

typedef struct {
    int id;
    char nom[50];
    double prix;
} Produit;

Produit produits[MAX_PRODUITS];
int nb_produits = 0;
int compteur_id = 1;

// ===== Fonctions distantes (simulees via RPC) =====

void rpc_ajouter(char *nom, double prix, char *reponse) {
    printf("[RPC] Appel : ajouter(%s, %.2f)\n", nom, prix);
    if (nb_produits >= MAX_PRODUITS) {
        strcpy(reponse, "ERREUR:Tableau plein");
        return;
    }
    produits[nb_produits].id = compteur_id++;
    strcpy(produits[nb_produits].nom, nom);
    produits[nb_produits].prix = prix;
    nb_produits++;
    strcpy(reponse, "OK:Produit ajoute");
}

void rpc_lister(char *reponse, int taille_max) {
    printf("[RPC] Appel : lister()\n");
    if (nb_produits == 0) {
        snprintf(reponse, taille_max, "LISTE:vide");
        return;
    }
    // snprintf borne chaque écriture : impossible de déborder le buffer.
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

void rpc_supprimer(int id, char *reponse) {
    printf("[RPC] Appel : supprimer(%d)\n", id);
    for (int i = 0; i < nb_produits; i++) {
        if (produits[i].id == id) {
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

// ===== Dispatcher RPC =====
// C'est lui qui recoit l'appel reseau et appelle la bonne fonction

void dispatcher(char *message, char *reponse) {
    char arg1[50], arg2[20];

    if (strncmp(message, "ajouter:", 8) == 0) {
        if (sscanf(message, "ajouter:%49[^:]:%19s", arg1, arg2) != 2) {
            strcpy(reponse, "ERREUR:Format attendu ajouter:nom:prix");
        } else {
            char *fin;
            double prix = strtod(arg2, &fin);
            if (*fin != '\0') {
                strcpy(reponse, "ERREUR:Prix invalide");
            } else {
                rpc_ajouter(arg1, prix, reponse);
            }
        }

    } else if (strcmp(message, "lister") == 0) {
        rpc_lister(reponse, BUFFER_SIZE);

    } else if (strncmp(message, "supprimer:", 10) == 0) {
        sscanf(message, "supprimer:%19s", arg1);
        rpc_supprimer(atoi(arg1), reponse);

    } else {
        strcpy(reponse, "ERREUR:Fonction inconnue");
    }
}

void gerer_client(SOCKET client_socket) {
    char buffer[BUFFER_SIZE];
    char reponse[BUFFER_SIZE];

    while (1) {
        memset(buffer, 0, BUFFER_SIZE);
        int bytes = recv(client_socket, buffer, BUFFER_SIZE - 1, 0);

        if (bytes <= 0) {
            printf("[RPC] Client deconnecte.\n");
            break;
        }

        buffer[strcspn(buffer, "\r\n")] = 0;
        printf("[RPC] Requete recue : %s\n", buffer);

        memset(reponse, 0, BUFFER_SIZE);
        dispatcher(buffer, reponse);

        printf("[RPC] Reponse : %s\n\n", reponse);
        strcat(reponse, "\n");
        send(client_socket, reponse, strlen(reponse), 0);
    }

    closesocket(client_socket);
}

int main() {
    WSADATA wsa;
    SOCKET server_socket, client_socket;
    struct sockaddr_in server_addr, client_addr;
    int client_len = sizeof(client_addr);

    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
        printf("Erreur : initialisation de Winsock impossible.\n");
        return 1;
    }

    server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket == INVALID_SOCKET) {
        printf("Erreur : creation du socket impossible (code %d).\n", WSAGetLastError());
        WSACleanup();
        return 1;
    }

    server_addr.sin_family      = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port        = htons(PORT);

    if (bind(server_socket, (struct sockaddr*)&server_addr, sizeof(server_addr)) == SOCKET_ERROR) {
        printf("Erreur : bind impossible sur le port %d (deja utilise ? code %d).\n",
               PORT, WSAGetLastError());
        closesocket(server_socket);
        WSACleanup();
        return 1;
    }
    listen(server_socket, 5);

    printf("=== Serveur RPC (C) demarre sur le port %d ===\n", PORT);
    printf("Fonctions disponibles : ajouter, lister, supprimer\n\n");

    while (1) {
        client_socket = accept(server_socket,
                               (struct sockaddr*)&client_addr,
                               &client_len);
        printf("[RPC] Nouveau client connecte.\n");
        gerer_client(client_socket);
    }

    closesocket(server_socket);
    WSACleanup();
    return 0;
}