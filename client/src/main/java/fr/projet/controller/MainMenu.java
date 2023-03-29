package fr.projet.controller;

import fr.projet.DactyloClient;
import fr.projet.type.GameType;
import fr.projet.type.MenuType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Contrôleur pour le menu principal.
 */
public class MainMenu {

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private Button multiBTN;

    @FXML
    private Button settings;

    @FXML
    private Button soloBtn;

    @FXML
    private Button statsBTN;

    @FXML
    private Button trainBtn;

    /**
     * Initialisation des boutons.
     */
    @FXML
    public void initialize() {
        if (DactyloClient.getClient().isFirstTime()) {
            soloBtn.setDisable(true);
            trainBtn.setDisable(true);
            multiBTN.setDisable(true);
            statsBTN.setDisable(true);
        }
    }

    /**
     * Démarre une partie en mode solo.
     *
     * @param event MouseEvent
     * @throws IOException
     */
    @FXML
    void playLocal(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            DactyloClient.setNewScene(MenuType.GAME, GameType.LOCAL);
        }
    }

    /**
     * Démarre une partie en mode entraînement.
     *
     * @param event MouseEvent
     * @throws IOException
     */
    @FXML
    void playTrain(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            DactyloClient.setNewScene(MenuType.GAME, GameType.TRAIN);
        }
    }

    /**
     * Démarre une partie en mode multijoueur.
     *
     * @param event MouseEvent
     * @throws IOException
     */
    @FXML
    void playMulti(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            DactyloClient.setNewScene(MenuType.MULTICHOICE, GameType.MULTI);
        }
    }

    /**
     * Affiche les statistiques.
     *
     * @param event MouseEvent
     * @throws IOException
     */
    @FXML
    void showStats(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            DactyloClient.setNewScene(MenuType.STATS);
        }
    }

    /**
     * Affiche les paramètres.
     *
     * @param event MouseEvent
     * @throws IOException
     */
    @FXML
    void showSettings(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            DactyloClient.setNewScene(MenuType.SETTINGS);
        }
    }
}
