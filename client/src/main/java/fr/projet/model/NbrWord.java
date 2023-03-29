package fr.projet.model;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Classe contenant le nombre de mots corrects tapés.
 */
public final class NbrWord {


    private final SimpleIntegerProperty nbrWord = new SimpleIntegerProperty(this, "Nombre De mots", 0);

    /**
     * Le nombre de mots.
     *
     * @return La propriété nbrWord.
     */
    public SimpleIntegerProperty nbrWordProperty() {
        return nbrWord;
    }

    /**
     * Nombre de mots corrects.
     *
     * @return Entier correspondant au nombre de mots.
     */
    public int getNbrWord() {
        return nbrWord.get();
    }

    /**
     * Modifie le nombre de mots.
     *
     * @param n Nouveau nombre de mots.
     */
    public void setNbrWord(int n) {
        nbrWord.set(n);
        if (nbrWord.get() < 0) {
            nbrWord.set(0);
        }
    }

}
