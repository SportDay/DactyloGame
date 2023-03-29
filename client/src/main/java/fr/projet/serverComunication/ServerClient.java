package fr.projet.serverComunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.projet.DactyloClient;
import fr.projet.controller.GameMenu;
import fr.projet.controller.MultiChoice;
import fr.projet.model.Client;
import fr.projet.type.MessageType;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import static fr.projet.DactyloClient.LOG;


public final class ServerClient {

    private static ServerClient INSTANCE;

    private final Client client;

    private final String ip;
    private final int port;

    private Socket socket;
    private Thread thread;
    private ReadServer readServer;

    private PrintWriter writer;

    private ArrayList<String> words;

    /**
     * Constructeur de la classe ServerClient
     * @param ip l'adresse ip du serveur
     * @param port le port du serveur
     */
    private ServerClient(String ip, int port) {
        client = DactyloClient.getClient();
        this.ip = ip;
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * Connextion au serveur
     *
     * @param ip  adresse ip du serveur
     * @param port port du serveur
     * @return l'instance
     * @throws IOException si la connexion échoue
     */
    public static ServerClient getInstance(String ip, int port) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new ServerClient(ip, port);
            INSTANCE.connect();
        }
        return INSTANCE;
    }

    /**
     * Supprime l'instance
     */
    public static void clear(){
        if(INSTANCE != null){
            INSTANCE.disconnect(false);
            INSTANCE = null;
        }
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    /**
     * Retourne l'instance de connexion au serveur
     * @return
     */
    public static ServerClient getINSTANCE() {
        return INSTANCE;
    }

    public void setMultiChoice(MultiChoice multiChoice) {
        if(readServer != null) {
            readServer.setMultiChoice(multiChoice);
        }
    }

    public void setGameMenu(GameMenu gameMenu) {
        readServer.setGameMenu(gameMenu);
    }

    /**
     * Connexion au serveur
     * @throws IOException
     */
    private void connect() throws IOException {
        LOG.info("Connexion au serveur");
        socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port), 10000);
        writer = new PrintWriter(socket.getOutputStream(), true);
        readServer = new ReadServer(this);
        thread = new Thread(readServer);
        thread.start();
        Message message = new Message(client, MessageType.GAME_JOIN);
        LOG.info("Envoi du message de connexion");
        sendMessage(message);
    }

    /**
     * Déconnexion du serveur
     * @param send true si on envoie un message de déconnexion
     */
    public void disconnect(boolean send) {
        LOG.info("Deconnexion du serveur");
        try {
            if(send){
                sendDisconnectMessage();
            }
            if (socket != null) {
                socket.close();
            }
            if (writer != null) {
                writer.close();
                writer = null;
            }
            if (thread != null) {
                thread.interrupt();
            }
            if(readServer != null && readServer.getMultiChoice() != null){
                readServer.getMultiChoice().disconnect();
            }
            INSTANCE = null;
        } catch (IOException ignored) {
            System.out.println(ignored.getMessage());
        }
    }

    /**
     * Envoie du message de déconnexion
     */
    public void sendDisconnectMessage() {
        Message message = new Message(client, MessageType.GAME_FORCE_DISCONNECT);
        sendMessage(message);
    }

    /**
     * Envoie du message vers le serveur
     * @param message message à envoyer
     */
    public void sendMessage(Message message) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(message);
        if(writer != null){
            writer.println(json);
        }
    }
}
