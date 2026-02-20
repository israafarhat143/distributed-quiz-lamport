# Configuration du Quiz Distribué

## Paramètres du Serveur

### Port
- **Port par défaut:** 12345
- **Fichier:** src/quizisraahelmi/Serveur.java
- **Ligne:** `int port = 12345;`

### Nombre de joueurs
- **Joueurs attendus:** 3
- **Fichier:** src/quizisraahelmi/Serveur.java
- **Ligne:** `private final int expectedPlayers = 3;`

### Fichier de questions
- **Chemin:** src/Questions/Questions.txt
- **Format:** Voir MANUEL.md

## Paramètres Client

### Serveur à rejoindre
- **Hôte:** localhost
- **Port:** 12345
- **Fichier:** src/quizisraahelmi/Client.java
- **Ligne:** `Socket socket = new Socket("localhost", 12345);`

## Système de Points

### Formule
```
Points = 10 + Max(0, 10 - delta)
```

Où:
- `delta = timestamp_réponse - timestamp_question`
- Maximum: 20 points par question
- Minimum: 10 points par question

### Ajuster les points
- **Fichier:** src/quizisraahelmi/ServeurThread.java
- **Ligne:** `int points = 10 + Math.max(0, 10 - delta);`

## Horloge Lamport

### Comportement
- Chaque client a sa propre horloge
- Chaque serveur a son horloge
- Synchronisation lors de la réception de messages

### Implémentation
- **Fichier:** src/quizisraahelmi/LamportClock.java
- **Classe:** AtomicInteger (thread-safe)

## Compilation

### Commande
```powershell
javac -d build\classes src\quizisraahelmi\*.java
```

### Répertoire de sortie
- **Dossier:** build/classes
- **Chemin:** d:\downloads\QuizIsraaHelmi\build\classes

## Structure des Questions

### Format du fichier `Questions.txt`

```
Texte de la question ?
Option1,Option2,Option3,Option4
Option1

```

### Lignes par question
- Ligne 1: Texte
- Ligne 2: Options (séparées par des virgules)
- Ligne 3: Réponse correcte
- Ligne 4: Vide

### Contraintes
- Maximum 4 options par question
- La réponse correcte doit être dans la liste
- Pas d'espaces autour des virgules
- Pas de caractères spéciaux (accents ok)

## Communication

### Format des messages

```
[TS:timestamp] TYPE|CONTENU
```

### Types de messages
- QUESTION: Envoi d'une question
- OPTION: Envoi d'une option
- REPONSE: Demande de réponse
- START: Début du quiz
- END: Fin du quiz

### Format de réponse client

```
numéro|CLOCK|timestamp
```

Exemple:
```
1|CLOCK|7
```

## Threads

### Serveur
- Thread principal: accepte les connexions
- ServeurThread (×3): gère chaque client

### Client
- Thread principal: lit le clavier
- ClientThread (daemon): lit depuis le serveur

## Synchronisation

### Mécanismes utilisés
- `synchronized`: Blocs critiques
- `wait()`/`notify()`: Synchronisation inter-threads
- `AtomicInteger`: Opérations atomiques

### Ressources protégées
- `scores`: HashMap des scores
- `readyCount`: Compteur de joueurs prêts
- `clients`: Liste des clients connectés

## Performance

### Timeouts
- `Thread.sleep(100)`: Attente du démarrage du quiz
- `Thread.sleep(1)`: Entre les clients (script)

### Bande passante
- Messages texte simples
- Pas de sérialisation d'objets
- Communication basée sur des chaînes

## Sécurité

### Points d'attention
- Pas de chiffrement
- Pas d'authentification
- Pas de validation réseau
- Pour environnement local uniquement

## Extension

### Ajouter des données
1. Modifier le fichier de questions
2. Recompiler: `javac -d build\classes src\quizisraahelmi\*.java`
3. Relancer les applications

### Ajouter des fonctionnalités
1. Modifier les classes Java
2. Recompiler
3. Relancer

### Utiliser CriticalSectionManager
```java
CriticalSectionManager csm = new CriticalSectionManager(
    "CLIENT1", 4, lamportClock
);

// Demander l'accès
csm.requestCriticalSection();

// Section critique
// ... code protégé ...

// Libérer
csm.releaseCriticalSection();
```

## Débogage

### Activer les logs
- Les messages s'affichent automatiquement
- Vérifier la console pour les timestamps

### Vérifier la synchronisation
- Consulter les timestamps Lamport [TS:X]
- Vérifier que les scores augmentent

### Problèmes courants
- Voir MANUEL.md section "Problèmes Courants"

## Fichiers de Configuration

- `src/quizisraahelmi/Serveur.java`: Paramètres serveur
- `src/quizisraahelmi/Client.java`: Paramètres client
- `src/Questions/Questions.txt`: Données
- `run_quiz.bat`: Script Windows
- `run_quiz.sh`: Script Linux/Mac
