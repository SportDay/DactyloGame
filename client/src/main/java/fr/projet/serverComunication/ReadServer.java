package fr.projet.serverComunication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.projet.DactyloClient;
import fr.projet.controller.GameMenu;
import fr.projet.controller.MultiChoice;
import fr.projet.type.GameType;
import fr.projet.type.MenuType;
import fr.projet.type.MessageType;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

import static fr.projet.DactyloClient.LOG;

public final class ReadServer implements Runnable{

    private final ServerClient serverClient;
    private final BufferedReader cin;
    private boolean running = true;

    private MultiChoice multiChoice;
    private GameMenu gameMenu;

    public void setMultiChoice(MultiChoice multiChoice) {
        this.multiChoice = multiChoice;
    }

    public ReadServer(ServerClient serverClient) throws IOException {
        this.serverClient = serverClient;
        Socket socket = serverClient.getSocket();
        this.cin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Lecture des messages du serveur
     */
    @Override
    public void run() {
        try {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            while (running) {
                String message = cin.readLine();
                if(message != null){
                    Message msg = gson.fromJson(message, Message.class);
                    LOG.info("Message recu : " + msg.getMessage() + " de type " + msg.getMsgType());
                    if(msg.getMsgType() == MessageType.GAME_JOIN_SUCCESS){
                        if(multiChoice != null) {
                            multiChoice.addLogs("Connexion au serveur réussie.");
                        }
                        if(gameMenu != null){
                            gameMenu.addLogs("Connexion au serveur réussie.");
                        }
                    }
                    if(msg.getMsgType() != MessageType.GAME_PLAYER_LIST){
                        if(multiChoice != null) {
                            multiChoice.addLogs(msg.getMessage());
                        }
                        if(gameMenu != null){
                            gameMenu.addLogs(msg.getMessage());
                        }
                    }

                    if(msg.getMsgType() == MessageType.GAME_START){
                        serverClient.setWords(msg.getWords());
                        DactyloClient.getClient().setMultiLife(msg.getLife());
                        Platform.runLater(() -> {
                            try {
                                DactyloClient.setNewScene(MenuType.GAME, GameType.MULTI);
                                multiChoice = null;
                                Message tmp = new Message(DactyloClient.getClient(), MessageType.GAME_PLAYER_LIST);
                                serverClient.sendMessage(tmp);
                            } catch (IOException ignored) {
                            }
                        });
                    }

                    if(msg.getMsgType() == MessageType.GAME_SPECIAL){
                        if(gameMenu != null) {
                            Platform.runLater(() -> {
                                if(msg.getWords() != null && msg.getWords().size() > 0) {
                                    boolean good = gameMenu.getWords().check(false, msg.getWords().get(0));
                                    gameMenu.updateView(!good,true);
                                }
                            });
                        }
                    }
                    if(msg.getMsgType() == MessageType.GAME_LOSE){
                        if(gameMenu != null) {
                            gameMenu.getLife().setLife(0);
                            Platform.runLater(() -> {
                                gameMenu.getWords().setGameOver(true);
                                gameMenu.endSession(DactyloClient.getGameType() == GameType.MULTI);
                            });
                        }
                    }
                    if(msg.getMsgType() == MessageType.GAME_WIN){
                        if(gameMenu != null){
                            Platform.runLater(() -> {
                                gameMenu.getWords().setGameOver(true);
                                gameMenu.endSession(DactyloClient.getGameType() == GameType.MULTI);
                            });
                        }
                    }
                    if(msg.getMsgType() == MessageType.GAME_PLAYER_LIST){
                        if(multiChoice != null){
                            multiChoice.addPlayer(msg.getMessage());
                        }
                        if(gameMenu != null){
                            gameMenu.addPlayer(msg.getMessage());
                        }
                    }
                    switch (msg.getMsgType()){
                        case GAME_DENY, GAME_END, GAME_FORCE_DISCONNECT -> {
                            cin.close();
                            serverClient.disconnect(false);
                            running = false;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            if(!e.getMessage().contentEquals("Connection reset")){
                serverClient.disconnect(false);
            }
        } catch (IOException exception) {
            if(!exception.getMessage().equalsIgnoreCase("Stream closed")){
                LOG.info("Deconnexion du serveur: " + exception.getMessage());
                serverClient.disconnect(true);
            }
        } finally {
            try {
                cin.close();
            } catch (Exception ignored) {
            }
        }
    }

    public MultiChoice getMultiChoice() {
        return multiChoice;
    }


    public void setGameMenu(GameMenu gameMenu) {
        this.gameMenu = gameMenu;
    }
}
