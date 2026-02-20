#!/bin/bash
# Script de vérification de compilation

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  Vérification du Projet Quiz Distribué                         ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Vérifier la présence des fichiers source
echo "1. Vérification des fichiers source..."
files=(
    "src/quizisraahelmi/Client.java"
    "src/quizisraahelmi/ClientThread.java"
    "src/quizisraahelmi/Serveur.java"
    "src/quizisraahelmi/ServeurThread.java"
    "src/quizisraahelmi/LamportClock.java"
    "src/quizisraahelmi/Message.java"
    "src/quizisraahelmi/CriticalSectionManager.java"
    "src/quizisraahelmi/Questions.java"
    "src/quizisraahelmi/QuizIsraaHelmi.java"
    "src/Questions/Questions.txt"
)

all_exist=true
for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "   ✓ $file"
    else
        echo "   ✗ $file MANQUANT"
        all_exist=false
    fi
done

if [ "$all_exist" = false ]; then
    echo ""
    echo "❌ Certains fichiers manquent!"
    exit 1
fi

echo ""
echo "2. Compilation du projet..."
javac -d build/classes src/quizisraahelmi/*.java 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Erreur de compilation"
    exit 1
fi

echo "✓ Compilation réussie"

echo ""
echo "3. Vérification des fichiers compilés..."
cd build/classes

classes=(
    "quizisraahelmi/Client.class"
    "quizisraahelmi/ClientThread.class"
    "quizisraahelmi/Serveur.class"
    "quizisraahelmi/ServeurThread.class"
    "quizisraahelmi/LamportClock.class"
    "quizisraahelmi/Message.class"
    "quizisraahelmi/CriticalSectionManager.class"
    "quizisraahelmi/Questions.class"
)

for class in "${classes[@]}"; do
    if [ -f "$class" ]; then
        echo "   ✓ $class"
    else
        echo "   ✗ $class MANQUANT"
    fi
done

cd ../..

echo ""
echo "4. Test du serveur (simple vérification)..."
cd build/classes

# Vérifier que la classe peut être trouvée
if java -cp . quizisraahelmi.Serveur --version 2>&1 | head -1 | grep -q "Exception\|Error"; then
    echo "❌ Erreur lors du chargement du serveur"
    exit 1
else
    echo "✓ Serveur chargeable"
fi

echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  ✅ VÉRIFICATION RÉUSSIE - Le projet est prêt à l'emploi!      ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "Prochaines étapes:"
echo "1. Lancer le serveur:  java quizisraahelmi.Serveur"
echo "2. Lancer 3 clients:   java quizisraahelmi.Client"
