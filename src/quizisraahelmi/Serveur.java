/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools / Templates
 * and open the template in the editor.
 */
package quizisraahelmi;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Serveur du quiz distribué avec synchronisation Lamport
 * Gère les connexions des clients, distribue les questions et calcule les scores
 */
public class Serveur {
    
    private ServerSocket serverSocket;
    private final List<ServeurThread> clients = Collections.synchronizedList(new ArrayList<>());
    private ArrayList<Questions> questions = new ArrayList<>();
    private LamportClock lamport = new LamportClock();
    private HashMap<String, Integer> scores = new HashMap<>();
    private HashMap<String, Integer> currentQuestion = new HashMap<>();
    private int counter = 0;
    private final int expectedPlayers = 3;
    private volatile int readyCount = 0;
    private volatile int finishedCount = 0;
    private volatile boolean quizStarted = false;
    private volatile boolean resultsDisplayed = false;
    private final Object startLock = new Object();
    private final Object endLock = new Object();
    private final Object resultsLock = new Object();
    private CriticalSectionManager sectionCritique;

    public Serveur(int port) {
        try {
            serverSocket = new ServerSocket(port);
            this.sectionCritique = new CriticalSectionManager("Serveur", expectedPlayers, lamport);
            System.out.println("═════════════════════════════════════════════");
            System.out.println("Serveur du Quiz démarré sur le port " + port);
            System.out.println("En attente de " + expectedPlayers + " joueurs...");
            System.out.println("═════════════════════════════════════════════");
            
            // Charger les questions
            loadQuestions();
            
            int playerCount = 0;
            while (playerCount < expectedPlayers) {
                Socket clientSocket = serverSocket.accept();
                String clientAddr = clientSocket.getInetAddress().getHostAddress();
                System.out.println("[" + lamport.getTime() + "] Nouvelle connexion : " + clientAddr);
                
                // Créer un thread pour ce client
                ServeurThread clientThread = new ServeurThread(clientSocket, this);
                clients.add(clientThread);
                clientThread.start();
                playerCount++;
            }
            
            System.out.println("\n[" + lamport.getTime() + "] ✓ Tous les joueurs sont connectés !");
            System.out.println("═════════════════════════════════════════════");
            
            // Attendre que tous les joueurs soient prêts
            synchronized (startLock) {
                while (readyCount < expectedPlayers) {
                    try {
                        startLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            
            // Lancer le quiz
            synchronized (this) {
                quizStarted = true;
                System.out.println("\n[" + lamport.getTime() + "]  Démarrage du quiz pour tous les joueurs !");
                broadcastSecure("START|Le quiz commence maintenant !");
            }
            
            // Attendre que tous les clients finissent le quiz
            synchronized (endLock) {
                while (finishedCount < expectedPlayers) {
                    try {
                        System.out.println(" En attente de " + (expectedPlayers - finishedCount) + " client(s) pour terminer...");
                        endLock.wait(5000);  // Timeout de 5 secondes pour éviter blocage infini
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            
            // Tous les clients ont fini - afficher et envoyer les résultats finaux
            System.out.println("\n[LOG] Tous les clients ont terminé !");
            displayFinalScores();
            broadcastFinalResults();
            
            // Garder le serveur actif pour un peu plus longtemps
            Thread.sleep(3000);
            System.out.println("Fermeture du serveur...");

            
        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Serveur interrompu : " + e.getMessage());
        }
    }
    
    /**
     * Broadcaster un message à tous les clients de manière synchronisée
     */
    public synchronized void broadcastSecure(String msg) {
        int timestamp = lamport.tick();
        String broadcastMsg = "[TS:" + timestamp + "] " + msg;
        
        for (ServeurThread client : clients) {
            try {
                client.sendMessage(broadcastMsg);
            } catch (IOException e) {
                System.err.println("Erreur lors de l'envoi à " + client.getPlayerName());
                removeClient(client);
            }
        }
    }
    
    /**
     * Charger les questions depuis le fichier
     */
    public void loadQuestions() throws IOException {
        // Essayer différents chemins possibles
        File questionsFile = null;
        String[] possiblePaths = {
            "src/Questions/Questions.txt",
            "../../../src/Questions/Questions.txt",
            "Questions/Questions.txt"
        };
        
        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) {
                questionsFile = f;
                break;
            }
        }
        
        if (questionsFile == null) {
            throw new FileNotFoundException("Questions.txt introuvable. Chemins essayés: " + Arrays.toString(possiblePaths));
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(questionsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Ignorer les lignes vides
                if (line.isEmpty()) {
                    continue;
                }
                String qid = String.format("Q%02d", counter++);
                String text = line;
                String optionsLine = br.readLine();
                if (optionsLine == null) break;
                String[] opts = optionsLine.split(",");
                String correctLine = br.readLine();
                if (correctLine == null) break;
                String correct = correctLine.trim();
                questions.add(new Questions(qid, text, opts, correct));
            }
            System.out.println("✓ Questions chargées : " + questions.size() + " questions");
        }
    }
    
    /**
     * Marquer un joueur comme prêt
     */
    public synchronized void playerReady(String playerName) {
        readyCount++;
        // Initialiser le score à 0 pour ce joueur
        if (!scores.containsKey(playerName)) {
            scores.put(playerName, 0);
        }
        System.out.println("[" + lamport.getTime() + "] " + playerName + " est prêt (" + readyCount + "/" + expectedPlayers + ")");
        
        if (readyCount == expectedPlayers) {
            synchronized (startLock) {
                startLock.notifyAll();
            }
        }
    }
    
    /**
     * Marquer un joueur comme ayant terminé le quiz
     */
    public synchronized void playerFinished(String playerName) {
        finishedCount++;
        System.out.println("[" + lamport.getTime() + "] " + playerName + " a terminé (" + finishedCount + "/" + expectedPlayers + ")");
        
        synchronized (endLock) {
            if (finishedCount >= expectedPlayers) {
                endLock.notifyAll();
            }
        }
    }
    
    /**
     * Attendre que les résultats finaux aient été affichés
     */
    public void waitForResults() {
        synchronized (resultsLock) {
            while (!resultsDisplayed) {
                try {
                    resultsLock.wait(10000);  // Timeout de 10 secondes
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    /**
     * Vérifier si le quiz a commencé
     */
    public boolean isQuizStarted() {
        return quizStarted;
    }
    
    /**
     * Obtenir l'horloge Lamport
     */
    public LamportClock getClock() {
        return lamport;
    }
    
    /**
     * Obtenir les questions
     */
    public ArrayList<Questions> getQuestions() {
        return questions;
    }
    
    /**
     * Obtenir une question spécifique
     */
    public Questions getQuestion(int index) {
        if (index >= 0 && index < questions.size()) {
            return questions.get(index);
        }
        return null;
    }
    
    /**
     * Obtenir le nombre total de questions
     */
    public int getTotalQuestions() {
        return questions.size();
    }
    
    /**
     * Ajouter des points au score d'un joueur
     */
    public synchronized void addScore(String playerName, int points) {
        int currentScore = scores.getOrDefault(playerName, 0);
        scores.put(playerName, currentScore + points);
        System.out.println("[SCORE] " + playerName + " gagne " + points + " points → Total: " + scores.get(playerName));
    }
    
    /**
     * Obtenir le score d'un joueur
     */
    public synchronized int getScore(String playerName) {
        return scores.getOrDefault(playerName, 0);
    }
    
    /**
     * Obtenir tous les scores
     */
    public synchronized Map<String, Integer> getAllScores() {
        return new HashMap<>(scores);
    }

    /**
     * Obtenir le gagnant (joueur avec le plus haut score)
     */
    public synchronized String getWinner() {
        if (scores.isEmpty()) return null;
        return scores.entrySet().stream()
                .max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Obtenir le classement trié par score décroissant
     */
    public synchronized List<Map.Entry<String, Integer>> getRanking() {
        List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(scores.entrySet());
        sortedScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sortedScores;
    }
    
    /**
     * Envoyer les résultats finaux à tous les clients
     */
    public synchronized void broadcastFinalResults() {
        String winner = getWinner();
        List<Map.Entry<String, Integer>> ranking = getRanking();
        
        // Envoyer le classement à tous les clients
        for (ServeurThread client : clients) {
            try {
                if (client.getPlayerName().equals(winner)) {
                    // Message de victoire
                    client.sendMessage("\n═════════════════════════════════════════════");
                    client.sendMessage(" BRAVO " + winner.toUpperCase() + " ! 🏆");
                    client.sendMessage("VOUS AVEZ GAGNÉ LE QUIZ !");
                    client.sendMessage("═════════════════════════════════════════════");
                } else {
                    // Message d'encouragement
                    client.sendMessage("\n═════════════════════════════════════════════");
                    client.sendMessage("Merci " + client.getPlayerName() + " !");
                    client.sendMessage("Le gagnant est : " + winner);
                    client.sendMessage("═════════════════════════════════════════════");
                }
                
                // Envoyer le classement complet
                client.sendMessage("\n📊 CLASSEMENT FINAL:");
                int rank = 1;
                for (Map.Entry<String, Integer> entry : ranking) {
                    String medal = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : "  ";
                    client.sendMessage(medal + " " + rank + ". " + entry.getKey() + " : " + entry.getValue() + " points");
                    rank++;
                }
                client.sendMessage("═════════════════════════════════════════════\n");
                
            } catch (IOException e) {
                System.err.println("Erreur lors de l'envoi des résultats à " + client.getPlayerName());
            }
        }
        
        // Signaler que les résultats ont été envoyés
        resultsDisplayed = true;
        synchronized (resultsLock) {
            resultsLock.notifyAll();
        }
    }
    
    /**
     * Enregistrer la question actuelle d'un joueur
     */
    public synchronized void setCurrentQuestion(String playerName, int questionIndex) {
        currentQuestion.put(playerName, questionIndex);
    }
    
    /**
     * Obtenir l'indice de la question actuelle d'un joueur
     */
    public synchronized int getCurrentQuestion(String playerName) {
        return currentQuestion.getOrDefault(playerName, 0);
    }
    
    /**
     * Retirer un client de la liste
     */
    public synchronized void removeClient(ServeurThread client) {
        clients.remove(client);
        System.err.println("Client " + client.getPlayerName() + " déconnecté");
    }
    
     public void ajouterScoreAvecSectionCritique(String joueur, int points) {
        try {
            sectionCritique.requestCriticalSection();
            // Opération protégée
            int scoreActuel = scores.getOrDefault(joueur, 0);
            scores.put(joueur, scoreActuel + points);
            System.out.println("[CRITIQUE] Score mis à jour pour " + joueur);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            sectionCritique.releaseCriticalSection();
        }
    }

    
    /**
     * Afficher les scores finaux
     */
    public synchronized void displayFinalScores() {
        System.out.println("\n═════════════════════════════════════════════");
        System.out.println("🏆 SCORES FINAUX 🏆");
        System.out.println("═════════════════════════════════════════════");
        
        List<Map.Entry<String, Integer>> sortedScores = getRanking();
        
        int rank = 1;
        for (Map.Entry<String, Integer> entry : sortedScores) {
            String medal = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : "  ";
            System.out.println(medal + " " + rank + ". " + entry.getKey() + " : " + entry.getValue() + " points");
            rank++;
        }
        System.out.println("═════════════════════════════════════════════\n");
    }
    
    /**
     * Méthode principale
     */
    public static void main(String[] args) {
        int port = 12345;
        new Serveur(port);
    }
}
