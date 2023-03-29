package fr.projet.model;

import fr.projet.DactyloClient;
import fr.projet.util.Constants;

/**
 * Classe stockant les paramètres du jeu.
 */
public final class SettingsModel {
    private int nbrLife = Constants.nbrLife;
    private int timerStart = Constants.timerStart;
    private int wordToEnd = Constants.wordToEnd;
    private int motLevel = Constants.motLevel;

    /**
     * Nombre de vies.
     *
     * @return Le nombre de vies que possède le joueur.
     */
    public int getNbrLife() {
        return nbrLife;
    }

    /**
     * Modifie le nombre de vies.
     *
     * @param nbrLife Nouvelle valeur
     */
    public void setNbrLife(int nbrLife) {
        this.nbrLife = nbrLife;
        DactyloClient.getClient().setLife(nbrLife);
    }

    /**
     * Valeur de timerStart.
     *
     * @return Valeur de timerStart.
     */
    public int getTimerStart() {
        return timerStart;
    }

    /**
     * Modifie timerStart
     *
     * @param timerStart Temps de début du minuteur.
     */
    public void setTimerStart(int timerStart) {
        this.timerStart = timerStart;
    }

    /**
     * Nombre de mots nécessaire pour monter d'un niveau.
     *
     * @return Nombre de mots.
     */
    public int getMotLevel() {
        return motLevel;
    }

    /**
     * Modifie nombre de mots nécessaire pour monter d'un niveau.
     *
     * @param motLevel Nouvelle valeur.
     */
    public void setMotLevel(int motLevel) {
        this.motLevel = motLevel;
    }

    /**
     * Nombre de mots nécessaire pour terminer le niveau (en mode entraînement).
     *
     * @return Nombre de mots.
     */
    public int getWordToEnd() {
        return wordToEnd;
    }

    /**
     * Modifie nombre de mots nécessaire pour terminer le niveau.
     *
     * @param wordToEnd Nouvelle valeur.
     */
    public void setWordToEnd(int wordToEnd) {
        this.wordToEnd = wordToEnd;
    }
}
