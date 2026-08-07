/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quiz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author ASUS
 */
public class Client {
    public static void main(String[] args) {
     try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out =
                    new PrintWriter(socket.getOutputStream(), true);
            BufferedReader keyboard =
                    new BufferedReader(new InputStreamReader(System.in));
            

            
           
            
            System.out.println("Tu est connecté");

            // Lancer le thread qui gère la communication
            ClientThread handler =
                    new ClientThread(in, out, keyboard);

            handler.start();

        } catch (IOException e) {
            System.out.println("Erreur connexion serveur : " + e.getMessage());
        }
    }
    
}
