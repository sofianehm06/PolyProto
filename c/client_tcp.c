#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>

#pragma comment(lib, "ws2_32.lib")

#define PORT 6000
#define HOST "127.0.0.1"
#define BUFFER_SIZE 1024

int main() {
    WSADATA wsa;
    SOCKET sock;
    struct sockaddr_in server_addr;
    char buffer[BUFFER_SIZE];
    char message[BUFFER_SIZE];

    // Initialiser Winsock
    WSAStartup(MAKEWORD(2, 2), &wsa);

    // Creer le socket
    sock = socket(AF_INET, SOCK_STREAM, 0);

    // Configurer l'adresse du serveur
    server_addr.sin_family = AF_INET;
    server_addr.sin_port   = htons(PORT);
    server_addr.sin_addr.s_addr = inet_addr(HOST);

    // Connexion au serveur
    if (connect(sock, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        printf("Erreur : impossible de se connecter au serveur !\n");
        return 1;
    }

    printf("=== Client TCP (C) connecte au serveur ===\n");
    printf("Commandes disponibles :\n");
    printf("  AJOUTER:nom:prix\n");
    printf("  LISTER\n");
    printf("  SUPPRIMER:id\n");
    printf("  quitter\n");
    printf("==========================================\n\n");

    while (1) {
        printf("> ");
        fgets(message, BUFFER_SIZE, stdin);
        message[strcspn(message, "\n")] = 0;

        if (strcmp(message, "quitter") == 0) {
            printf("Au revoir !\n");
            break;
        }

        // Envoyer au serveur
        strcat(message, "\n");
        send(sock, message, strlen(message), 0);

        // Recevoir la reponse
        memset(buffer, 0, BUFFER_SIZE);
        recv(sock, buffer, BUFFER_SIZE - 1, 0);
        printf("Reponse : %s\n", buffer);
    }

    closesocket(sock);
    WSACleanup();
    return 0;
}