package fr.projet.model;

import fr.projet.DactyloClient;

/**
 * Implémentation d'un minuteur.
 */
public final class CountdownTimer {

    private int start = DactyloClient.getClient().getSettingsModel().getTimerStart();
	private int seconds = start;

    /**
     * Recommence le minuteur.
     * Met à jour la valeur de début du timer si besoin.
     */
    public void restart() {
        start = DactyloClient.getClient().getSettingsModel().getTimerStart();
        seconds = start;
    }


    /**
     * Secondes restantes.
     *
     * @return Entier correspondant aux secondes restantes.
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Modifie le temps restant du minuteur.
     *
     * @param seconds Nouvelle valeur.
     */
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

}
