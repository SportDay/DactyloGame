package fr.projet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import fr.projet.controller.GameMenu;
import fr.projet.model.Life;
import fr.projet.model.NbrWord;
import fr.projet.serverComunication.Message;
import fr.projet.serverComunication.ServerClient;
import fr.projet.type.GameType;
import fr.projet.type.MessageType;
import fr.projet.util.Constants;

/**
 * Génère et gère les mots à écrire.
 */
public final class Words {

    private ArrayList<String> allWords = new ArrayList<>(); // liste de tous les mots
    private ArrayList<String> words = new ArrayList<>();    // liste des mots a taper
    private ArrayList<Boolean> bonus = new ArrayList<>();   // mot bonus ou non
    private char[] letters;                                 // mot a taper
    private char[] check;                                   // mot tape
    private int index = 0;                                  // indice du caractere a taper dans le mot a taper
    private int correctLetters = 0;                         // nombre de lettres corrects dans check
    private Life life;
    private NbrWord nbrWord;
    private boolean gameOver = false;
    private GameMenu gameMenu;

    private final GameType gameType;                        // mode de jeu

    /**
     * Contructeur de la classe.
     *
     * @param filename Nom du fichier contenant les mots.
     * @param gameType Mode de jeu.
     */
    public Words(InputStream filename, GameType gameType) {
        allWords = toList(filename);
        Collections.shuffle(allWords);
        initFirstList(); // words
        this.letters = toCharArray();
        check = new char[words.get(0).length() + 1];
        this.gameType = gameType;
    }
    /**
     * Contructeur de la classe.
     *
     * @param words Liste des mots.
     * @param gameType Mode de jeu.
     */
    public Words(ArrayList<String> words, GameType gameType) {
        allWords = new ArrayList<>(words);
        initFirstList(); // words
        this.letters = toCharArray();
        check = new char[this.words.get(0).length() + 1];
        this.gameType = gameType;
    }

    /**
     * Créé une ArrayList dont chaque élément de la liste est un mot du fichier filename.
     *
     * @param filename Nom du fichier contenant les mots.
     * @return ArrayList contenant tous les mots du fichier filename.
     */
    private ArrayList<String> toList(InputStream filename) {
        ArrayList<String> array = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(filename));
            String line = reader.readLine();
            while (line != null) {
                array.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    /**
     * Un mot aléatoire parmi tous les mots possibles.
     *
     * @return Mot aléatoire.
     */
    private String getRandomWord() {
        double rand = Math.random() * allWords.size();
        return allWords.get((int) rand);
    }

    /**
     * Ajoute un mot dans la liste des mots à taper.
     */
    private void addWord() {
        String s = getRandomWord();
        words.add(s);
        if (s.length() > 6) {
            double r = Math.random() * s.length();
            if (r < s.length() / 4d) {
                bonus.add(true);
            } else {
                bonus.add(false);
            }
        } else {
            bonus.add(false);
        }
    }

    /**
     * Ajoute LIST_MAX_SIZE mots aléatoires dans la liste des mots à taper.
     */
    private void initFirstList() {
        for (int i = 0; i < Constants.LIST_MAX_SIZE; i++) {
            addWord();
        }
    }

    /**
     * Transforme une chaîne de caratères en tableau de caractères en ajoutant
     * un caractère espace à la fin.
     *
     * @return Premier mot de la liste de mots à taper sous forme de tableau de caractères.
     */
    private char[] toCharArray() {
        int len = words.get(0).length();
        char[] array = new char[len + 1];
        for (int i = 0; i < len; i++) {
            array[i] = words.get(0).charAt(i);
        }
        array[len] = ' ';
        return array;
    }

    /**
     * Met à jour la liste des mots à taper.
     * Supprime le premier mot et en ajoute un nouveau si la taille de la liste
     * est inférieure à la moitié de LIST_MAX_SIZE.
     */
    private void updateWordsList() {
        words.remove(0);
        bonus.remove(0);
        if (GameType.TRAIN == DactyloClient.getGameType()) {
            addWord();
        } else if (words.size() < Constants.LIST_MAX_SIZE * 0.5) {
            addWord();
        }
        check = new char[words.get(0).length() + 1];
    }

    /**
     * Vérifie si la lettre tapée est correcte.
     *
     * @return true si la lettre est correcte, false sinon.
     */
    public boolean checkLetter() {
        if (index < letters.length) {
            index++;
        }
        if (words.get(0).charAt(index - 1) == check[index - 1]) {
            correctLetters++;
            return true;
        }
        return false;
    }





    /**
     * Vérifie si le mot tapé est correct.
     *
     * @param timeout Boolean indiquant dans quel condition faire la vérification.
     * @param word Pour le mode multijoueur, mot envoyé par l'adversiare.
     * @return true si le mot est correct, false sinon.
     */
    public boolean check(boolean timeout, String word) {
        if(word == null) {
            index = 0;
        }
        String str = words.get(0);
        int len = str.length();
        int errors = 0;
        for (int i = 0; i < len; i++) {
            if (words.get(0).charAt(i) != check[i]) {
                errors++;
            }
        }

        if (errors > 0) {
			if(GameType.MULTI != DactyloClient.getGameType() || word == null || words.size() == Constants.LIST_MAX_SIZE) {
                if((!timeout && word == null) ||  words.size() == Constants.LIST_MAX_SIZE) {
                    if (gameType != GameType.TRAIN) {
                        life.setLife(life.getLife() - errors);
                        if (life.getLife() == 0) {
                            gameOver = true;
                            if(gameMenu != null) {
                                gameMenu.endSession(DactyloClient.getGameType() == GameType.MULTI);
                            }
                        }
                    }
                }
				bonus.set(0, false);
				correctLetters = 0;
				if (timeout) {
					addWhenTimeOut(errors);
				}else if (word != null){
                    addStrToWordsList(word,errors);
                }
				return false;
			}
        }

        if (gameType == GameType.LOCAL) {
            if (bonus.get(0)) {
                life.setLife(life.getLife() + correctLetters);
            }
        }
        if (gameType == GameType.MULTI) {
            if (bonus.get(0)) {
                Message msg = new Message(DactyloClient.getClient(), MessageType.GAME_SPECIAL, str);
                if (ServerClient.getINSTANCE() != null) {
                    ServerClient.getINSTANCE().sendMessage(msg);
                }
            }
        }
        if (timeout) {
            addWhenTimeOut(errors);
        }else if (word != null) {
            addStrToWordsList(word,errors);
        } else {
            nbrWord.setNbrWord(nbrWord.getNbrWord() + 1);
            correctLetters = 0;
            updateWordsList();
            letters = toCharArray();
        }
        return true;
    }


    /**
     * Ajoute un mot à la liste des mots en force la validation du mot à taper si la liste est pleine.
     */
    private void addWhenTimeOut(int error) {
        if (GameType.LOCAL == DactyloClient.getGameType()) {
            removeFirstWord(error);
            addWord();
        }
    }

    /**
     * Si la liste est pleine alors on met a jour la liste des mots à taper.
     *
     * @param error nombre d'erreur
     */
    private void removeFirstWord(int error) {
        if (words.size() == Constants.LIST_MAX_SIZE) {
            words.remove(0);
            bonus.remove(0);
            check = new char[words.get(0).length() + 1];
            letters = toCharArray();
            if(gameMenu != null) {
                gameMenu.addPressed(error);
            }
            index = 0;
        }
    }

    /**
     * Ajoute le mot str à la liste des mots en force la validation du mot à taper si la liste est pleine.
     *
     * @param str Mot à ajouter.
     * @param error Nombre d'erreur dans le mot tapé.
     */
    public void addStrToWordsList(String str,int error) {
        if (GameType.MULTI == DactyloClient.getGameType()) {
            removeFirstWord(error);
            words.add(str);
            bonus.add(false);
        }
    }

    /**
     * Mot à taper.
     *
     * @return Tableau de chaîne de caractères contenant le mot à taper.
     */
    public char[] getLetters() {
        return letters;
    }

    /**
     * Etat de la partie.
     *
     * @return true si la partie est terminée, false sinon.
     */
    public boolean gameOver() {
        return gameOver;
    }

    /**
     * Modifie l'état de la partie.
     *
     * @param gameOver Nouvelle état de la partie.
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if(gameOver) {
            gameMenu.endSession(DactyloClient.getGameType() == GameType.MULTI);
        }
    }

    /**
     * Liste des mots à taper.
     *
     * @return ArrayList contenant les mots à taper.
     */
    public ArrayList<String> getWords() {
        return words;
    }

    /**
     * Indique si un mot est un bonus.
     *
     * @param i Indice du mot.
     * @return true si le mot à l'indice i est un bonus, false sinon.
     */
    public boolean isBonus(int i) {
        return bonus.get(i);
    }

    /**
     * Modifie le dernier caractère tapé.
     *
     * @param letter Nouvelle lettre.
     */
    public void setCheck(String letter) {
        if (letter.equals("")) {
            this.check[index] = 0;
        } else {
            this.check[index] = letter.charAt(0);
        }
    }

    /**
     * Indice de la lettre à taper
     *
     * @return Entier entre 0 et la taille du mot à taper.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Modifie l'indice de la lettre à taper.
     *
     * @param index Nouvel indice.
     */
    public void setIndex(int index) {
        if (index < words.get(0).length()) {
            this.index = index;
            if (this.index < 0) {
                this.index = 0;
            }
        }
    }

    /**
     * Définie la variable life.
     *
     * @param life Valeur de life.
     */
    public void setLife(Life life) {
        this.life = life;
    }

    
    /**
     * Définie la valeur de la variable nbrWord.
     * 
     * @param nbrWord Valeur de nbrWord.
     */
    public void setNbrWord(NbrWord nbrWord) {
        this.nbrWord = nbrWord;
    }

    /**
     * Définit la variable gameMenu.
     * 
     * @param gameMenu Valeur de gameMenu.
     */
    public void setGameMenu(GameMenu gameMenu) {
        this.gameMenu = gameMenu;
    }

}
