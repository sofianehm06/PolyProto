#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>

#pragma comment(lib, "ws2_32.lib")

#define PORT 6001
#define HOST "127.0.0.1"
#define BUFFER_SIZE 1024

int main() {
    WSADATA wsa;
    SOCKET sock;
    struct sockaddr_in server_addr;
    int server_len = sizeof(server_addr);
    char buffer[BUFFER_SIZE];
    char message[BUFFER_SIZE];

    WSAStartup(MAKEWORD(2, 2), &wsa);

    // SOCK_DGRAM = UDP
    sock = socket(AF_INET, SOCK_DGRAM, 0);

    server_addr.sin_family      = AF_INET;
    server_addr.sin_port        = htons(PORT);
    server_addr.sin_addr.s_addr = inet_addr(HOST);

    // Timeout 3 secondes
    int timeout = 3000;
    setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO,
               (char*)&timeout, sizeof(timeout));

    printf("=== Client UDP (C) ===\n");
    printf("Commandes disponibles :\n");
    printf("  AJOUTER:nom:prix\n");
    printf("  LISTER\n");
    printf("  SUPPRIMER:id\n");
    printf("  quitter\n");
    printf("======================\n\n");

    while (1) {
        printf("> ");
        fgets(message, BUFFER_SIZE, stdin);
        message[strcspn(message, "\n")] = 0;

        if (strcmp(message, "quitter") == 0) {
            printf("Au revoir !\n");
            break;
        }

        // Envoyer le paquet — pas de connexion, direct sendto
        sendto(sock, message, strlen(message), 0,
               (struct sockaddr*)&server_addr,
               server_len);

        // Attendre la reponse
        memset(buffer, 0, BUFFER_SIZE);
        int bytes = recvfrom(sock, buffer, BUFFER_SIZE - 1, 0,
                             NULL, NULL);

        if (bytes < 0) {
            printf("Pas de reponse du serveur (timeout)\n");
        } else {
            printf("Reponse : %s\n", buffer);
        }
    }

    closesocket(sock);
    WSACleanup();
    return 0;
}