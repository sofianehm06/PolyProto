@echo off
REM ===================================================================
REM  Lance un composant Java avec le classpath complet (libs ActiveMQ).
REM  Usage : run.bat <classe.complete> [arguments...]
REM
REM  Exemples :
REM    run.bat server.Server
REM    run.bat server.ServerUDP
REM    run.bat server.ServerRMI
REM    run.bat server.ServerJMS
REM    run.bat server.ServerMulticast
REM    run.bat client.ClientUI
REM ===================================================================
setlocal
cd /d "%~dp0"

if "%~1"=="" (
    echo Usage : run.bat ^<classe^> [arguments...]
    echo   ex.  run.bat server.Server
    echo        run.bat client.ClientUI
    exit /b 1
)

set CP=.;lib\activemq-all-5.19.6.jar;lib\log4j-core-2.25.3.jar;lib\log4j-api-2.25.3.jar;lib\log4j-slf4j2-impl-2.25.3.jar;lib\activemq-log4j-appender-5.19.6.jar

java -cp "%CP%" %*
endlocal
