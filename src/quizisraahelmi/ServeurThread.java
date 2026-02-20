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
 * Thread serveur pour gérer chaque client connecté
 * Gère la réception des pseudos, les réponses aux questions et les scores
 */
public class ServeurThread extends Thread {
    private final Socket socket;
    private final Serveur server;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName = "Joueur";

    public ServeurThread(Socket socket, Serveur server) {
        this.socket = socket;
        this.server = server;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Erreur initialisation flux : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // 1) Demander le pseudo
            out.println("Entrez votre pseudo :");
            out.flush();
            
            playerName = in.readLine();
            if (playerName == null || playerName.isEmpty()) {
                playerName = "Joueur_" + System.currentTimeMillis();
            }
            
            System.out.println("[" + server.getClock().getTime() + "] Pseudo reçu : " + playerName);
            server.broadcastSecure(playerName + " a rejoint le quiz !");
            
            // Marquer le joueur comme prêt
            server.playerReady(playerName);
            
            // Attendre que le quiz commence
            while (!server.isQuizStarted()) {
                Thread.sleep(100);
            }
            
            // 2) Parcourir les questions et envoyer au client
            out.println("═══════════════════════════════════════════════");
            out.println("Le quiz commence ! Bonne chance, " + playerName + " !");
            out.println("═══════════════════════════════════════════════");
            out.flush();
            
            ArrayList<Questions> allQuestions = server.getQuestions();
            
            for (int i = 0; i < allQuestions.size(); i++) {
                Questions q = allQuestions.get(i);
                
                // Envoyer la question à ce client
                int clockQuestion = server.getClock().tick();
                out.println("\n[TS:" + clockQuestion + "] QUESTION|" + q.Qid + "|" + q.text);
                
                // Envoyer les options
                for (int j = 0; j < q.options.length; j++) {
                    out.println("[TS:" + clockQuestion + "] OPTION|" + (j + 1) + "|" + q.options[j]);
                }
                
                out.println("[TS:" + clockQuestion + "] REPONSE|Entrez votre réponse (1/2/3/4):");
                out.flush();
                
                // Lire la réponse du client - Boucle de validation
                boolean responseValid = false;
                int clientResponse = -1;
                int clientTimestamp = -1;
                
                while (!responseValid) {
                    String clientInput = in.readLine();
                    if (clientInput == null) {
                        i = allQuestions.size();  // Forcer la fin des questions
                        break;  // Client déconnecté
                    }
                    
                    try {
                        // Extraire la réponse et le timestamp
                        String[] parts = clientInput.split("\\|CLOCK\\|");
                        
                        if (parts.length < 2) {
                            // Format invalide
                            out.println("[TS:" + server.getClock().tick() + "]  Format invalide ! Utilisez : nombre|CLOCK|timestamp");
                            out.println("[TS:" + server.getClock().getTime() + "] Entrez votre réponse (1/2/3/4):");
                            out.flush();
                            continue;
                        }
                        
                        clientResponse = Integer.parseInt(parts[0].trim()) - 1;
                        clientTimestamp = Integer.parseInt(parts[1].trim());
                        
                        // Vérifier que la réponse est valide (0-3, donc 1-4 avant conversion)
                        if (clientResponse >= 0 && clientResponse <= 3) {
                            responseValid = true;
                        } else {
                            out.println("[TS:" + server.getClock().tick() + "] ❌ Erreur : Votre réponse doit être 1, 2, 3 ou 4 !");
                            out.println("[TS:" + server.getClock().getTime() + "] Entrez votre réponse (1/2/3/4):");
                            out.flush();
                        }
                    } catch (NumberFormatException e) {
                        out.println("[TS:" + server.getClock().tick() + "] ❌ Format invalide ! Entrez un nombre entre 1 et 4.");
                        out.println("[TS:" + server.getClock().getTime() + "] Entrez votre réponse (1/2/3/4):");
                        out.flush();
                    }
                }
                
                if (clientResponse < 0 || clientResponse > 3) {
                    continue;  // Redemander cette question
                }
                
                // Mettre à jour l'horloge Lamport avec le timestamp du client
                int clockResponse = server.getClock().update(clientTimestamp);
                
                // Vérifier si la réponse est correcte
                boolean correct = false;
                if (clientResponse >= 0 && clientResponse < q.options.length) {
                    correct = q.options[clientResponse].trim().equalsIgnoreCase(q.correct.trim());
                }
                
                // Calculer les points
                if (correct) {
                    int delta = clockResponse - clockQuestion;
                    int points = 10 + Math.max(0, 10 - delta);  // Bonus de rapidité
                    
                   
                    server.addScore(playerName, points);
                    
                    out.println("[TS:" + clockResponse + "]  Bravo " + playerName + " !");
                    out.println("[TS:" + clockResponse + "] +++ " + points + " points +++");
                    out.println("[TS:" + clockResponse + "] Score total : " + server.getScore(playerName));
                } else {
                    out.println("[TS:" + clockResponse + "] Oups ! La bonne réponse était : " + q.correct);
                    out.println("[TS:" + clockResponse + "] Score actuel : " + server.getScore(playerName));
                }
                
                out.flush();
            }
            
            // Fin du quiz pour ce client
            out.println("\n═══════════════════════════════════════════════");
            out.println("FIN DU QUIZ - " + playerName);
            out.println("Score final : " + server.getScore(playerName) + " points");
            out.println("En attente du classement final...");
            out.println("═══════════════════════════════════════════════");
            out.flush();
            
            // Signaler au serveur que ce client a fini
            server.playerFinished(playerName);
            
            // Attendre que le serveur envoie les résultats finaux à tous les clients
            server.waitForResults();
            
            // Attendre un peu pour que les clients puissent lire les résultats
            Thread.sleep(2000);
            
            // Fermer la connexion
            socket.close();
            
        } catch (IOException e) {
            System.err.println("Erreur client " + playerName + " : " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrompu pour " + playerName);
        } finally {
            server.removeClient(this);
        }
    }
    
    /**
     * Envoyer un message au client
     */
    public synchronized void sendMessage(String msg) throws IOException {
        out.println(msg);
        out.flush();
    }
    
    /**
     * Obtenir le nom du joueur
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Obtenir le socket
     */
    public Socket getSocket() {
        return socket;
    }
   // server.ajouterScoreAvecSectionCritique(playerName, points);
}
