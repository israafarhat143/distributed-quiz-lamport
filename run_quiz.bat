@echo off
REM Script pour lancer le serveur et les clients du quiz sous Windows

setlocal enabledelayedexpansion

echo.
echo ╔═════════════════════════════════════════════════════════════════╗
echo ║  SYSTÈME DE QUIZ DISTRIBUÉ AVEC SYNCHRONISATION LAMPORT         ║
echo ╚═════════════════════════════════════════════════════════════════╝
echo.
echo Ce script lance:
echo 1. Le serveur du quiz ^(attend 3 clients^)
echo 2. Trois clients pour jouer au quiz
echo.

REM Vérifier que les fichiers sont compilés
if not exist "build\classes" (
    echo ❌ Erreur : Le projet n'est pas compilé
    echo Compilez d'abord avec: javac -d build\classes src\quizisraahelmi\*.java
    pause
    exit /b 1
)

echo ✓ Projet compilé détecté
echo.

echo ▶ Compilation du projet...
cd /d "%~dp0"
javac -d build\classes src\quizisraahelmi\*.java
if errorlevel 1 (
    echo ❌ Erreur de compilation
    pause
    exit /b 1
)

echo ✓ Compilation réussie
echo.
echo ═════════════════════════════════════════════════════════════════
echo ▶ Lancement du serveur...
echo ═════════════════════════════════════════════════════════════════
echo.

REM Lancer le serveur dans une fenêtre PowerShell
start "" powershell -NoExit -Command "cd '%CD%\build\classes'; java quizisraahelmi.Serveur"

REM Attendre un peu pour que le serveur démarre
timeout /t 3 /nobreak

echo.
echo Lancement des clients...
echo.

REM Lancer 3 clients dans des fenêtres PowerShell séparées
for /L %%i in (1,1,3) do (
    echo Lancement du client %%i...
    start "" powershell -NoExit -Command "cd '%CD%\build\classes'; java quizisraahelmi.Client"
    timeout /t 1 /nobreak
)

echo.
echo ═════════════════════════════════════════════════════════════════
echo ✓ Serveur et clients lancés
echo Fermez les fenêtres PowerShell pour terminer
echo ═════════════════════════════════════════════════════════════════
