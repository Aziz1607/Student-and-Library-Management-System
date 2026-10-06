@echo off
setlocal

set "JAVAC_CMD="
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
if not defined JAVAC_CMD if exist "C:\Program Files\Java\jdk1.8.0_202\bin\javac.exe" set "JAVAC_CMD=C:\Program Files\Java\jdk1.8.0_202\bin\javac.exe"
if not defined JAVAC_CMD set "JAVAC_CMD=javac"

echo ==============================================
echo  Compilation du projet CORBA
echo  Compilateur : %JAVAC_CMD%
echo ==============================================

"%JAVAC_CMD%" -encoding UTF-8 Institue\*.java serveur\*.java client\*.java

if errorlevel 1 goto ERREUR

echo ==============================================
echo  [SUCCES] Compilation terminee avec succes !
echo ==============================================
goto FIN

:ERREUR
echo ==============================================
echo  [ERREUR] La compilation a echoue.
echo  Remarque : CORBA necessite Java 8
echo ==============================================

:FIN
endlocal

