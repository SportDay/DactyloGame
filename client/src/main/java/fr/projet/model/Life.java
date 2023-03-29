package fr.projet.model;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Classe implémentant les vies.
 */
public final class Life {


    private final SimpleIntegerProperty life = new SimpleIntegerProperty(this, "Vie", 5);

    /**
     * Nombre de vie.
     *
     * @return SimpleIntegerProperty.
     */
    public SimpleIntegerProperty lifeProperty() {
        return life;
    }

    /**
     * Nombre de vies restants.
     *
     * @return Entier correspondant au nombre de vies restants.
     */
    public int getLife() {
        return life.get();
    }

    /**
     * Modifie le nombre de vies restants.
     *
     * @param n Nouveau nombre de vies restants.
     */
    public void setLife(int n) {
        life.set(n);
        if (life.get() < 0) {
            life.set(0);
        }
    }

}
