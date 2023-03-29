package fr.projet;

import org.junit.jupiter.api.Test;

import fr.projet.model.Life;
import fr.projet.model.NbrWord;
import fr.projet.type.GameType;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordsTest {

    Words words = new Words(ClassLoader.getSystemResourceAsStream("source/wordsList.txt"), GameType.LOCAL);
    Life life = new Life();
    NbrWord nbrWord = new NbrWord();
    
    WordsTest() {
        words.setLife(life);
        words.setNbrWord(nbrWord);
    }

    /**
     * Test pour la vérification d'une lettre correcte.
     */
    @Test
    public void testCorrectLetter() {
        char[] letters = words.getLetters();
        words.setCheck(letters[0] + "");
        assertEquals(true, words.checkLetter());
    }

    /**
     * Test pour la vérification d'une lettre incorrecte.
     */
    @Test
    public void testIncorrectLetter() {
        char[] letters = words.getLetters();
        words.setCheck("A");
        assertEquals(false, words.checkLetter());
    }

    /**
     * Test pour la vérification d'un mot correct.
     */
    @Test
    public void testCorrect() {
        char[] letters = words.getLetters();
        for (int i = 0; i < letters.length - 1; i++) {
            words.setCheck(letters[i] + "");
            words.checkLetter();
        }
        assertEquals(true, words.check(false, null));
    }

    /**
     * Test pour la vérification d'un mot dont toute les lettres sont incorrectes.
     */
    @Test
    public void testAllIncorrect() {
        char[] letters = words.getLetters();
        for (int i = 0; i < letters.length - 1; i++) {
            words.setCheck("A");
            words.checkLetter();
        }
        assertEquals(false, words.check(false, null));
    }

    /**
     * Test pour la vérification d'un mot dont une lettre sur deux est incorrect.
     */
    @Test
    public void testSomeIncorrectLetters() {
        char[] letters = words.getLetters();
        for (int i = 0; i < letters.length - 1; i++) {
            if (i % 2 == 0) {
                words.setCheck(letters[i] + "");
            }
            else {
                words.setCheck("A");
            }
            words.checkLetter();
        }
        assertEquals(false, words.check(false, null));
    }

    /**
     * Test pour la vérification d'un mot vide.
     */
    @Test
    public void testEmpty() {
        words.setCheck("");
        words.checkLetter();
        assertEquals(false, words.check(false, null));
    }

    /**
     * Test pour la vérification d'un mot incomplet où il manque les deux dernières lettres.
     */
    @Test
    public void testIncorrectMissingLastTwoLetters() {
        char[] letters = words.getLetters();
        for (int i = 2; i < letters.length - 1; i++) {
            words.setCheck(letters[i - 2] + "");
            words.checkLetter();
        }
        assertEquals(false, words.check(false, null));
    }
    
    /**
     * Test pour la vérification d'un mot d'abord incorrect puis correct.
     */
    @Test
    public void testIncorrectThenCorrect() {
        char[] letters = words.getLetters();
        words.setCheck("");
        words.checkLetter();
        words.check(false, null);
        for (int i = 0; i < letters.length - 1; i++) {
            words.setCheck(letters[i] + "");
            words.checkLetter();
        }
        assertEquals(true, words.check(false, null));
    }

    /**
     * Test pour l'ajout de vie en cas de mots bonus.
     */
    @Test
    public void testGetLifeBonus() {
        if (words.isBonus(0)) {
            char[] letters = words.getLetters();
            for (int i = 0; i < letters.length - 1; i++) {
                words.setCheck(letters[i] + "");
                words.checkLetter();
            }
            int val = life.getLife() + (letters.length - 1);
            words.check(false, null);
            assertEquals(val, life.getLife());
        }
    }

    /**
     * Test pour la perte de vie.
     */
    @Test
    public void testLoseLife() {
        char[] letters = words.getLetters();
        words.setCheck("");
        words.checkLetter();
        int val = life.getLife() - words.getWords().get(0).length();
        if (val < 0) {
            val = 0;
        }
        words.check(false, null);
        assertEquals(val, life.getLife());
    }

    /**
     * Test pour le gain d'un point avec un mot correct.
     */
    @Test
    public void testGetPoint() {
        char[] letters = words.getLetters();
        for (int i = 0; i < letters.length - 1; i++) {
            words.setCheck(letters[i] + "");
            words.checkLetter();
        }
        int val = nbrWord.getNbrWord() + 1;
        words.check(false, null);
        assertEquals(val, nbrWord.getNbrWord());
    }

    /**
     * Test pour le non gain de points avec un mot incorrect.
     */
    @Test
    public void testNoPoint() {
        char[] letters = words.getLetters();
        words.setCheck("");
        words.checkLetter();
        int val = nbrWord.getNbrWord();
        words.check(false,null);
        assertEquals(val, nbrWord.getNbrWord());
    }

}
