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
    struct ip_mreq groupe;
    char buffer[BUFFER_SIZE];

    WSAStartup(MAKEWORD(2, 2), &wsa);

    // Socket UDP
    sock = socket(AF_INET, SOCK_DGRAM, 0);

    // Permettre plusieurs processus sur le meme port
    int reuse = 1;
    setsockopt(sock, SOL_SOCKET, SO_REUSEADDR,
               (char*)&reuse, sizeof(reuse));

    // Bind sur le port multicast
    server_addr.sin_family      = AF_INET;
    server_addr.sin_port        = htons(PORT);
    server_addr.sin_addr.s_addr = INADDR_ANY;

    bind(sock, (struct sockaddr*)&server_addr, sizeof(server_addr));

    // Rejoindre le groupe multicast
    groupe.imr_multiaddr.s_addr = inet_addr(GROUPE_MULTICAST);
    groupe.imr_interface.s_addr = INADDR_ANY;

    setsockopt(sock, IPPROTO_IP, IP_ADD_MEMBERSHIP,
               (char*)&groupe, sizeof(groupe));

    printf("=== Client Multicast (C) ===\n");
    printf("Rejoint le groupe %s:%d\n", GROUPE_MULTICAST, PORT);
    printf("En attente de notifications...\n\n");

    while (1) {
        memset(buffer, 0, BUFFER_SIZE);

        int bytes = recvfrom(sock, buffer, BUFFER_SIZE - 1, 0,
                             NULL, NULL);

        if (bytes > 0) {
            printf("[Recu] %s\n", buffer);
        }
    }

    closesocket(sock);
    WSACleanup();
    return 0;
}