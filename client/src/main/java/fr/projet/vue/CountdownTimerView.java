package fr.projet.vue;

import fr.projet.model.CountdownTimer;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Vue de la classe CountdownTimer.
 */
public class CountdownTimerView extends HBox {

    private static final Color BLUE = Color.rgb(128, 203, 196);
    private final CountdownTimer model;
    private final Text time;

    public CountdownTimerView(CountdownTimer model) {
        this.model = model;
        time = new Text(model.getSeconds() + "");
        time.setFont(Font.font("System Bold", FontWeight.BOLD, 20));
        time.setFill(BLUE);
        getChildren().add(time);
        setAlignment(Pos.CENTER);
    }

    /**
     * Met à jour l'affichage du timer.
     */
    public void update() {
        int seconds = model.getSeconds();
        if (seconds < 30 && seconds > 15) {
            time.setFill(Color.ORANGE);
        } else if (seconds <= 15 && seconds >= 0) {
            time.setFill(Color.RED);
        } else {
            time.setFill(BLUE);
        }
        time.setText(seconds + "");
    }

}
