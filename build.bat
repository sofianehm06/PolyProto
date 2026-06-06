@echo off
REM ===================================================================
REM  Compilation de toute la partie Java (common, server, client)
REM  Usage : build.bat
REM ===================================================================
setlocal
cd /d "%~dp0"

set CP=.;lib\activemq-all-5.19.6.jar;lib\log4j-core-2.25.3.jar;lib\log4j-api-2.25.3.jar;lib\log4j-slf4j2-impl-2.25.3.jar;lib\activemq-log4j-appender-5.19.6.jar

echo [Java] Compilation en cours...
javac -encoding UTF-8 -cp "%CP%" common\*.java server\*.java client\*.java
if errorlevel 1 (
    echo [Java] ECHEC de la compilation.
    exit /b 1
)
echo [Java] Compilation reussie.
echo.
echo Pour lancer un composant : run.bat ^<classe^>
echo   Exemples :
echo     run.bat server.Server
echo     run.bat client.ClientUI
endlocal
