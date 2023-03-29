package fr.projet.vue;

import fr.projet.model.Life;
import javafx.scene.control.Label;

/**
 * Vue de la classe Life.
 */
public class LifeView extends Label {

    private final Life model;

    public LifeView(Life model) {
        this.model = model;
        getStylesheets().add(ClassLoader.getSystemResource("css/gameMenu.css").toExternalForm());
        getStylesheets().add(ClassLoader.getSystemResource("css/all.css").toExternalForm());
        getStyleClass().add("cText");
        getStyleClass().add("life");
        update();
    }

    /**
     * Met à jour l'affichage des vies.
     */
    public void update() {
        setText("" + model.getLife());
    }

}
