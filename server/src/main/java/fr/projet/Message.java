package fr.projet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import fr.projet.type.MessageType;

import java.util.ArrayList;

/**
 * C'est une classe qui représente un message
 */
public final class Message {


    /**
     * Type de message
     */
    @Expose
    @SerializedName("msgType")
    private final MessageType msgType;

    /**
     * Contenu du message
     */
    @Expose
    @SerializedName("message")
    private String message;

    /**
     * Liste des mots
     */
    @Expose
    @SerializedName("words")
    private ArrayList<String> words;

    /**
     * Client qui envoie le message
     */
    @Expose
    @SerializedName("client")
    private Client client;

    /**
     * Vie
     */
    @Expose
    @SerializedName("life")
    private int life;

    /**
     * Constructeur de la classe Message
     *
     * @param client  Client
     * @param msgType MessageType
     * @param message Message
     */
    public Message(Client client, MessageType msgType, String message) {
        this.client = client;
        this.msgType = msgType;
        this.message = message;
    }

    /**
     * Le nombr de vie du joueur
     *
     * @param life int
     */
    public void setLife(int life) {
        this.life = life;
    }

    /**
     * Constructeur pour envoyer une liste de mots au serveur
     *
     * @param words la liste de mots
     */
    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    /**
     * Retourne un client
     *
     * @return client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Retourne le type de message
     *
     * @return msgType
     */
    public MessageType getMsgType() {
        return msgType;
    }

    /**
     * Retourne le message
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Change le message
     *
     * @param message le nouveau message
     */
    public void setMessage(String message) {
        this.message = message;
    }


}
