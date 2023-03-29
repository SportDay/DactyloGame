package fr.projet;

import fr.projet.controller.CustomTimer;
import fr.projet.controller.RootMenu;
import fr.projet.model.Client;
import fr.projet.type.GameType;
import fr.projet.type.MenuType;
import fr.projet.util.Constants;
import fr.projet.util.Security;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Classe principale héritant de la classe Application.
 * @version 1.0
 * @see Application
 */
public final class DactyloClient extends Application {

    public static final Logger LOG = LogManager.getLogger();
    public static final double WIDTH = 700;
    public static final double HEIGHT = 900;
    private static final Security security = new Security();
    private static Stage primaryStage;
    private static RootMenu rootMenu;
    private static GameType gameType = GameType.NONE;
    private static Client client;

    /**
     * Mode de jeu.
     *
     * @return GameType correspondant au mode de jeu.
     */
    public static GameType getGameType() {
        return gameType;
    }

    /**
     * Modifie le mode de jeu.
     *
     * @param gameType Nouveau mode de jeu.
     */
    public static void setGameType(GameType gameType) {
        DactyloClient.gameType = gameType;
    }

    /**
     * Client du jeu.
     *
     * @return Client correspondant au client.
     */
    public static Client getClient() {
        return client;
    }

    /**
     * Modifie client.
     *
     * @param client Nouvelle valeur de client.
     */
    public static void setClient(Client client) {
        DactyloClient.client = client;
    }

    /**
     * Fenêtre du jeu.
     *
     * @return Stage correspondant à la fenêtre du jeu.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Lancement de l'application.
     *
     * @param args
     */
    public static void initApp(String[] args) {
        initFolder();
        launch(args);
    }

    /**
     * Modifie la scène.
     *
     * @param game  Menu du jeu.
     * @param train Mode de jeu.
     * @throws IOException
     */
    public static void setNewScene(MenuType game, GameType train) throws IOException {
        setGameType(train);
        setNewScene(game);
        setMenuTitle(gameType.name);
    }

    /**
     * Modifie la scène.
     *
     * @param menuType Menu du jeu.
     * @throws IOException
     */
    public static void setNewScene(MenuType menuType) throws IOException {
        rootMenu.setNewScene(menuType);
    }

    /**
     * Créé le fichier .DactyloClient dans le dossier user
     */
    private static void initFolder() {
        if (!new File(Constants.PATH).exists()) {
            new File(Constants.PATH).mkdir();
        }
    }

    /**
     * Modifie le titre du menu.
     *
     * @param title Nouveau titre.
     */
    public static void setMenuTitle(String title) {
        rootMenu.setMenuTitle((rootMenu.getMenuTitle() + " - " + title));
    }

    /**
     * Ferme le jeu.
     */
    public static void stopApp() {
        Security.saveCrypt(false);
        CustomTimer.stopTimer();

        Platform.exit();
        System.exit(0);
    }

    /**
     * Mise à jour de UUID.
     */
    public static void updateUUID() {
        rootMenu.updateUUID();
    }

    /**
     * Création de l'interface graphique.
     */
    @Override
    public void start(Stage primaryStage) throws IOException, InterruptedException {
        if (DactyloClient.primaryStage == null) {
            DactyloClient.primaryStage = primaryStage;
        }
        Security.openCrypt();
        if (client == null) {
            Security.saveCrypt(true);
        }
        primaryStage.setTitle("DactyloGame");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("image/logo.png"))));
        primaryStage.setFullScreenExitHint("Pour sortir du plein écran appuyer sur F11.");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        FXMLLoader loader = new FXMLLoader();
        Parent tmp1 = loader.load(getClass().getResourceAsStream(MenuType.ROOT.getFxmlPathForStream()));
        Scene scene = new Scene(tmp1);
        scene.setFill(Color.TRANSPARENT);

        rootMenu = loader.getController();
        rootMenu.setNewScene(MenuType.MAIN);

        primaryStage.setMinHeight(700);
        primaryStage.setMinWidth(900);
        primaryStage.setScene(scene);
        primaryStage.show();

        tmp1.requestFocus();

        rootMenu.setOldBounds(new Rectangle2D(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight()));
        security.checkOk();

        if (client.isFirstTime()) {
            rootMenu.setNewScene(MenuType.FIRST);
        }
    }
}
