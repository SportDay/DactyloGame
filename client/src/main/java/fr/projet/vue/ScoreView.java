package fr.projet.vue;

import fr.projet.model.NbrWord;
import javafx.scene.control.Label;

/**
 * Vue de la classe NbrWord.
 */
public class ScoreView extends Label {

    private final NbrWord model;

    public ScoreView(NbrWord model) {
        this.model = model;
        getStylesheets().add(ClassLoader.getSystemResource("css/gameMenu.css").toExternalForm());
        getStylesheets().add(ClassLoader.getSystemResource("css/all.css").toExternalForm());
        getStyleClass().add("cText");
        getStyleClass().add("score");
        update();
    }

    /**
     * Met à jour l'affichage.
     */
    public void update() {
        setText("" + model.getNbrWord());
    }

}
