package fr.projet.controller;

import fr.projet.serverComunication.ServerClient;
import fr.projet.util.Util;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public final class MultiChoice {

    private final Tooltip portError = new Tooltip("Le port doit être compris entre 0 et 65535");
    @FXML
    private TextArea logsArea;
    @FXML
    private Button connectBTN;
    @FXML
    private TextField ipField;
    @FXML
    private AnchorPane mainPanel;
    @FXML
    private Pane serverStatus;
    @FXML
    private TextField portField;
    @FXML
    private TextArea playerList;
    private boolean connected = false;
    private ServerClient serverClient = null;

    private boolean checkPort(String newValue) {
        return newValue.matches("\\d*");
    }

    public void connect() {
        connected = true;
        Platform.runLater(() -> {
            serverStatus.getStyleClass().removeAll("offline");
            serverStatus.getStyleClass().add("online");
            connectBTN.setText("Déconnexion");
        });
    }

    public void disconnect() {
        connected = false;
        Platform.runLater(() -> {
            addLogs("Déconnexion du serveur réussie.");
            serverStatus.getStyleClass().removeAll("online");
            serverStatus.getStyleClass().add("offline");
            connectBTN.setText("Connexion");
            playerList.setText("");
        });

    }

    @FXML
    public void initialize() {
        if (serverClient != null) {
            serverClient.disconnect(true);
        }
        connectBTN.disableProperty().bind(
                ipField.textProperty().isEmpty()
                        .or(portField.textProperty().isEmpty())
        );

        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (checkPort(newValue)) {
                portField.getStyleClass().removeAll("error");
                portError.hide();
                if (newValue.length() > 0 && Integer.parseInt(newValue) > 65535) {
                    portField.setText("65535");
                }
            } else {
                portField.setText(newValue.replaceAll("[^\\d]", ""));
                if (!portField.getStyleClass().contains("error")) {
                    portField.getStyleClass().add("error");
                }
                Util.showTooltip(portField, portError);
            }

            if (newValue.length() > 5) {
                portField.setText(newValue.substring(0, 5));
            }
        });

        portField.setOnKeyPressed(ke -> {
            if (Objects.requireNonNull(ke.getCode()) == KeyCode.ESCAPE) {
                portField.getStyleClass().removeAll("error");
                portField.setText("");
            }
        });

        portField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                if (portField.getText().length() > 0 && checkPort(portField.getText())) {
                    portField.getStyleClass().removeAll("error");
                } else {
                    if (!portField.getStyleClass().contains("error")) {
                        portField.getStyleClass().add("error");
                    }
                }
            }
        });

        TextFormatter<String> formatter = new TextFormatter<>((TextFormatter.Change change) -> {
            String text = change.getText();
            if (!text.isEmpty()) {
                String newText = text.replaceAll("\\s*", "").toLowerCase();
                int carretPos = change.getCaretPosition() - text.length() + newText.length();
                change.setText(newText);
                change.selectRange(carretPos, carretPos);
            }
            return change;
        });

        ipField.setTextFormatter(formatter);
        ipField.setOnKeyPressed(ke -> {
            String ip = ipField.getText();
            switch (ke.getCode()) {
                case ENTER, SPACE, TAB -> {
                    if (ip.length() < 1) {
                        if (!ipField.getStyleClass().contains("error")) {
                            ipField.getStyleClass().add("error");
                        }
                    }
                }
                case ESCAPE -> {
                    ipField.getStyleClass().removeAll("error");
                    ipField.setText("");
                }
            }
        });
    }


    @FXML
    void connect(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (!connected) {
                boolean error = false;
                if (ipField.getText().length() < 1) {
                    if (!ipField.getStyleClass().contains("error")) {
                        ipField.getStyleClass().add("error");
                    }
                    error = true;
                }
                if (portField.getText().length() < 1) {
                    if (!portField.getStyleClass().contains("error")) {
                        portField.getStyleClass().add("error");
                    }
                    error = true;
                }
                if (portField.getStyleClass().contains("error")) {
                    error = true;
                }
                if (ipField.getStyleClass().contains("error")) {
                    error = true;
                }

                if (error) {
                    addLogs("Veuillez vérifier les champs en rouge.");
                    return;
                }

                try {
                    serverClient = ServerClient.getInstance(ipField.getText(), Integer.parseInt(portField.getText()));
                    if (serverClient != null && serverClient.getSocket() != null && serverClient.getSocket().isConnected()) {
                        connect();
                        serverClient.setMultiChoice(this);
                    }
                } catch (IOException e) {
                    if (e.getMessage().contains("Network is unreachable")) {
                        addLogs("Impossible de se connecter au serveur. Verifies que le serveur est bien lance.");
                    } else if (e.getMessage().contains("Connection refused")) {
                        addLogs("Le serveur a refusé la connexion. Verifies que l'adresse IP est correcte et que le port est ouvert.");
                    } else if (e.getMessage().contains("Connection timed out")) {
                        addLogs("Le serveur n'a pas répondu.");
                    } else {
                        addLogs("Une erreur est survenue lors de la connexion au serveur.");
                    }
                    if (serverClient != null) {
                        serverClient.disconnect(true);
                    }else{
                        ServerClient.clear();
                    }
                }
            } else {
                serverClient.disconnect(true);
            }
        }
    }

    public void addLogs(String log) {
        Platform.runLater(() -> {
            logsArea.appendText(log + "\n");
        });
    }

    public void addPlayer(String name) {
        Platform.runLater(() -> {
            playerList.setText(name);
        });
    }
}
