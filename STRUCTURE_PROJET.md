# 📁 STRUCTURE DU PROJET

## Arborescence Complète

```
d:\downloads\QuizIsraaHelmi\
│
├── 📂 src/                                    # Code source Java
│   ├── 📂 quizisraahelmi/                   # Package principal
│   │   ├── 📄 Client.java                   # Point d'entrée client
│   │   ├── 📄 ClientThread.java             # Thread communication client
│   │   ├── 📄 Serveur.java                  # Serveur principal
│   │   ├── 📄 ServeurThread.java            # Thread serveur par client
│   │   ├── 📄 LamportClock.java             # Horloge Lamport
│   │   ├── 📄 Message.java                  # Structure message
│   │   ├── 📄 CriticalSectionManager.java   # Gestion section critique
│   │   ├── 📄 Questions.java                # Structure question
│   │   └── 📄 QuizIsraaHelmi.java           # Main vide (réservé)
│   │
│   └── 📂 Questions/                        # Données
│       └── 📄 Questions.txt                 # Base de questions
│
├── 📂 build/                                 # Fichiers compilés
│   └── 📂 classes/                          # Classes .class
│       └── 📂 quizisraahelmi/               # Classes Java compilées
│
├── 📂 nbproject/                            # Configuration NetBeans
│
├── 📄 build.xml                             # Configuration build (Ant)
│
├── 📄 manifest.mf                           # Manifest JAR
│
├── 📄 README.md                             # Documentation technique
│
├── 📄 MANUEL.md                             # Guide utilisateur
│
├── 📄 CONFIG.md                             # Configuration
│
├── 📄 DEMARRAGE_RAPIDE.md                   # Guide 30 secondes
│
├── 📄 SYNTHESE.md                           # Résumé améliorations
│
├── 📄 STRUCTURE_PROJET.md                   # Ce fichier
│
├── 🔧 run_quiz.bat                          # Script Windows
│
├── 🔧 run_quiz.sh                           # Script Linux/Mac
│
└── 🔧 test_compilation.sh                   # Test compilation

```

---

## 📊 Vue d'ensemble par Fichier

### 🖥️ SERVEUR

#### Serveur.java (260 lignes)
```
├── Classe: Serveur
│   ├── Attributs:
│   │   ├── ServerSocket serverSocket
│   │   ├── List<ServeurThread> clients (thread-safe)
│   │   ├── ArrayList<Questions> questions
│   │   ├── LamportClock lamport
│   │   ├── HashMap<String, Integer> scores
│   │   └── volatile int readyCount
│   │
│   └── Méthodes principales:
│       ├── Serveur(int port) : Constructor
│       ├── loadQuestions() : Charger Q
│       ├── broadcastSecure(String) : Envoyer à tous
│       ├── playerReady(String) : Synchro démarrage
│       ├── addScore(String, int) : Ajouter points
│       ├── getScore(String) : Lire score
│       ├── removeClient(ServeurThread) : Déconnexion
│       └── main(String[]) : Point d'entrée
```

#### ServeurThread.java (140 lignes)
```
├── Classe: ServeurThread extends Thread
│   ├── Attributs:
│   │   ├── Socket socket
│   │   ├── Serveur server
│   │   ├── BufferedReader in
│   │   ├── PrintWriter out
│   │   └── String playerName
│   │
│   └── Méthodes:
│       ├── run() : Boucle principale
│       │   ├── Demander pseudo
│       │   ├── Broadcast à tous
│       │   ├── Attendre démarrage
│       │   ├── Boucle questions:
│       │   │   ├── Envoyer question
│       │   │   ├── Lire réponse (TS)
│       │   │   ├── Vérifier réponse
│       │   │   ├── Calculer points
│       │   │   └── Afficher résultat
│       │   └── Fermer
│       ├── sendMessage(String) : Envoyer
│       ├── getPlayerName() : Getter
│       └── getSocket() : Getter
```

---

### 👤 CLIENT

#### Client.java (45 lignes)
```
├── Classe: Client
│   └── main(String[]):
│       ├── Créer Socket
│       ├── Créer flux I/O
│       ├── Créer ClientThread
│       └── Lancer thread
```

#### ClientThread.java (60 lignes)
```
├── Classe: ClientThread extends Thread
│   ├── Attributs:
│   │   ├── BufferedReader in (final)
│   │   ├── PrintWriter out (final)
│   │   ├── BufferedReader keyboard (final)
│   │   └── LamportClock clock (final)
│   │
│   └── run():
│       ├── Thread daemon pour lire serveur
│       └── Boucle clavier:
│           ├── Si réponse (1-4):
│           │   └── Envoyer + timestamp
│           └── Sinon: Envoyer texte
```

---

### ⏱️ SYNCHRONISATION

#### LamportClock.java (45 lignes)
```
├── Classe: LamportClock
│   ├── Attributs:
│   │   └── AtomicInteger time
│   │
│   └── Méthodes:
│       ├── tick() : L += 1
│       ├── update(int) : L = max(L, ts) + 1
│       ├── getTime() : Lire L
│       ├── reset() : L = 0
│       └── compare(int, int) : Comparer
```

#### Message.java (95 lignes)
```
├── Enum: MessageType
│   ├── PSEUDO
│   ├── QUESTION
│   ├── RESPONSE
│   ├── START_QUIZ
│   ├── END_QUIZ
│   └── ... (8 types)
│
└── Classe: Message implements Comparable
    ├── Attributs:
    │   ├── int lamportTimestamp
    │   ├── String clientId
    │   ├── MessageType type
    │   ├── String content
    │   └── int priority
    │
    └── Méthodes:
        ├── compareTo(Message)
        └── Getters/Setters
```

#### CriticalSectionManager.java (150 lignes)
```
├── Classe: CriticalSectionManager
│   ├── Attributs:
│   │   ├── PriorityQueue<Message> requestQueue
│   │   ├── Set<String> replyReceived
│   │   ├── LamportClock clock
│   │   ├── String ownerId
│   │   ├── boolean inCriticalSection
│   │   └── int totalClients
│   │
│   └── Méthodes:
│       ├── requestCriticalSection()
│       ├── releaseCriticalSection()
│       ├── receiveReply(String)
│       ├── addRequestToQueue(Message)
│       ├── canEnterCriticalSection()
│       ├── getQueuePosition()
│       └── reset()
```

---

### 📚 DONNÉES

#### Questions.java (20 lignes)
```
├── Classe: Questions
│   ├── Attributs (final):
│   │   ├── String Qid : "Q00", "Q01", ...
│   │   ├── String text : Question
│   │   ├── String[] options : Choix
│   │   └── String correct : Bonne réponse
│   │
│   └── Constructor: Questions(Qid, text, options, correct)
```

#### Questions.txt (format)
```
Quelle est la capitale de la France ?
Paris,Lyon,Marseille,Toulouse
Paris

Combien font 3 × 4 ?
9,10,11,12
12

... (5 questions au total)
```

---

## 🔄 Flux de Données

### 1. Démarrage

```
main(Client)
    ↓
    Socket + Streams
    ↓
    ClientThread (daemon)
    ↓
    Serveur accepte connexion
    ↓
    ServeurThread créé
    ↓
    Attendre 3 joueurs
```

### 2. Jeu

```
Serveur: "START"
    ↓
Pour chaque question:
    ├─ Serveur: Envoyer question
    ├─ Client: Afficher question
    ├─ Client: Lire réponse
    ├─ Client: Envoyer réponse + TS
    ├─ Serveur: Mettre à jour horloge
    ├─ Serveur: Vérifier réponse
    ├─ Serveur: Calculer points
    └─ Client: Afficher résultat
    ↓
Fin du quiz
```

### 3. Communication

```
Client → Serveur:
    PSEUDO: "Alice"
    REPONSE: "1|CLOCK|7"

Serveur → Client:
    QUESTION: "Q00|Texte?"
    OPTION: "1|Paris"
    REPONSE: "Entrez:"
    SCORE: "Score: 20"
```

---

## 🧮 Dépendances Entre Classes

```
Main Entry Points:
    Client.java (main)
    Serveur.java (main)

Clients:
    Client → ClientThread → LamportClock
    ClientThread → BufferedReader, PrintWriter

Serveur:
    Serveur → ServeurThread → Questions
    Serveur → LamportClock
    ServeurThread → LamportClock
    
Synchronisation:
    LamportClock (Standalone)
    Message → MessageType
    CriticalSectionManager → LamportClock, Message
```

---

## 📦 Packages

```
quizisraahelmi/
├── Client                  (public class)
├── ClientThread            (public class)
├── Serveur                 (public class)
├── ServeurThread           (public class)
├── LamportClock            (public class)
├── Message                 (public class)
├── Message.MessageType     (public enum)
├── CriticalSectionManager  (public class)
├── Questions               (public class)
└── QuizIsraaHelmi          (public class)
```

---

## 🔐 Visibilité des Membres

```
Public Members:
    - Tous les constructeurs
    - Toutes les méthodes principales
    - Enums et types

Private Members:
    - Streams (in, out)
    - Variables d'état
    - Collections internes

Final Members:
    - Ressources immuables
    - Références constantes
    - Paramètres constructeur
```

---

## 📊 Matrices de Taille

| Classe | Lignes | Méthodes | Attributs |
|--------|--------|----------|-----------|
| Serveur | 260 | 14 | 9 |
| ServeurThread | 140 | 6 | 5 |
| ClientThread | 60 | 1 | 4 |
| Client | 45 | 1 | 0 |
| LamportClock | 45 | 6 | 1 |
| Message | 95 | 10 | 5 |
| CriticalSectionManager | 150 | 9 | 6 |
| Questions | 20 | 1 | 4 |
| **TOTAL** | **815** | **48** | **34** |

---

## 🎯 Responsabilités par Classe

| Classe | Responsabilité | Pattern |
|--------|----------------|---------|
| **Serveur** | Coordonner le jeu | Singleton (implicite) |
| **ServeurThread** | Gérer un client | Observer |
| **Client** | Connexion initiale | Factory |
| **ClientThread** | Communication | Producer/Consumer |
| **LamportClock** | Horloge logique | Utility |
| **Message** | Structure données | DTO |
| **CriticalSectionManager** | Exclusion mutuelle | Manager |
| **Questions** | Structure données | DTO |

---

## 🔌 Points d'Intégration

### Serveur ↔ Clients
```
TCP Socket: localhost:12345
Protocol: Text-based (lines separated by \n)
Format: TYPE|DATA|CLOCK|TIMESTAMP
```

### Synchronisation Intra-Processus
```
synchronized blocks: scores, clients, readyCount
wait()/notify(): startLock
AtomicInteger: LamportClock
Collections.synchronizedList: clients
```

### Synchronisation Inter-Processus
```
LamportClock: Garantit ordre causal
Message.timestamp: Traçabilité
CriticalSectionManager: Exclusion mutuelle
```

---

## 🧪 Points de Test

1. **Compilation**: Aucune erreur
2. **Démarrage serveur**: Port 12345 disponible
3. **Connexion clients**: 3 clients se connectent
4. **Synchronisation**: Timestamps Lamport augmentent
5. **Questions**: Toutes les questions s'affichent
6. **Scoring**: Points calculés correctement
7. **Fin**: Résultats finaux affichés

---

## 📈 Scalabilité

### Limitations actuelles:
- 3 clients fixés
- Questions en mémoire
- Pas de persistance

### Pour étendre:
1. Modifier `expectedPlayers` dans Serveur
2. Charger questions depuis base de données
3. Implémenter CriticalSectionManager
4. Ajouter logging distribuée

---

**Structure bien organisée et scalable! 🎉**
