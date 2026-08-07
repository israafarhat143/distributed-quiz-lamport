/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools / Templates
 * and open the template in the editor.
 */
package quiz;

import java.io.Serializable;

/**
 * Classe pour représenter un message avec timestamp Lamport pour la
 * synchronisation
 */
public class Message implements Serializable, Comparable<Message> {

    private static final long serialVersionUID = 1L;

    public enum MessageType {
        PSEUDO, // Client envoie son pseudo
        PLAYER_READY, // Client est prêt
        QUESTION, // Serveur envoie une question
        RESPONSE, // Client répond à une question
        RESPONSE_ACK, // Serveur confirme la réponse
        START_QUIZ, // Lancer le quiz
        END_QUIZ, // Fin du quiz
        BROADCAST, // Message de broadcast
        SCORES, // Afficher les scores
        CRITICAL_SECTION, // Demande de section critique
        CRITICAL_RELEASE  // Libération de section critique
    }

    private int lamportTimestamp;
    private String clientId;
    private MessageType type;
    private String content;
    private int priority;  // Pour l'ordre d'exécution

    public Message(int lamportTimestamp, String clientId, MessageType type, String content) {
        this.lamportTimestamp = lamportTimestamp;
        this.clientId = clientId;
        this.type = type;
        this.content = content;
        this.priority = lamportTimestamp;  // Par défaut, utilise le timestamp comme priorité
    }

    public Message(int lamportTimestamp, String clientId, int priority, MessageType type, String content) {
        this.lamportTimestamp = lamportTimestamp;
        this.clientId = clientId;
        this.type = type;
        this.content = content;
        this.priority = priority;
    }

    @Override
    public int compareTo(Message other) {
        // Comparer d'abord par timestamp Lamport
        if (this.lamportTimestamp != other.lamportTimestamp) {
            return Integer.compare(this.lamportTimestamp, other.lamportTimestamp);
        }
        // En cas d'égalité, comparer par clientId (pour garantir un ordre total)
        return this.clientId.compareTo(other.clientId);
    }

    // Getters
    public int getLamportTimestamp() {
        return lamportTimestamp;
    }

    public String getClientId() {
        return clientId;
    }

    public MessageType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public int getPriority() {
        return priority;
    }

    // Setters
    public void setLamportTimestamp(int timestamp) {
        this.lamportTimestamp = timestamp;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return String.format("[%s | TS:%d | Clnt:%s] %s: %s",
                type, lamportTimestamp, clientId, type, content);
    }
}
