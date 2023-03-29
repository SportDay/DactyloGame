package fr.projet.controller;

import fr.projet.DactyloClient;
import fr.projet.Words;
import fr.projet.model.CountdownTimer;
import fr.projet.type.GameType;
import fr.projet.util.Constants;
import fr.projet.vue.CountdownTimerView;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Contrôleur de la classe CountdownTimer.
 */
public final class CustomTimer {

    private static Timer timer;
    private final CountdownTimer model = new CountdownTimer();
    private final CountdownTimerView view = new CountdownTimerView(model);
    private final GameMenu game;
    private final Words words;

    public CustomTimer(Words words, GameMenu game) {
        super();
        this.game = game;
        this.words = words;
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        timer = new Timer();
    }

    /**
     * Arrête le minuteur.
     */
    public static void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = new Timer();
        }
    }

    /**
     * Démarre le minuteur.
     */
    public void startTimer() {
        stopTimer();
        model.restart();
        double start = (int) (Constants.timerCoef * Math.pow(0.9, DactyloClient.getClient().getLevel()));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (!words.gameOver()) {
                        if (model.getSeconds() == -1) {
                            int len = words.getWords().size();
                            words.check(true, null);
                            game.updateView(len != words.getWords().size(),false);
                            startTimer();
                        } else {
                            view.update();
                            model.setSeconds(model.getSeconds() - 1);
                        }
                    } else {
                        game.endSession(DactyloClient.getGameType() == GameType.MULTI);
                    }
                });

            }
        }, (int) start, (int) start);
    }

    /**
     * Model du minuteur.
     *
     * @return CountdownTimer
     */
    public CountdownTimer getModel() {
        return model;
    }

    /**
     * Vue du minuteur.
     *
     * @return CountdownTimerView
     */
    public CountdownTimerView getView() {
        return view;
    }
}
