# 🚀 GUIDE DE DÉMARRAGE RAPIDE

## 30 Secondes pour Démarrer

### Étape 1: Ouvrir PowerShell
```
Appuyez sur: Win + R
Tapez: powershell
Appuyez sur: Entrée
```

### Étape 2: Aller au dossier
```powershell
cd d:\downloads\QuizIsraaHelmi
```

### Étape 3: Compiler (une seule fois)
```powershell
javac -d build\classes src\quizisraahelmi\*.java
```

### Étape 4: Lancer le serveur
```powershell
cd build\classes
java quizisraahelmi.Serveur
```

### Étape 5: Lancer les clients (3 nouveaux PowerShell)

**PowerShell #2, #3, #4:**
```powershell
cd d:\downloads\QuizIsraaHelmi\build\classes
java quizisraahelmi.Client
```

### Étape 6: Jouer!
- Entrez votre pseudo (Alice, Bob, Charlie)
- Répondez aux questions (1, 2, 3, ou 4)
- Voyez votre score

---

## 🎯 Protocole de Jeu

### Serveur affiche:
```
✓ Tous les joueurs sont connectés !
✓ Démarrage du quiz
```

### Client reçoit:
```
QUESTION|Q00|Quelle est la capitale...
OPTION|1|Paris
OPTION|2|Lyon
OPTION|3|Marseille
OPTION|4|Toulouse
REPONSE|Entrez votre réponse:
```

### Vous tapez:
```
1
```

### Réponse:
```
✓ Bravo! +20 points
Score total: 20
```

---

## ✅ Checklist

- [ ] PowerShell ouvert
- [ ] Dossier correct: `d:\downloads\QuizIsraaHelmi`
- [ ] Compilé: `javac -d build\classes src\quizisraahelmi\*.java`
- [ ] Serveur lancé: `java quizisraahelmi.Serveur`
- [ ] 3 clients lancés: `java quizisraahelmi.Client`
- [ ] Jeu en cours

---

## 🎮 Pendant le Jeu

| Action | Commande |
|--------|----------|
| Entrer pseudo | `Tapez votre nom` |
| Répondre question | `1`, `2`, `3`, ou `4` |
| Quitter | `Ctrl+C` |

---

## 🏁 Résultats Finaux

Le serveur affiche:
```
═════════════════════════════════════════════
SCORES FINAUX
═════════════════════════════════════════════
1. Alice: 95 points
2. Bob: 85 points
3. Charlie: 90 points
═════════════════════════════════════════════
```

---

## 📊 Points

- Réponse correcte immédiate: **20 points**
- Réponse correcte rapide: **15-19 points**
- Réponse correcte normale: **10-14 points**
- Réponse incorrecte: **0 points**

---

## 🆘 Si ça ne marche pas

### "Port 12345 in use"
```powershell
# Attendre 1 minute ou changer le port
# dans src/quizisraahelmi/Serveur.java
```

### "File not found"
```powershell
# Vérifier: cd build\classes
java quizisraahelmi.Serveur  # Bon
cd src
java quizisraahelmi.Serveur  # Mauvais
```

### "Connection refused"
```powershell
# Lancer le serveur AVANT les clients
```

---

## 💡 Conseils

1. **Réponses rapides** = Plus de points
2. **Les 3 joueurs** doivent être prêts avant le quiz
3. **Utilisez des pseudos différents** pour les 3 clients
4. **Regardez les timestamps** [TS:X] pour voir la synchronisation Lamport

---

**Amusez-vous! 🎉**
