package fr.projet.controller;

import fr.projet.DactyloClient;
import fr.projet.model.SettingsModel;
import fr.projet.type.MenuType;
import fr.projet.util.Security;
import fr.projet.util.TextFieldChecker;
import fr.projet.util.Util;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrôleur du menu Paramètres.
 */
public class SettingsMenu {

    @FXML
    private Button dellBtn;

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private TextField motLevel;

    @FXML
    private TextField nbrLife;

    @FXML
    private TextField playerName;

    @FXML
    private Button sSolo;

    @FXML
    private Button sTrain;

    @FXML
    private Button sauvegarder;

    @FXML
    private TextField timer;

    @FXML
    private TextField wordEnd;

    private final Tooltip numError = new Tooltip("Que des chiffres sont autorisés");

    @FXML
    private void initialize() {
        sauvegarder.disableProperty().bind(
                playerName.textProperty().isEmpty().or(playerName.textProperty().length().lessThan(3))
        );

        sTrain.disableProperty().bind(
                wordEnd.textProperty().isEmpty().or(wordEnd.textProperty().length().lessThan(1))
        );

        sSolo.disableProperty().bind(
                motLevel.textProperty().isEmpty().or(motLevel.textProperty().length().lessThan(1))
                        .and(nbrLife.textProperty().isEmpty().or(nbrLife.textProperty().length().lessThan(1)))
                        .and(timer.textProperty().isEmpty().or(timer.textProperty().length().lessThan(1)))

        );
        SettingsModel settingsModel = DactyloClient.getClient().getSettingsModel();
        nbrLife.setPromptText(settingsModel.getNbrLife() + " - min: 2");
        timer.setPromptText(settingsModel.getTimerStart() + " - min: 20");
        wordEnd.setPromptText(settingsModel.getWordToEnd() + " - min: 2");
        motLevel.setPromptText(settingsModel.getMotLevel() + " - min: 3");


        playerName.setPromptText(DactyloClient.getClient().getName());

        playerName.setOnKeyPressed(ke -> {
            if (Objects.requireNonNull(ke.getCode()) == KeyCode.ESCAPE) {
                playerName.getStyleClass().removeAll("error");
                playerName.setText("");
            }
        });

        TextFieldChecker.playerName(playerName);
        TextFieldChecker.numTextField(nbrLife, numError);
        TextFieldChecker.numTextField(timer, numError);
        TextFieldChecker.numTextField(wordEnd, numError);
        TextFieldChecker.numTextField(motLevel, numError);
    }

    /**
     * Suppression des données.
     * @param event MouseEvent
     * @throws IOException
     */
    @FXML
    void dell(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (profilReset("Vous voulez supprimer votre profil","Supprimer")) {
                Security.saveCrypt(true);
                DactyloClient.setNewScene(MenuType.FIRST);
                DactyloClient.updateUUID();
            }
        }
    }

    /**
     * Sauvegarde du nom.
     * @param event MouseEvent
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
                DactyloClient.setNewScene(MenuType.SETTINGS);
            } else {
                Util.showTooltip(playerName, TextFieldChecker.nameError);
            }
        }
    }

    /**
     * Modification des paramètres en mode solo.
     * @param event MouseEvent
     */
    @FXML
    void saveSolo(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (profilReset("Vous voulez changer les parammetres du profil","Valider")) {
                String oldName = DactyloClient.getClient().getName();
                UUID oldId = DactyloClient.getClient().getUUID();
                SettingsModel oldSettings = DactyloClient.getClient().getSettingsModel();
                Security.saveCrypt(true);
                DactyloClient.getClient().setUUID(oldId);
                DactyloClient.getClient().setName(oldName);
                SettingsModel settingsModel = DactyloClient.getClient().getSettingsModel();

                if (!nbrLife.getText().isEmpty() && Integer.parseInt(nbrLife.getText()) >= 2) {
                    settingsModel.setNbrLife(Integer.parseInt(nbrLife.getText()));
                }else{
                    settingsModel.setNbrLife(oldSettings.getNbrLife());
                }
                if (!timer.getText().isEmpty() && Integer.parseInt(timer.getText()) >= 20) {
                    settingsModel.setTimerStart(Integer.parseInt(timer.getText()));
                }else{
                    settingsModel.setTimerStart(oldSettings.getTimerStart());
                }
                if (!motLevel.getText().isEmpty() && Integer.parseInt(motLevel.getText()) >= 3) {
                    settingsModel.setMotLevel(Integer.parseInt(motLevel.getText()));
                }else{
                    settingsModel.setMotLevel(oldSettings.getMotLevel());
                }
                settingsModel.setWordToEnd(oldSettings.getWordToEnd());
                Security.saveCrypt(false);
                DactyloClient.setNewScene(MenuType.SETTINGS);
            }
        }
    }

    /**
     * Modification des paramètres en mode entraînement.
     * @param event MouseEvent
     * @throws IOException
     */
    @FXML
    void saveTrain(MouseEvent event) throws IOException {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (!wordEnd.getText().isEmpty()&& Integer.parseInt(wordEnd.getText()) >= 2) {
                DactyloClient.getClient().getSettingsModel().setWordToEnd(Integer.parseInt(wordEnd.getText()));
            }
            Security.saveCrypt(false);
            DactyloClient.setNewScene(MenuType.SETTINGS);
        }
    }

    private boolean profilReset(String title, String validName) {
        ButtonType supType = new ButtonType(validName, ButtonBar.ButtonData.YES);
        Alert alert = new Alert(Alert.AlertType.WARNING,
                "Cela supprimera toutes vos données:\n"
                        + " - Vos statistiques\n"
                        + " - Vos niveaux\n"
                        + " - Vos nombres de vies\n"
                        + " - Votre nom de joueur\n"
                        + " - Vos mots de passe\n",
                ButtonType.CANCEL, supType);
        alert.setTitle(title);
        alert.setHeaderText("Cette action est irréversible. Voulez-vous continuer ?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(ButtonType.CLOSE) == supType;
    }

}
