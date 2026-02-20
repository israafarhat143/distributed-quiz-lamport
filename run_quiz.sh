#!/bin/bash
# Script pour lancer le serveur et les clients du quiz

echo "╔═════════════════════════════════════════════════════════════════╗"
echo "║  SYSTÈME DE QUIZ DISTRIBUÉ AVEC SYNCHRONISATION LAMPORT         ║"
echo "╚═════════════════════════════════════════════════════════════════╝"
echo ""
echo "Ce script lance:"
echo "1. Le serveur du quiz (attend 3 clients)"
echo "2. Trois clients pour jouer au quiz"
echo ""

# Vérifier que les fichiers sont compilés
if [ ! -d "build/classes" ]; then
    echo "❌ Erreur : Le projet n'est pas compilé"
    echo "Compilez d'abord avec: javac -d build/classes src/quizisraahelmi/*.java"
    exit 1
fi

echo "✓ Projet compilé détecté"
echo ""

# Fonction pour lancer le serveur
launch_server() {
    echo "▶ Lancement du serveur..."
    cd build/classes
    java quizisraahelmi.Serveur
}

# Fonction pour lancer un client
launch_client() {
    local client_num=$1
    echo "▶ Lancement du client $client_num..."
    sleep 1
    cd build/classes
    java quizisraahelmi.Client
}

# Lancer le serveur en arrière-plan
launch_server &
SERVER_PID=$!

# Attendre que le serveur démarre
sleep 2

# Lancer les clients
for i in 1 2 3; do
    launch_client $i &
done

# Attendre tous les processus
wait

echo ""
echo "═════════════════════════════════════════════════════════════════"
echo "✓ Quiz terminé"
echo "═════════════════════════════════════════════════════════════════"
