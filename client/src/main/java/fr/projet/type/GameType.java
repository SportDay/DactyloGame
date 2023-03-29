package fr.projet.type;

/**
 * Mode de jeu.
 */
public enum GameType {
    LOCAL("Solo"),
    MULTI("Multijoueur"),
    TRAIN("Entrainement"),
    NONE("Aucun");

    public final String name;

    /**
     * Constructeur
     * @param name nom du mode de jeu
     */
    GameType(String name) {
        this.name = name;
    }

}
