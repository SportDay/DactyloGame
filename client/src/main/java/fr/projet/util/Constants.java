package fr.projet.util;

import java.io.File;

/**
 * Fichier contenant les constantes du projet.
 */
public class Constants {

    public static final String PATH = System.getProperty("user.home") + File.separator + ".DactyloClient" + File.separator;
    public static final int timerCoef = 150;
    public static final int LIST_MAX_SIZE = 16;// nombre de mots dans la liste

    public static final int nbrLife = 100;
    public static final int timerStart = 300;
    public static final int wordToEnd = 100;
    public static final int motLevel = 100;
}
