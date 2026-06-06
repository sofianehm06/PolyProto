@echo off
REM ===================================================================
REM  Compilation de toute la partie C (clients + serveurs Winsock).
REM  Necessite GCC (MinGW). Usage : build.bat
REM ===================================================================
setlocal
cd /d "%~dp0"

for %%F in (
    server_tcp client_tcp
    server_udp client_udp
    server_multicast client_multicast
    server_rpc client_rpc
) do (
    gcc %%F.c -o %%F.exe -lws2_32
    if errorlevel 1 (
        echo [C] ECHEC : %%F.c
        exit /b 1
    )
    echo [C] [OK] %%F.exe
)

echo.
echo [C] Compilation terminee.
endlocal
