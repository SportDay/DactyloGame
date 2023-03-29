package fr.projet.controller;

import fr.projet.DactyloClient;
import fr.projet.Words;
import fr.projet.model.Life;
import fr.projet.model.NbrWord;
import fr.projet.model.SettingsModel;
import fr.projet.model.UtilWords;
import fr.projet.serverComunication.ServerClient;
import fr.projet.type.GameType;
import fr.projet.util.Constants;
import fr.projet.util.Security;
import fr.projet.vue.LifeView;
import fr.projet.vue.ScoreView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static fr.projet.DactyloClient.LOG;

/**
 * Classe utilisée pour créer un menu de jeu.
 */
public class GameMenu {

    private static final Color DEFAULT = Color.GAINSBORO;
    private static final Color ERROR = Color.RED;
    private static final Color WRONG = Color.SALMON;
    private static final Color CORRECT = Color.rgb(100, 100, 100);
    private static final Color NEXT = Color.rgb(250, 200, 0);
    private static final Color BONUS = Color.DEEPSKYBLUE;
    private static final Color MULTI_BONUS = Color.web("#b71c1c");
    private static UtilWords utilWords;
    private static int maxWordsSession = 20;
    DecimalFormat df = new DecimalFormat("#.##");
    @FXML
    private FlowPane currentTextContainer;
    @FXML
    private GridPane infoContainer;
    @FXML
    private AnchorPane mainPanel;
    @FXML
    private VBox mainContainer;
    @FXML
    private Label precisionText;
    @FXML
    private Label regularityText;
    @FXML
    private Label speedText;
    @FXML
    private HBox statsContainer;
    @FXML
    private FlowPane wordListContainer;
    @FXML
    private TextArea playerList;
    @FXML
    private TextArea logsArea;
    @FXML
    private VBox playerListContainer;
    @FXML
    private VBox logsContainer;
    @FXML
    private Label f1Grid;
    @FXML
    private Label levelLabel;
    private Life life;
    private LifeView lifeView;
    private NbrWord nbrWord;
    private ScoreView scoreView;
    private Words words;
    private int pressed = 0;
    private int nbWordsSession = 0;
    private CustomTimer timer;

    /**
     * Initialisation des attributs et de l'affichage.
     */
    @FXML
    private void initialize() {
        SettingsModel settings = DactyloClient.getClient().getSettingsModel();
        if (ServerClient.getINSTANCE() != null) {
            ServerClient.getINSTANCE().setGameMenu(this);
        }
        if (DactyloClient.getGameType() == GameType.LOCAL || DactyloClient.getGameType() == GameType.TRAIN) {
            mainContainer.getChildren().remove(playerListContainer);
            mainContainer.getChildren().remove(logsContainer);
        }

        double startTime = System.currentTimeMillis();
        DactyloClient.getClient().getTmpStats().setStartTime(startTime);
        pressed = 0;
        nbWordsSession = 0;
        maxWordsSession = settings.getWordToEnd();

        nbrWord = new NbrWord();

        utilWords = new UtilWords();
        if (DactyloClient.getGameType() == GameType.MULTI) {
            words = new Words(ServerClient.getINSTANCE().getWords(), DactyloClient.getGameType());
        } else {
            words = new Words(ClassLoader.getSystemResourceAsStream("source/wordsList.txt"),
                    DactyloClient.getGameType());
        }
        words.setGameMenu(this);
        scoreView = new ScoreView(nbrWord);
        words.setNbrWord(nbrWord);
        if (DactyloClient.getGameType() == GameType.LOCAL) {
            precisionText.setText(df.format(DactyloClient.getClient().getTmpStats().getLastPrecision()) + " %");
            regularityText.setText(df.format(DactyloClient.getClient().getTmpStats().getLastRegularity()) + "");
            speedText.setText(df.format(DactyloClient.getClient().getTmpStats().getLastSpeed()) + " MPM");
        }

        utilWords.utilsWordsProperty().addListener((observable, oldValue, newValue) -> {
            double precision = (100d * (double) utilWords.getUtilsWords() / pressed);
            if(Double.isNaN(precision)){
                precision = 0;
            }
            if(Double.isInfinite(precision)){
                precision = 0;
            }
            precisionText.setText(df.format(precision) + " %");

            double endMinutes = ((System.currentTimeMillis() - startTime) / 60000d);
            double speed = ((double) utilWords.getUtilsWords() / endMinutes) / 5d;
            if(Double.isNaN(speed)){
                speed = 0;
            }
            if(Double.isInfinite(speed)){
                speed = 0;
            }

            speedText.setText(df.format(speed) + " MPM");

            if (DactyloClient.getGameType() == GameType.LOCAL) {
                DactyloClient.getClient().getTmpStats().addPrecision(precision);
                DactyloClient.getClient().getTmpStats().addSpeed(speed);
            }
        });

        utilWords.regulariteProperty().addListener((observable, oldValue, newValue) -> {
            if (DactyloClient.getGameType() == GameType.LOCAL) {
                DactyloClient.getClient().getTmpStats().addRegularity((Double) newValue);
            }
            regularityText.setText(df.format(newValue) + "");
        });

        if (DactyloClient.getGameType() == GameType.LOCAL || DactyloClient.getGameType() == GameType.MULTI) {
            life = new Life();

            words.setLife(life);
            lifeView = new LifeView(life);

            life.lifeProperty().addListener((observable, oldValue, newValue) -> {
                if (DactyloClient.getGameType() == GameType.LOCAL) {
                    DactyloClient.getClient().setLife(newValue.intValue());
                }
                lifeView.update();
            });
            if (DactyloClient.getGameType() == GameType.MULTI) {
                life.setLife(DactyloClient.getClient().getMultiLife());
            } else {
                life.setLife(DactyloClient.getClient().getLife());
            }
            infoContainer.add(lifeView, 0, 1);
        }

        if (DactyloClient.getGameType() == GameType.LOCAL) {
            levelLabel.setText("Niveau: " + DactyloClient.getClient().getLevel());
            pressed = DactyloClient.getClient().getTmpStats().getPressed();
            timer = new CustomTimer(words, this);

            nbrWord.nbrWordProperty().addListener((observable, oldValue, newValue) -> {
                DactyloClient.getClient().setNbrMots(newValue.intValue());

                int tmpLevel = newValue.intValue() / settings.getMotLevel();
                if (tmpLevel != DactyloClient.getClient().getLevel()) {
                    DactyloClient.getClient().setLevel(tmpLevel);
                    levelLabel.setText("Niveau: " + DactyloClient.getClient().getLevel());
                    timer.startTimer();
                }
                scoreView.update();
            });

            life.setLife(DactyloClient.getClient().getLife());
            if (life.getLife() <= 0) {
                words.setGameOver(true);
            }
            nbrWord.setNbrWord(DactyloClient.getClient().getNbrMots());

            infoContainer.add(timer.getView(), 1, 1);
            timer.startTimer();

        } else {
            mainContainer.getChildren().remove(levelLabel);
        }

        infoContainer.add(scoreView, 2, 1);
        if (DactyloClient.getGameType() == GameType.TRAIN) {
            Label txt = new Label(settings.getWordToEnd() + "");
            txt.getStylesheets().add("css/gameMenu.css");
            txt.getStylesheets().add("css/all.css");
            txt.getStyleClass().add("cText");
            f1Grid.setText("Mots pour finir");
            infoContainer.add(txt, 0, 1);
            infoContainer.getChildren().remove(1);
            infoContainer.setHgap(25d);
        }
        if (DactyloClient.getGameType() == GameType.MULTI) {
            infoContainer.getChildren().remove(1);
            infoContainer.setHgap(25d);
        }

        init();

        DactyloClient.getPrimaryStage().getScene().setOnKeyReleased(event -> {
            if (!words.gameOver()) {
                int len = words.getWords().get(0).length();
                if (event.getCode().equals(KeyCode.SPACE) || event.getCode().equals(KeyCode.ENTER)) {
                    if (words.check(false, null)) { // mot correct
                        utilWords.computeWords();
                        init();
                        if (DactyloClient.getGameType() != GameType.TRAIN) {
                            lifeView.update();
                        }
                        scoreView.update();
                        if (DactyloClient.getGameType() == GameType.TRAIN) {
                            nbWordsSession++;
                            if (nbWordsSession == maxWordsSession) {
                                endSession(DactyloClient.getGameType() == GameType.MULTI);
                                words.setGameOver(true);
                            }
                        }
                    } else { // mot incorrect
                        if (words.gameOver()) {
                            endSession(DactyloClient.getGameType() == GameType.MULTI);
                            CustomTimer.stopTimer();
                        } else {
                            for (int i = 0; i < len; i++) {
                                if (i == 0) {
                                    ((Text) currentTextContainer.getChildren().get(i)).setFill(NEXT); // prochaine lettre a taper
                                } else {
                                    ((Text) currentTextContainer.getChildren().get(i)).setFill(WRONG);
                                }
                            }
                        }
                    }

                    if (!words.gameOver() && DactyloClient.getGameType() == GameType.LOCAL) {
                        timer.startTimer();
                    }
                } else if (event.getCode().equals(KeyCode.BACK_SPACE)) { // si effacement du dernier caractere
                    words.setIndex(words.getIndex() - 1);
                    words.setCheck("");
                    if (words.isBonus(0) && words.getIndex() + 1 < len
                            && DactyloClient.getGameType() != GameType.TRAIN) {
                        if (DactyloClient.getGameType() == GameType.LOCAL) {
                            ((Text) currentTextContainer.getChildren().get(words.getIndex() + 1)).setFill(BONUS);
                        } else if (DactyloClient.getGameType() == GameType.MULTI) {
                            ((Text) currentTextContainer.getChildren().get(words.getIndex() + 1)).setFill(MULTI_BONUS);
                        }
                    } else if (words.getIndex() + 1 < len) {
                        ((Text) currentTextContainer.getChildren().get(words.getIndex() + 1)).setFill(DEFAULT);
                    }
                    if (words.getIndex() < len) { // prochaine lettre a taper
                        ((Text) currentTextContainer.getChildren().get(words.getIndex())).setFill(NEXT);
                    }
                } else if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) { // si caractere quelconque
                    pressed++;
                    if (DactyloClient.getGameType() != GameType.TRAIN) {
                        DactyloClient.getClient().getTmpStats().setPressed(pressed);
                    }
                    if (words.getIndex() < len) {
                        words.setCheck(event.getText());
                        if (words.checkLetter()) { // couleur selon si la lettre est correcte ou non
                            utilWords.addWord((Text) currentTextContainer.getChildren().get(words.getIndex() - 1));
                            ((Text) currentTextContainer.getChildren().get(words.getIndex() - 1)).setFill(CORRECT);
                        } else {
                            ((Text) currentTextContainer.getChildren().get(words.getIndex() - 1)).setFill(ERROR);
                        }

                        ((Text) currentTextContainer.getChildren().get(words.getIndex())).setFill(NEXT); // prochaine lettre a taper
                    }
                }
            } else {
                endSession(DactyloClient.getGameType() == GameType.MULTI);

            }
        });
        if (DactyloClient.getClient().getLife() <= 0 && DactyloClient.getGameType() == GameType.LOCAL) {
            words.setGameOver(true);
            endSession(DactyloClient.getGameType() == GameType.MULTI);
        }
    }

    /**
     * Varaible words de gameMenu.
     *
     * @return Words.
     */
    public Words getWords() {
        return words;
    }


    /**
     * Affichage du mot et de la liste des mots à taper.
     */
    private void init() {
        currentTextContainer.getChildren().clear();
        wordListContainer.getChildren().clear();
        char[] letters = words.getLetters();
        int len = letters.length;
        for (int i = 0; i < len; i++) {
            Text letter = new Text(letters[i] + "");
            letter.setFont(Font.font(40));
            if (words.getIndex() == i) {
                letter.setFill(NEXT);
            } else if (words.isBonus(0) && DactyloClient.getGameType() == GameType.LOCAL) {
                letter.setFill(BONUS);
            } else if (words.isBonus(0) && DactyloClient.getGameType() == GameType.MULTI) {
                letter.setFill(MULTI_BONUS);
            } else {
                letter.setFill(DEFAULT);
            }
            currentTextContainer.getChildren().add(letter);
        }
        updateWordList();
    }

    private void updateWordList() {
        wordListContainer.getChildren().clear();
        ArrayList<String> wordsList = words.getWords();
        for (int i = 1; i < wordsList.size(); i++) {
            Text word = new Text(wordsList.get(i));
            word.setFont(Font.font(20));
            if (words.isBonus(i) && DactyloClient.getGameType() == GameType.LOCAL) {
                word.setFill(BONUS);
            } else if (words.isBonus(i) && DactyloClient.getGameType() == GameType.MULTI) {
                word.setFill(MULTI_BONUS);
            } else {
                word.setFill(DEFAULT);
            }
            wordListContainer.getChildren().add(word);
        }
    }

    /**
     * Augmente le nombre de touche pressées et met à jour la précision.
     *
     * @param pressed Valeur à ajouter.
     */
    public void addPressed(int pressed) {
        this.pressed += pressed;
        double precision = (100d * (double) utilWords.getUtilsWords() / pressed);
        precisionText.setText(df.format(precision) + " %");
    }

    /**
     * Mise à jour de l'affichage suite au temps écoulé.
     *
     * @param isWrong Boolean indiquant si le mot est faux.
     */
    public void updateView(boolean isWrong, boolean fromServer) {
        if(words.getWords().size() != Constants.LIST_MAX_SIZE) {
            for (int i = 0; i < words.getWords().get(0).length(); i++) {
                if (words.getIndex() == i) {
                    ((Text) currentTextContainer.getChildren().get(i)).setFill(NEXT);
                } else {
                    if (isWrong) {
                        ((Text) currentTextContainer.getChildren().get(i)).setFill(WRONG);
                    } else {
                        if (fromServer) {
                            Paint color = ((Text) currentTextContainer.getChildren().get(i)).getFill();
                            ((Text) currentTextContainer.getChildren().get(i)).setFill(color);
                        } else {
                            ((Text) currentTextContainer.getChildren().get(i)).setFill(DEFAULT);
                        }
                    }
                }
            }
            updateWordList();
        }else{
            init();
        }

    }

    /**
     * Affichage pour la fin d'une partie.
     *
     * @param multi Mode de jeu en multijoueur on non.
     */
    public void endSession(boolean multi) {
        Platform.runLater(() -> {
            currentTextContainer.getChildren().clear();
            wordListContainer.getChildren().clear();
            Text end = new Text("Fin de la session");
            if (DactyloClient.getGameType() != GameType.TRAIN) {
                if (life.getLife() == 0) {
                    end.setText("Défaite");
                    if (!multi) {
                        Text endMessage = new Text(
                                "Pour recommencer, cliquez sur le bouton \"Réinitialiser\",\nqui ce trouve dans les Paramètres.");
                        endMessage.setFont(Font.font("System Bold", 30));
                        endMessage.setFill(Color.rgb(128, 203, 196));
                        endMessage.setTextAlignment(TextAlignment.CENTER);
                        endMessage.setWrappingWidth(600);
                        wordListContainer.getChildren().add(endMessage);
                        Security.saveCrypt(false);
                    }
                } else if (multi) {
                    end.setText("Vous avez Gagné.");
                }
            }

            end.setFont(Font.font("System Bold", 50));
            end.setFill(Color.rgb(128, 203, 196));
            currentTextContainer.getChildren().add(end);
            CustomTimer.stopTimer();
            DactyloClient.getPrimaryStage().getScene().setOnKeyReleased(null);

            if (DactyloClient.getGameType() == GameType.MULTI) {
                if (ServerClient.getINSTANCE() != null) {
                    ServerClient.getINSTANCE().disconnect(true);
                }
            }
        });

    }

    /**
     * Variable life de gameMenu.
     *
     * @return Life
     */
    public Life getLife() {
        return life;
    }

    public void addLogs(String log) {
        Platform.runLater(() -> {
            logsArea.appendText(log + "\n");
        });
    }

    public void addPlayer(String name) {
        Platform.runLater(() -> {
            playerList.setText(name);
        });
    }

}
