@echo off
setlocal

set "JAVA_CMD="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
if not defined JAVA_CMD if exist "C:\Program Files\Java\jdk1.8.0_202\bin\java.exe" set "JAVA_CMD=C:\Program Files\Java\jdk1.8.0_202\bin\java.exe"
if not defined JAVA_CMD set "JAVA_CMD=java"

echo Lancement du Serveur CORBA avec : %JAVA_CMD%
"%JAVA_CMD%" -cp . serveur.Serveur %*

endlocal

