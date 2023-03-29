package fr.projet.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.LinkedList;

public final class UtilWords {

    // Nombre de mots util
    private final SimpleIntegerProperty utilsWords = new SimpleIntegerProperty(this, "Mot Utile", 0);

    private final SimpleDoubleProperty regularite = new SimpleDoubleProperty(this, "Precision", 0);

    // Liste des mots utiles, avec leur nombre d'utilisations
    private final HashMap<Text, Integer> wordList = new HashMap<>();
    // Liste des mots utiles, avec leur temps de la première utilisation
    private final HashMap<Text, Long> timeList = new HashMap<>();

    private final HashMap<Integer, Text> index = new HashMap<>();

    // Liste contenant le temps entre chaque utilisation
    private final LinkedList<Long> time = new LinkedList<>();

    int i = 0;


    public void addWord(Text word) {
        wordList.put(word, wordList.getOrDefault(word, 0) + 1);
        timeList.putIfAbsent(word, System.nanoTime());
        index.put(i++, word);
    }

    // Calcul de la précision et du nombre de mots utiles
    public void computeWords() {
        int f = 0;
        for (var entry : wordList.entrySet()) {
            if (entry.getValue() == 1) {
                f++;
            }
        }
        utilsWords.set(utilsWords.get() + f);
        for (int i = 0; i < timeList.size() - 1; i++) {
            if (wordList.get(index.get(i)) == 1 && wordList.get(index.get(i + 1)) == 1) {
                long currentTime = timeList.get(index.get(i));
                long nextTime = timeList.get(index.get(i + 1));
                long timeDifference = nextTime - currentTime;
                time.add(timeDifference);
            }
        }

        long sum = time.stream().reduce(0L, Long::sum);
        double mean = (double) sum / time.size();

        double variance = 0;
        for (long timeDifference : time) {
            variance += Math.pow(timeDifference - mean, 2);
        }
        double tmp = Math.sqrt(variance / (time.size()));
        double precision = tmp / 10_000_000d;
        if(!Double.isNaN(precision)){
            regularite.set(precision);
        }else{
            regularite.set(0);
        }

        wordList.clear();
        timeList.clear();
        index.clear();
        i = 0;
        time.removeAll(time);
    }

    public SimpleIntegerProperty utilsWordsProperty() {
        return utilsWords;
    }

    public SimpleDoubleProperty regulariteProperty() {
        return regularite;
    }

    public int getUtilsWords() {
        return utilsWords.get();
    }
}
