package fr.projet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import static fr.projet.DactyloGameServer.LOG;

/**
 * C'est une classe qui permet de gérer les mots
 */
public final class WordController {

    /**
     * Il supprime tous les signes de ponctuation et les accents d'une liste de chaînes
     *
     * @param words La liste des mots à normaliser.
     */
    public static void normalizeString(ArrayList<String> words) {
        words.replaceAll(word -> removeAccents(word).replaceAll("\\p{Punct}", ""));
    }

    /**
     * Il prend une chaîne, et supprime tous les caractères non ASCII
     *
     * @param word Le mot à normaliser.
     * @return Le mot sans accents.
     */
    private static String removeAccents(String word) {
        return Normalizer.normalize(word, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * Il lit un fichier, place chaque ligne dans un tableau, mélange le tableau et renvoie les 5000 premiers éléments du
     * tableau
     *
     * @param filename  le fichier à lire
     * @param normalize si vrai, les mots seront normalisés (voir ci-dessous)
     * @return Une ArrayList de String
     */
    public static ArrayList<String> getWordsList(InputStream filename, boolean normalize) {
        ArrayList<String> array = new ArrayList<>();
        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new InputStreamReader(filename));
            String line = reader.readLine();

            while (line != null) {
                array.add(line.toLowerCase());
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            if (!e.getMessage().equalsIgnoreCase("Stream closed")) {
                LOG.error("Impossible de lire le fichier de mots");
            }
            LOG.error(e.getMessage());
        }
        Collections.shuffle(array);
        array = array.stream().limit(5_000).collect(Collectors.toCollection(ArrayList::new));
        if (normalize) {
            normalizeString(array);
        }
        return array;
    }
}
