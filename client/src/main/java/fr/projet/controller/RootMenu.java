package fr.projet.controller;

import fr.projet.DactyloClient;
import fr.projet.serverComunication.ServerClient;
import fr.projet.type.GameType;
import fr.projet.type.MenuType;
import fr.projet.util.Security;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class RootMenu {

    private final Image maximazeIco = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/maximaze.png")));
    private final Image restoreIco = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/restore.png")));
    private boolean maximized = true;
    private Rectangle2D oldBounds;
    private double xOffset = 0, yOffset = 0;
    private Stage stage;

    @FXML
    private Button closeBtn;
    @FXML
    private Button hideBtn;
    @FXML
    private Button maxMinBtn;
    @FXML
    private ImageView minMaxIcon;
    @FXML
    private Button menuBTN;
    @FXML
    private Label menuTitle;
    @FXML
    private Label rootTitle;
    @FXML
    private GridPane topBar;
    @FXML
    private VBox mainPanel;

    @FXML
    private Label uuidField;

    @FXML
    private StackPane stackPane;

    /**
     * Initialisation du menu.
     */
    @FXML
    private void initialize() {
        stage = DactyloClient.getPrimaryStage();
        rootTitle.setVisible(false);
        menuTitle.setText(MenuType.MAIN.toString());
        stackPane.setPrefSize(900, 700);
        uuidField.setText("UUID: " + DactyloClient.getClient().getUUID());

        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (maximized) {
                mainPanel.getStyleClass().removeAll("fullscreen");
                minMaxIcon.setImage(maximazeIco);
                maximized = false;
            } else {
                mainPanel.getStyleClass().add("fullscreen");
                minMaxIcon.setImage(restoreIco);
                maximized = true;
            }
        });

    }

    /**
     * Met à jour le champ UUID dans l'interface graphique
     */
    public void updateUUID() {
        uuidField.setText("UUID: " + DactyloClient.getClient().getUUID());
    }

    /**
     * Modifie oldBounds.
     *
     * @param oldBounds Nouvelle valeur.
     */
    public void setOldBounds(Rectangle2D oldBounds) {
        this.oldBounds = oldBounds;
    }

    /**
     * Modifie la scène.
     *
     * @param type Type de menu.
     */
    public void setNewScene(MenuType type) throws IOException {
        Node node = FXMLLoader.load(ClassLoader.getSystemResource(type.fxmlPath));
        stackPane.getChildren().removeAll(stackPane.getChildren());
        stackPane.getChildren().setAll(node);
        if (type == MenuType.MAIN) {
            menuBTN.setVisible(false);
            rootTitle.setVisible(false);
            menuTitle.setText(type.toString());
        } else {
            rootTitle.setVisible(true);
            menuTitle.setText(type.toString());
            if (type != MenuType.FIRST) {
                menuBTN.setVisible(true);
            }
        }
        updateUUID();
    }

    /**
     * Ferme le jeu.
     *
     * @param event MouseEvent
     */
    @FXML
    void closeWindow(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            DactyloClient.stopApp();
        }
    }

    /**
     * Cache la fenêtre de jeu.
     *
     * @param event MouseEvent
     */
    @FXML
    void hideWindow(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            stage.setIconified(true);
        }
    }

    /**
     * Modifie la taille de la fenêtre de jeu.
     *
     * @param event MouseEvent
     */
    @FXML
    void maxMinWin(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (maximized) {
                changeScale(oldBounds);
            } else {
                oldBounds = (new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()));
                ObservableList<Screen> screens = Screen.getScreensForRectangle(oldBounds);
                changeScale(screens.get(0).getVisualBounds());
            }
        }
    }

    /**
     * Change la taille de stage.
     *
     * @param bounds
     */
    private void changeScale(Rectangle2D bounds) {
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }

    /**
     * Action à faire si il y'a un clique de la souris.
     *
     * @param event MouseEvent
     */
    @FXML
    void mousePressed(MouseEvent event) {
        if (maximized) {
            xOffset = (stage.getX()) - event.getScreenX() / 2;
        } else {
            xOffset = stage.getX() - event.getScreenX();
        }
        yOffset = stage.getY() - event.getScreenY();
    }

    /**
     * Action à faire si il y'a un déplacement de la fenêtre avec la souris.
     *
     * @param event MouseEvent
     */
    @FXML
    void mousseDragged(MouseEvent event) {
        if (!stage.isFullScreen()) {
            if (maximized) {
                changeScale(oldBounds);
            }
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        }
    }

    /**
     * Met la fenêtre de jeu en plein écran.
     *
     * @param event MouseEvent
     */
    @FXML
    void setFullScreen(KeyEvent event) {
        if (event.getCode() == KeyCode.F11) {
            stage.setFullScreen(!stage.isFullScreen());
            if (stage.isFullScreen()) {
                mainPanel.getStyleClass().add("fullscreen");
                maxMinBtn.setVisible(false);
                hideBtn.setVisible(false);
            } else {
                mainPanel.getStyleClass().removeAll("fullscreen");
                maxMinBtn.setVisible(true);
                hideBtn.setVisible(true);
            }
        }
    }

    /**
     * Affichage du menu.
     *
     * @param event MouseEvent
     */
    @FXML
    void setMenu(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            try {
                if (DactyloClient.getClient().isFirstTime()) {
                    setNewScene(MenuType.FIRST);
                } else {
                    setNewScene(MenuType.MAIN);
                }
                CustomTimer.stopTimer();
                if (ServerClient.getINSTANCE() != null) {
                    ServerClient.getINSTANCE().disconnect(true);
                }
                DactyloClient.setGameType(GameType.NONE);
                if (DactyloClient.getGameType() == GameType.LOCAL) {
                    DactyloClient.getClient().getTmpStats().updateTotalTime();
                }
                Security.saveCrypt(false);
                DactyloClient.getPrimaryStage().getScene().setOnKeyReleased(null);
            } catch (IOException e) {
            }
        }
    }

    /**
     * Titre du menu.
     *
     * @return Chaîne de caractères correspondant au titre du menu.
     */
    public String getMenuTitle() {
        return menuTitle.getText();
    }

    /**
     * Modifie le titre du menu.
     *
     * @param title
     */
    public void setMenuTitle(String title) {
        menuTitle.setText(title);
    }
}
