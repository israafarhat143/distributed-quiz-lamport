/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools / Templates
 * and open the template in the editor.
 */
package quiz;

import java.io.*;

/**
 * Thread client qui gère la communication avec le serveur Envoie les réponses
 * avec les timestamps Lamport
 */
public class ClientThread extends Thread {

    private final BufferedReader in;
    private final PrintWriter out;
    private final BufferedReader keyboard;
    private final LamportClock clock;

    public ClientThread(BufferedReader in, PrintWriter out, BufferedReader keyboard) {
        this.in = in;
        this.out = out;
        this.keyboard = keyboard;
        this.clock = new LamportClock();
    }

    @Override
    public void run() {
        try {
            // Thread pour lire ce que le serveur envoie
            Thread reader = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    // Connexion serveur fermée
                }
            });
            reader.setDaemon(true);
            reader.start();

            // Envoyer les messages du clavier au serveur
            String userInput;
            while ((userInput = keyboard.readLine()) != null) {
                // Si c'est un chiffre, ajouter le timestamp Lamport (même si c'est pas 1-4, le serveur validera)
                if (userInput.matches("\\d+")) {
                    clock.tick();  // Incrémenter pour cet événement
                    out.println(userInput + "|CLOCK|" + clock.getTime());
                } else {
                    // Message textuel (pseudo ou autre)
                    clock.tick();
                    out.println(userInput);
                }
            }

        } catch (IOException e) {
            System.err.println("Erreur client : " + e.getMessage());
        }
    }
}
