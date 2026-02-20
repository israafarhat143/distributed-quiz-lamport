# 📖 MANUEL D'UTILISATION - Système de Quiz Distribué

## 🎯 Objectif du Projet

Créer une **application de quiz distribuée** où:
- 3 joueurs se connectent à un serveur central
- Le serveur envoie les questions à tous les joueurs
- Les joueurs répondent et les points sont calculés
- L'horloge Lamport synchronise tous les événements

---

## 🏃 Démarrage Rapide

### 1️⃣ Compilation du Projet

**Option A : Compilation manuelle**
```powershell
cd d:\downloads\QuizIsraaHelmi
javac -d build\classes src\quizisraahelmi\*.java
```

**Option B : Utiliser le script fourni**
```powershell
.\run_quiz.bat
```

### 2️⃣ Lancement du Serveur

**Terminal 1 :**
```powershell
cd d:\downloads\QuizIsraaHelmi\build\classes
java quizisraahelmi.Serveur
```

**Vous devriez voir :**
```
═════════════════════════════════════════════
Serveur du Quiz démarré sur le port 12345
En attente de 3 joueurs...
═════════════════════════════════════════════
✓ Questions chargées : 5 questions
```

### 3️⃣ Lancement des Clients

**Terminal 2, 3 et 4 (lancez 3 fois):**
```powershell
cd d:\downloads\QuizIsraaHelmi\build\classes
java quizisraahelmi.Client
```

---

## 🎮 Utilisation du Jeu

### Étape 1: Connexion des Joueurs

Chaque client doit entrer son pseudo:
```
Entrez votre pseudo :
Alice
```

Le serveur affiche:
```
[1] Alice est prêt (1/3)
```

### Étape 2: Démarrage du Quiz

Une fois les 3 joueurs connectés:
```
[3] ✓ Tous les joueurs sont connectés !
[4] ▶ Démarrage du quiz pour tous les joueurs !
```

### Étape 3: Répondre aux Questions

Le client reçoit:
```
[5] QUESTION|Q00|Quelle est la capitale de la France ?
[5] OPTION|1|Paris
[5] OPTION|2|Lyon
[5] OPTION|3|Marseille
[5] OPTION|4|Toulouse
[5] REPONSE|Entrez votre réponse (1/2/3/4):
```

L'utilisateur tape le numéro:
```
1
```

### Étape 4: Voir le Résultat

```
[7] ✓ Bravo Alice !
[7] +++ 20 points +++
[7] Score total : 20
```

---

## 📊 Système de Points

### Comment les points sont calculés ?

```
Points = 10 + Max(0, 10 - délai)
```

Où `délai` est la différence entre:
- Timestamp de la question
- Timestamp de la réponse

### Exemples:

| Délai | Points | Explication |
|-------|--------|-------------|
| 0-2   | 18-20  | Réponse très rapide |
| 3-5   | 15-17  | Réponse rapide |
| 6-8   | 12-14  | Réponse normale |
| 10+   | 10     | Réponse lente |

---

## 📝 Ajouter Vos Propres Questions

### Éditer le fichier de questions

Fichier: `src/Questions/Questions.txt`

**Format:**
```
Texte de la question ?
Option1,Option2,Option3,Option4
Option1

Deuxième question ?
Opt1,Opt2,Opt3,Opt4
Opt2

```

**Important:**
- Chaque question = 3 lignes
- Les options sont séparées par des virgules (pas d'espaces)
- La bonne réponse doit être exactement comme dans la liste
- Une ligne vide entre les questions

### Exemple complet:

```
Quel est le plus grand océan ?
Atlantique,Pacifique,Indien,Arctique
Pacifique
Quand la France a-t-elle déclaré l'indépendance ?
1789,1815,1871,1945
1789
```

### Recompiler après modification:

```powershell
javac -d build\classes src\quizisraahelmi\*.java
```

---

## 🔍 Comprendre l'Horloge Lamport

### Qu'est-ce que c'est ?

L'horloge Lamport est un **compteur logique** qui synchronise les événements dans un système distribué.

### Comment ça marche ?

**Règles:**
1. Chaque processus a son propre compteur
2. À chaque événement local : `compteur += 1`
3. À la réception d'un message : `compteur = max(compteur, message.timestamp) + 1`

### Exemple dans le Quiz:

```
Serveur (TS=0)          Client (TS=0)
    │                       │
    ├─ Évènement local      │
    │  TS=1                 │
    │                       │
    ├─ Envoie question      │
    │  TS=2────────────────>│
    │                       │ Reçoit TS=2
    │                       │ TS = max(0, 2) + 1 = 3
    │                       │
    │                       ├─ Évènement local
    │                       │  TS=4
    │                       │
    │<──────TS=4────────────┤ Envoie réponse
    │                       │
    ├─ Reçoit TS=4          │
    │  TS = max(2, 4) + 1 = 5
    │
    ✓ Ordre causal maintenu !
```

---

## 🧹 Arrêter le Quiz

### Pour fermer proprement:

1. **Fermez les clients** (Terminal 2, 3, 4)
   - Appuyez sur `Ctrl+C`

2. **Fermez le serveur** (Terminal 1)
   - Appuyez sur `Ctrl+C`

Le serveur affichera:
```
Client Alice déconnecté
Client Bob déconnecté
Client Charlie déconnecté
```

---

## 📋 Structures de Données Principales

### LamportClock

```java
public class LamportClock {
    public int tick()           // Incrémenter
    public int update(int ts)   // Synchroniser
    public int getTime()        // Lire
}
```

### Questions

```java
public class Questions {
    String Qid;        // "Q00", "Q01", ...
    String text;       // "Quelle est..."
    String[] options;  // ["Paris", "Lyon", ...]
    String correct;    // "Paris"
}
```

### Message

```java
public class Message {
    int lamportTimestamp;
    String clientId;
    MessageType type;  // QUESTION, RESPONSE, etc.
    String content;
}
```

---

## 🔧 Configuration Avancée

### Changer le nombre de joueurs

**Fichier:** `src/quizisraahelmi/Serveur.java`

```java
private final int expectedPlayers = 3;  // Changer ici
```

### Changer le port

**Fichier:** `src/quizisraahelmi/Serveur.java` et `Client.java`

```java
int port = 12345;  // Changer à 12346, 12347, ...
```

### Recompiler:

```powershell
javac -d build\classes src\quizisraahelmi\*.java
```

---

## 🐛 Problèmes Courants et Solutions

### ❌ "Port 12345 already in use"

**Cause:** Le port est déjà utilisé

**Solutions:**
1. Attendre quelques minutes et relancer
2. Changer le port (voir section Configuration)
3. Vérifier les processus Java: `Get-Process java`

### ❌ "Fichier Questions.txt introuvable"

**Cause:** Lancement depuis le mauvais répertoire

**Solution:**
```powershell
cd build\classes  # Important !
java quizisraahelmi.Serveur
```

### ❌ "Connection refused"

**Cause:** Le serveur n'est pas lancé

**Solution:**
1. Lancer le serveur dans Terminal 1
2. Attendre le message "En attente de 3 joueurs"
3. Puis lancer les clients

### ❌ "IOException: Connection reset"

**Cause:** Un client s'est déconnecté brutalement

**Solution:** C'est normal, le serveur continue avec les autres clients

---

## 📊 Affichage des Résultats

### À la fin du quiz, le serveur affiche:

```
═════════════════════════════════════════════
SCORES FINAUX
═════════════════════════════════════════════
1. Charlie: 95 points
2. Alice: 90 points
3. Bob: 75 points
═════════════════════════════════════════════
```

### Dans chaque terminal client:

```
═══════════════════════════════════════════════
FIN DU QUIZ - Alice
Score final : 90 points
═══════════════════════════════════════════════
```

---

## 🎓 Concepts Expliqués

### 1. Threads

**Qu'est-ce que c'est?** Des processus légers s'exécutant en parallèle

**Dans notre projet:**
- Serveur Thread: gère chaque client
- Client Thread: communique avec le serveur

### 2. Sockets

**Qu'est-ce que c'est?** Canaux de communication réseau

**Dans notre projet:**
- ServerSocket: serveur écoute sur le port 12345
- Socket: connexion avec chaque client

### 3. Synchronisation

**Qu'est-ce que c'est?** Coordination entre processus concurrents

**Dans notre projet:**
- `synchronized`: protège les données partagées
- `wait()`/`notify()`: synchronise les threads

---

## 💡 Améliorations Possibles

### Court terme:
- ✅ Ajouter plus de questions
- ✅ Changer les points
- ✅ Ajouter des catégories

### Moyen terme:
- 🔲 Sauvegarder les scores en base de données
- 🔲 Interface graphique (Swing/JavaFX)
- 🔲 Classement persistant

### Long terme:
- 🔲 Jeux multiples (tournoi)
- 🔲 Chat entre joueurs
- 🔲 Niveaux de difficulté
- 🔲 Questions en temps réel

---

## 📞 Support

En cas de problème:
1. Vérifier la console du serveur
2. Vérifier les erreurs de compilation
3. Consulter la section "Problèmes Courants"
4. Relancer les programmes

---

## ✅ Checklist de Vérification

Avant de lancer:
- [ ] Projet compilé: `javac -d build\classes src\quizisraahelmi\*.java`
- [ ] Port 12345 disponible
- [ ] Fichier `src/Questions/Questions.txt` existe
- [ ] Répertoire `build/classes` créé

Pendant l'exécution:
- [ ] Serveur accepte 3 connexions
- [ ] Chaque client demande un pseudo
- [ ] Questions s'affichent correctement
- [ ] Points calculés correctement

Après l'exécution:
- [ ] Les scores finaux s'affichent
- [ ] Les connexions se ferment proprement
- [ ] Pas d'erreur dans les consoles

---

**Amusez-vous bien avec le Quiz Distribué! 🎉**
