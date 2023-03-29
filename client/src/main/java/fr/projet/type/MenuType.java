package fr.projet.type;

/**
 * Type du menu.
 *
 */
public enum MenuType {
    ROOT("ROOT", "fxml/rootMenu.fxml"),
    MAIN("Menu Principale", "fxml/MainMenu.fxml"),
    MULTICHOICE("Menu de connexion", "fxml/MultiChoice.fxml"),
    STATS("Statistiques", "fxml/StasMenu.fxml"),
    SETTINGS("Paramètre", "fxml/SettingsMenu.fxml"),
    FIRST("Démarrage", "fxml/FirstLogin.fxml"),
    GAME("Jeu", "fxml/GameMenu.fxml");


    public final String name;
    public final String fxmlPath;

    /**
     * Constructeur
     * @param s nom du menu
     * @param fxmlPath chemin du fichier fxml
     */
    MenuType(String s, String fxmlPath) {
        name = s;
        this.fxmlPath = fxmlPath;
    }

    public String getFxmlPathForStream() {
        return "/" + fxmlPath;
    }

    @Override
    public String toString() {
        return name;
    }
}
