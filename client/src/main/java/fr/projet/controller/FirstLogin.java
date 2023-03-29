package fr.projet.controller;

import fr.projet.DactyloClient;
import fr.projet.type.MenuType;
import fr.projet.util.Security;
import fr.projet.util.TextFieldChecker;
import fr.projet.util.Util;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

/**
 * Classe permettant à l'utilisateur d'entrer un nom et de l'enregistrer.
 */
public class FirstLogin {

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private TextField playerName;

    @FXML
    private Button validBTN;

    /**
     * Initialisation du KeyEvent OnKeyPressed.
     */
    @FXML
    void initialize() {
        playerName.setOnKeyPressed(ke -> {
            if (Objects.requireNonNull(ke.getCode()) == KeyCode.ESCAPE) {
                playerName.getStyleClass().removeAll("error");
                playerName.setText("");
            }
        });

        TextFieldChecker.playerName(playerName);
    }

    /**
     * Sauvegarde le nom.
     *
     * @param event MouseEvent.
     * @throws IOException
     */
    @FXML
    void save(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            boolean error = false;
            if (playerName.getText().length() < 3) {
                if (!playerName.getStyleClass().contains("error")) {
                    playerName.getStyleClass().add("error");
                }
                error = true;
            }
            if (!error) {
                DactyloClient.getClient().setName(playerName.getText());
                Security.saveCrypt(false);
                DactyloClient.setNewScene(MenuType.MAIN);
            } else {
                Util.showTooltip(playerName, TextFieldChecker.nameError);
            }
        }
    }

}
