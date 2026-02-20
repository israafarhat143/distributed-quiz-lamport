# Système de Quiz Distribué avec Synchronisation Lamport

## 📋 Description du Projet

Ce projet implémente une **application distribuée de quiz** en Java utilisant:
- **Architecture Client/Serveur** avec sockets
- **Synchronisation distribuée** via l'horloge Lamport
- **Gestion des accès concurrents** avec threads
- **Calcul des scores** basé sur la rapidité de réponse

### Fonctionnalités Principales

✅ **Serveur multi-clients** : Attend 3 clients avant de démarrer le quiz
✅ **Horloge Lamport** : Synchronisation causale des événements
✅ **Questions avec options** : Système de Q&A interactif
✅ **Système de scoring** : Points basés sur la rapidité (bonus de temps)
✅ **Thread-safe** : Accès synchronisé aux données partagées
✅ **Affichage des résultats** : Classement final des joueurs

---

## 🏗️ Architecture du Système

### Composants

#### 1. **LamportClock.java** ⏱️
Horloge logique pour la synchronisation distribuée.

**Fonctionnalités:**
- `tick()` : Incrémenter l'horloge pour un événement local
- `update(int received)` : Synchroniser avec un timestamp reçu
- `getTime()` : Obtenir le timestamp actuel

#### 2. **Message.java** 📨
Classe pour représenter les messages avec timestamp Lamport.

**Types de messages:**
- PSEUDO, QUESTION, RESPONSE, START_QUIZ, END_QUIZ, etc.

#### 3. **CriticalSectionManager.java** 🔒
Gestionnaire d'accès à la section critique basé sur l'algorithme Lamport.

**Permet:**
- Demander l'accès à une ressource partagée
- Gérer une file d'attente prioritaire
- Assurer l'exclusion mutuelle

#### 4. **Questions.java** 
Classe pour représenter une question avec options.

```java
public class Questions {
    public final String Qid;      // ID de la question (Q00, Q01, ...)
    public final String text;     // Texte de la question
    public String[] options;      // Options de réponse
    public final String correct;  // Réponse correcte
}
```

#### 5. **Serveur.java** 🖥️
Serveur principal qui gère les connexions et le quiz.

**Responsabilités:**
- Accepter les connexions des clients
- Charger les questions
- Synchroniser le démarrage du quiz
- Gérer les scores
- Broadcaster les messages

#### 6. **ServeurThread.java** 🧵
Thread serveur pour chaque client connecté.

**Processus:**
1. Demander le pseudo du joueur
2. Attendre que tous les joueurs soient prêts
3. Envoyer les questions une par une
4. Traiter les réponses avec timestamps Lamport
5. Calculer les points

#### 7. **Client.java** 👤
Point d'entrée du client.

#### 8. **ClientThread.java** 🧵
Thread client qui gère la communication avec le serveur.

**Gère:**
- Réception des messages du serveur
- Envoi des réponses avec timestamps Lamport

---

## 📊 Protocole de Communication

### Format des Messages

Les messages utilisent le format suivant :

```
[TS:timestamp] TYPE|CONTENU

Exemples:
[TS:5] QUESTION|Q00|Quelle est la capitale de la France ?
[TS:5] OPTION|1|Paris
[TS:5] REPONSE|Entrez votre réponse (1/2/3/4):
```

### Format de Réponse

```
numéro|CLOCK|timestamp

Exemple:
1|CLOCK|7
```

---

## 🎯 Flux d'Exécution

```
┌─────────────┐
│   Serveur   │
│   Démarré   │
└─────────────┘
       │
       ├─ Attend Client 1
       ├─ Attend Client 2
       ├─ Attend Client 3
       │
       └─ Tous connectés ✓
           │
           ├─ Client 1: Entrez pseudo → Joueur1
           ├─ Client 2: Entrez pseudo → Joueur2
           ├─ Client 3: Entrez pseudo → Joueur3
           │
           └─ Tous prêts ✓
               │
               ├─ Envoyer Question Q00 à tous
               │   └─ Client répond + Timestamp
               ├─ Mettre à jour Horloge Lamport
               ├─ Calculer score
               │
               ├─ Envoyer Question Q01 à tous
               │   (idem...)
               │
               ├─ ... (Questions Q02 à Q04)
               │
               └─ FIN DU QUIZ
                   ├─ Afficher les scores
                   └─ Fermer les connexions
```

---

## 🔢 Système de Scoring

Les points sont calculés selon la formule:

```
Points = 10 + Max(0, 10 - delta)

Où:
- delta = (timestamp_réponse - timestamp_question)
- Un score maximum de 20 points par question
- Bonus de rapidité : -1 point tous les 1 unité de temps
```

**Exemple:**
- Réponse immédiate (delta=0) : 20 points
- Réponse avec 5 unités de retard : 15 points
- Réponse avec 10+ unités de retard : 10 points

---

## 📁 Structure des Fichiers

```
QuizIsraaHelmi/
├── src/quizisraahelmi/
│   ├── Client.java              (Point d'entrée client)
│   ├── ClientThread.java        (Thread de communication client)
│   ├── Serveur.java             (Serveur principal)
│   ├── ServeurThread.java       (Thread serveur par client)
│   ├── LamportClock.java        (Horloge logique)
│   ├── Message.java             (Structure de message)
│   ├── CriticalSectionManager.java (Gestion section critique)
│   └── Questions.java           (Structure de question)
│
├── src/Questions/
│   └── Questions.txt            (Base de questions)
│
├── build/                       (Fichiers compilés)
│
├── run_quiz.bat                (Script Windows)
├── run_quiz.sh                 (Script Linux/Mac)
└── README.md                   (Ce fichier)
```

---

## 🚀 Installation et Utilisation

### Compilation

```bash
# Compiler tous les fichiers
javac -d build/classes src/quizisraahelmi/*.java
```

### Lancement

#### Option 1 : Script automatisé (Windows)
```bash
run_quiz.bat
```

#### Option 2 : Manuel - Terminal 1 (Serveur)
```bash
cd build/classes
java quizisraahelmi.Serveur
```

#### Option 2 : Manuel - Terminal 2, 3, 4 (Clients)
```bash
cd build/classes
java quizisraahelmi.Client
```

---

## 📝 Format du Fichier de Questions

`src/Questions/Questions.txt`

```
Quelle est la capitale de la France ?
Paris,Lyon,Marseille,Toulouse
Paris
Combien font 3 × 4 ?
9,10,11,12
12
```

**Format:**
- Ligne 1: Texte de la question
- Ligne 2: Options séparées par des virgules
- Ligne 3: Réponse correcte
- Ligne 4: Vide (séparateur)

---

## 🔐 Synchronisation Lamport

### Comment ça marche ?

L'horloge Lamport garantit un **ordre causal** des événements:

```
Client A: Event (timestamp=3)
           │
           ├─ Envoi avec TS:3
           │
           ▼
Serveur:   Reçoit TS:3
           Son TS actuel = 2
           Calcule: TS = max(2, 3) + 1 = 4
           ✓ Ordre causal maintenu
```

### Avantages

✅ Détecte les dépendances causales
✅ Ordonne totalement les événements
✅ Pas besoin d'horloge physique
✅ Léger (un entier par processus)

---

## 🧪 Exemple d'Exécution

### Console Serveur
```
═════════════════════════════════════════════
Serveur du Quiz démarré sur le port 12345
En attente de 3 joueurs...
═════════════════════════════════════════════
✓ Questions chargées : 5 questions
[1] Nouvelle connexion : 127.0.0.1
[1] Pseudo reçu : Alice
[1] Alice est prêt (1/3)
[2] Nouvelle connexion : 127.0.0.1
[2] Pseudo reçu : Bob
[2] Bob est prêt (2/3)
[3] Nouvelle connexion : 127.0.0.1
[3] Pseudo reçu : Charlie
[3] Charlie est prêt (3/3)

[3] ✓ Tous les joueurs sont connectés !
═════════════════════════════════════════════

[4] ▶ Démarrage du quiz pour tous les joueurs !
[SCORE] Alice gagne 20 points → Total: 20
[SCORE] Bob gagne 15 points → Total: 15
[SCORE] Charlie gagne 20 points → Total: 20
...
```

### Console Client (Alice)
```
Entrez votre pseudo :
Alice
═══════════════════════════════════════════════
Le quiz commence ! Bonne chance, Alice !
═══════════════════════════════════════════════

[5] QUESTION|Q00|Quelle est la capitale de la France ?
[5] OPTION|1|Paris
[5] OPTION|2|Lyon
[5] OPTION|3|Marseille
[5] OPTION|4|Toulouse
[5] REPONSE|Entrez votre réponse (1/2/3/4):
1
[7] ✓ Bravo Alice !
[7] +++ 20 points +++
[7] Score total : 20

[8] QUESTION|Q01|Combien font 3 × 4 ?
...
```

---

## 🎓 Concepts Clés Implémentés

### 1. **Horloge Logique de Lamport**
- Chaque processus a sa propre horloge
- Incrmentation locale à chaque événement
- Synchronisation lors de la réception de messages

### 2. **Exclusion Mutuelle** (Implémentée mais pas utilisée dans cette version)
- CriticalSectionManager peut être étendu pour protéger les sections critiques
- Utilise une file d'attente prioritaire basée sur les timestamps Lamport

### 3. **Architecture Distribuée Client/Serveur**
- Sockets TCP pour la communication
- Threads pour la concurrence
- Synchronisation via `synchronized` et `wait/notify`

### 4. **Gestion de la Concurrence**
- HashMap synchronisée pour les clients
- Verrous pour les scores et les états
- Notification entre threads

---

## 🔧 Extension du Projet

### Ajouter plus de questions
Modifier `src/Questions/Questions.txt`:
```
Votre nouvelle question ?
Option1,Option2,Option3,Option4
Option1

```

### Implémenter la section critique
Utiliser `CriticalSectionManager.requestCriticalSection()` pour les ressources partagées

### Ajouter une persistance
Sauvegarder les scores dans une base de données

### Implémenter d'autres algorithmes
- Horloges vectorielles
- Timestamps causels complets

---

## 📚 Références

- **Horloge de Lamport** : Lamport, L. (1978). "Time, Clocks, and the Ordering of Events in a Distributed System"
- **Section Critique** : Ricart-Agrawala Algorithm
- **Java Concurrency** : Oracle Documentation

---

## 🐛 Dépannage

### Problème: "Address already in use"
**Solution:** Le port 12345 est occupé. Changer le port dans `Serveur.java`:
```java
int port = 12346;  // Nouveau port
```

### Problème: "Fichier Questions.txt non trouvé"
**Solution:** Vérifier que vous lancez depuis `build/classes`:
```bash
cd build/classes
java quizisraahelmi.Serveur
```

### Problème: Les clients ne se connectent pas
**Solution:** Vérifier que le serveur est bien lancé et écoute sur le bon port

---

## 📄 Licence

Ce projet est fourni à titre éducatif.

---

## 👥 Auteur

Projet de système distribué avec synchronisation Lamport - 2025

---

## 🎯 Résumé des Améliorations

✅ **Horloge Lamport** : Synchronisation causale
✅ **Gestion multi-clients** : Jusqu'à N clients
✅ **Scoring intelligent** : Basé sur la rapidité
✅ **Thread-safe** : Accès synchronisé
✅ **Code propre** : Documentation complète
✅ **Scripts automatisés** : Lancement facile
✅ **Section critique** : Prête à l'emploi
✅ **Messages structurés** : Classe Message

---

**Bon quiz! 🎉**
