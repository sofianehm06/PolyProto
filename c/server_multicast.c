#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>
#include <ws2tcpip.h>

#pragma comment(lib, "ws2_32.lib")

#define GROUPE_MULTICAST "230.0.0.1"
#define PORT 6002
#define BUFFER_SIZE 1024

int main() {
    WSADATA wsa;
    SOCKET sock;
    struct sockaddr_in server_addr;

    if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0) {
        printf("Erreur : initialisation de Winsock impossible.\n");
        return 1;
    }

    // Socket UDP pour envoyer
    sock = socket(AF_INET, SOCK_DGRAM, 0);
    if (sock == INVALID_SOCKET) {
        printf("Erreur : creation du socket impossible (code %d).\n", WSAGetLastError());
        WSACleanup();
        return 1;
    }

    // TTL = combien de routeurs le paquet peut traverser
    int ttl = 1;
    setsockopt(sock, IPPROTO_IP, IP_MULTICAST_TTL,
               (char*)&ttl, sizeof(ttl));

    server_addr.sin_family      = AF_INET;
    server_addr.sin_port        = htons(PORT);
    server_addr.sin_addr.s_addr = inet_addr(GROUPE_MULTICAST);

    printf("=== Serveur Multicast (C) demarre ===\n");
    printf("Groupe : %s:%d\n", GROUPE_MULTICAST, PORT);
    printf("Envoi d'une notification toutes les 5 secondes...\n\n");

    int compteur = 1;

    while (1) {
        char message[BUFFER_SIZE];
        sprintf(message, "NOTIFICATION: Produit #%d ajoute sur le serveur !", compteur);

        sendto(sock, message, strlen(message), 0,
               (struct sockaddr*)&server_addr,
               sizeof(server_addr));

        printf("[Multicast envoye] -> %s\n", message);
        compteur++;

        // Attendre 5 secondes
        Sleep(5000);
    }

    closesocket(sock);
    WSACleanup();
    return 0;
}