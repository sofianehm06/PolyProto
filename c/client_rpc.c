#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>

#pragma comment(lib, "ws2_32.lib")

#define PORT 6003
#define HOST "127.0.0.1"
#define BUFFER_SIZE 1024

SOCKET sock;
char buffer[BUFFER_SIZE];

// ===== Stubs RPC =====
// Ce sont les fonctions locales qui cachent l'appel reseau
// Le client appelle ces fonctions comme si elles etaient locales

void appeler_serveur(char *message) {
    char msg[BUFFER_SIZE];
    strcpy(msg, message);
    strcat(msg, "\n");
    send(sock, msg, strlen(msg), 0);

    memset(buffer, 0, BUFFER_SIZE);
    recv(sock, buffer, BUFFER_SIZE - 1, 0);
    buffer[strcspn(buffer, "\r\n")] = 0;
}

// Stub : ajouter
void stub_ajouter(char *nom, double prix) {
    char message[BUFFER_SIZE];
    sprintf(message, "ajouter:%s:%.2f", nom, prix);
    appeler_serveur(message);
    printf("Resultat : %s\n", buffer);
}

// Stub : lister
void stub_lister() {
    appeler_serveur("lister");
    if (strcmp(buffer, "LISTE:vide") == 0) {
        printf("Resultat : Aucun produit.\n");
    } else {
        char *liste = buffer + 6; // Sauter "LISTE:"
        char *token = strtok(liste, ";");
        printf("Resultat :\n");
        while (token != NULL) {
            printf("   -> %s\n", token);
            token = strtok(NULL, ";");
        }
    }
}

// Stub : supprimer
void stub_supprimer(int id) {
    char message[BUFFER_SIZE];
    sprintf(message, "supprimer:%d", id);
    appeler_serveur(message);
    printf("Resultat : %s\n", buffer);
}

// ===== Menu =====

void afficher_menu() {
    printf("\n=== Client RPC (C) ===\n");
    printf("1. Ajouter un produit\n");
    printf("2. Lister tous\n");
    printf("3. Supprimer un produit\n");
    printf("4. Quitter\n");
    printf("Choix : ");
}

int main() {
    WSADATA wsa;
    struct sockaddr_in server_addr;

    WSAStartup(MAKEWORD(2, 2), &wsa);

    sock = socket(AF_INET, SOCK_STREAM, 0);

    server_addr.sin_family      = AF_INET;
    server_addr.sin_port        = htons(PORT);
    server_addr.sin_addr.s_addr = inet_addr(HOST);

    if (connect(sock, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        printf("Erreur : impossible de se connecter !\n");
        return 1;
    }

    printf("Connecte au serveur RPC !\n");

    int choix;
    char nom[50];
    double prix;
    int id;

    while (1) {
        afficher_menu();
        scanf("%d", &choix);
        getchar();

        switch (choix) {
            case 1:
                printf("Nom du produit : ");
                fgets(nom, 50, stdin);
                nom[strcspn(nom, "\n")] = 0;
                printf("Prix : ");
                scanf("%lf", &prix);
                getchar();
                stub_ajouter(nom, prix);
                break;

            case 2:
                stub_lister();
                break;

            case 3:
                printf("ID a supprimer : ");
                scanf("%d", &id);
                getchar();
                stub_supprimer(id);
                break;

            case 4:
                printf("Au revoir !\n");
                closesocket(sock);
                WSACleanup();
                return 0;

            default:
                printf("Choix invalide !\n");
        }
    }
}