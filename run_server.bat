@echo off
REM Lance le serveur du Quiz

cd /d "%~dp0build\classes"

echo ════════════════════════════════════════════
echo  LANCEMENT DU SERVEUR QUIZ
echo ════════════════════════════════════════════
echo  Port: 12345
echo  En attente de 3 joueurs...
echo ════════════════════════════════════════════
echo.

java quizisraahelmi.Serveur

pause
