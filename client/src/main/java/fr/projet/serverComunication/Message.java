package fr.projet.serverComunication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import fr.projet.model.Client;
import fr.projet.type.MessageType;

import java.util.ArrayList;

/**
 * Message caractérisant un message envoyé / reçu
 */
public final class Message {


    @Expose
    @SerializedName("msgType")
    private final MessageType msgType;

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("words")
    private ArrayList<String> words;

    @Expose
    @SerializedName("client")
    private Client client;
    @Expose
    @SerializedName("life")
    private int life;

    public Message(Client client, MessageType msgType, String message) {
        this.client = client;
        this.msgType = msgType;
        this.message = message;
    }

    public int getLife() {
        return life;
    }


    public Message(Client client, MessageType msgType) {
        this(client, msgType, "");
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public Client getClient() {
        return client;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
