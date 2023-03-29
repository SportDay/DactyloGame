package fr.projet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import fr.projet.type.GameState;
import fr.projet.type.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import static fr.projet.DactyloGameServer.LOG;

/**
 * Traite les messages reçus par le serveur
 */
public final class ThreadServer extends Thread {

    /**
     * Socket du client
     */
    private final Socket socket;
    /**
     * Liste des clients connectés
     */
    private final HashMap<ThreadServer, Client> clientList;

    /**
     * Constructeur de la classe ThreadServer
     *
     * @param socket     Socket du client
     * @param clientList Liste des clients
     */
    public ThreadServer(Socket socket, HashMap<ThreadServer, Client> clientList) {
        this.socket = socket;
        this.clientList = clientList;
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * Il ferme le socket, retire le client de la liste, envoie la nouvelle liste des joueurs à tous les clients, et si la
     * partie est en cours et qu'il ne reste plus qu'un joueur, il le déclare vainqueur
     */
    private synchronized void fatal() throws IOException {
        if (!socket.isClosed()) {
            if (clientList.get(this) != null) {
                LOG.info("Joueur deconnecte: " + clientList.get(this));
                Message msg = new Message(DactyloGameServer.SERVER_CLIENT, MessageType.GAME_INFO, clientList.get(this).getName() + " (" + clientList.get(this).getShortName() + ")" + " s'est deconnecte");
                broadcastMessage(this, msg);
            }
            socket.close();
        }
        clientList.remove(this);
        sendPlayerList();
        if (clientList.size() == 1 && GameState.IN_PROGRESS == DactyloGameServer.getGameState()) {
            ThreadServer winner = clientList.keySet().iterator().next();
            Message message = new Message(clientList.get(winner), MessageType.GAME_WIN, "Vous avez gagne!");
            winner.sendMessage(message);
            DactyloGameServer.EndGame(winner);
        } else if (DactyloGameServer.getGameState() != GameState.WAITING_FOR_PLAYERS && clientList.size() <= (DactyloGameServer.getNbPlayer() / 2)) {
            DactyloGameServer.prepareGame(false);
        }
        interrupt();
    }

    /**
     * Il envoie une liste de tous les joueurs connectés au serveur à tous les clients
     */
    private void sendPlayerList() {
        String playerList = "";
        for (var client : clientList.keySet()) {
            if (playerList.contains(clientList.get(client).getName())) {
                playerList += clientList.get(client).getName() + " (" + clientList.get(client).getShortName() + ")" + " | ";
            } else {
                playerList += clientList.get(client).getName() + " | ";
            }
        }
        if (playerList.length() > 0) {
            playerList = playerList.substring(0, playerList.length() - 3);
        }
        Message message = new Message(DactyloGameServer.SERVER_CLIENT, MessageType.GAME_PLAYER_LIST, playerList);
        broadcastMessage(null, message);
    }

    /**
     * Le code ci-dessus est un thread qui écoute le client. Il attend un message du client. Si le message est un
     * GAME_FORCE_DISCONNECT, il fermera le socket et interrompra le thread.
     * Si le message est un GAME_JOIN, il ajoutera le client à la liste des clients et enverra un message au client pour confirmer qu'il a rejoint le jeu.
     * Si le message est un GAME_LOSE, il enverra un message aux autres clients que le client a perdu.
     * Si le message est un GAME_SPECIAL, il enverra un message aux autres clients que le client a utilisé un bonus et le mot bonus.
     * Si le message est un GAME_WIN, il enverra un message aux autres clients que le client a gagné. Si le message est un GAME_INFO, il enverra un message aux autres clients que le client a envoyé un message.
     * Si le message est un GAME_PLAYER_LIST, il enverra une liste de tous les joueurs connectés au serveur à tous les clients.
     */
    @Override

    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (socket.isConnected()) {
                String outputString = input.readLine();
                if (outputString == null) {
                    socket.close();
                    interrupt();
                    continue;
                }
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                Message msg = gson.fromJson(outputString, Message.class);
                if (msg.getMsgType() == MessageType.GAME_FORCE_DISCONNECT) {
                    LOG.info("Le joueur " + msg.getClient().getName() + " veut se deconnecter.");
                    fatal();
                } else if (msg.getMsgType() == MessageType.GAME_JOIN) {
                    if (DactyloGameServer.getGameState() != GameState.WAITING_FOR_PLAYERS) {
                        Message message = new Message(DactyloGameServer.SERVER_CLIENT, MessageType.GAME_FORCE_DISCONNECT, "Jeu deja commence");
                        sendMessage(message);
                        fatal();
                        continue;
                    }
                    clientList.put(this, msg.getClient());
                    if (clientList.size() > DactyloGameServer.getNbPlayer()) {
                        LOG.info("Le serveur est plein");
                        Message message = new Message(DactyloGameServer.SERVER_CLIENT, MessageType.GAME_FORCE_DISCONNECT, "Le serveur est plein.");
                        sendMessage(message);
                        fatal();
                        continue;
                    }
                    LOG.info("Le joueur " + msg.getClient().getName() + " a rejoint la partie.");

                    Message message = new Message(DactyloGameServer.SERVER_CLIENT, MessageType.GAME_JOIN_SUCCESS, "Vous avez rejoint la partie.");
                    sendMessage(message);
                    message = new Message(DactyloGameServer.SERVER_CLIENT, MessageType.GAME_INFO, msg.getClient().getName() + " a rejoint la partie.");
                    broadcastMessage(this, message);
                    sendPlayerList();

                    if (clientList.size() == DactyloGameServer.getNbPlayer() && DactyloGameServer.getGameState() == GameState.WAITING_FOR_PLAYERS) {
                        DactyloGameServer.preStartGame();
                    }
                } else if (msg.getMsgType() == MessageType.GAME_LOSE) {
                    Message message = new Message(DactyloGameServer.SERVER_CLIENT, MessageType.GAME_INFO, msg.getClient().getName() + " a perdu.");
                    broadcastMessage(this, message);
                    fatal();
                    if (clientList.size() == 1) {
                        ThreadServer winner = clientList.keySet().iterator().next();
                        message = new Message(clientList.get(winner), MessageType.GAME_WIN, "Vous avez gagne!");
                        winner.sendMessage(message);
                        DactyloGameServer.EndGame(winner);
                    }
                } else if (msg.getMsgType() == MessageType.GAME_SPECIAL) {
                    LOG.info("Le joueur " + msg.getClient().getName() + " veut piéger les autres joueurs");
                    Message wordMessage = new Message(DactyloGameServer.SERVER_CLIENT, MessageType.GAME_SPECIAL, "Reception du nouveau mot: " + msg.getMessage());
                    ArrayList<String> words = new ArrayList<>();
                    words.add(msg.getMessage());
                    wordMessage.setWords(words);
                    broadcastMessage(this, wordMessage);
                } else if (msg.getMsgType() == MessageType.GAME_PLAYER_LIST) {
                    sendPlayerList();
                } else if (msg.getMsgType() == MessageType.GAME_INFO) {
                    LOG.info("Message du joueur: " + msg.getMessage());
                }
            }
        } catch (JsonSyntaxException | IOException e) {
            try {
                fatal();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Il envoie un message au client
     *
     * @param message le message à envoyer
     */
    public void sendMessage(Message message) {
        try {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String json = gson.toJson(message);
            if (clientList.get(this) != null) {
                if (message.getMsgType() == MessageType.GAME_START) {
                    LOG.info("Envoi des mots au joueur " + clientList.get(this).getName());
                } else {
                    LOG.info("Envoi du message: " + json + " au joueur: " + clientList.get(this).getShortName());
                }
            }
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(json);
        } catch (IOException e) {
            try {
                fatal();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Pour chaque client de la liste des clients, si le client n'est pas le client actuel, envoyez le message au client.
     *
     * @param tmp     Le client qui a envoyé le message
     * @param message Le message à envoyer à tous les clients.
     */
    private void broadcastMessage(ThreadServer tmp, Message message) {
        for (var client : clientList.keySet()) {
            if (client != tmp) {
                client.sendMessage(message);
            }
        }
    }
}
