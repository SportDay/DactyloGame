package fr.projet.util;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;

import java.util.Objects;

public final class TextFieldChecker {

    public static final Tooltip nameError = new Tooltip("Le nom du joueur peut contenir que des lettres ou des chiffres, ou les deux, et doit faire en moins 3 caractères");

    /**
     * Il ajoute un écouteur à la propriété text du champ de texte, et si le texte n'est pas un nombre, il ajoute la classe
     * d'erreur au champ de texte et affiche l'info-bulle
     *
     * @param txt Le TextField que vous souhaitez valider
     * @param error L'info-bulle qui s'affiche lorsque l'utilisateur saisit un caractère invalide.
     */
    public static void numTextField(TextField txt, Tooltip error) {
        txt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                if (!txt.getStyleClass().contains("error")) {
                    txt.getStyleClass().add("error");
                }
                Util.showTooltip(txt, error);
                txt.setText(newValue.replaceAll("[^\\d]", ""));
            } else {
                error.hide();
            }

        });

        txt.setOnKeyPressed(ke -> {
            if (Objects.requireNonNull(ke.getCode()) == KeyCode.ESCAPE) {
                txt.getStyleClass().removeAll("error");
                txt.setText("");
            }
        });

        txt.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                if (txt.getText().length() > 0) {
                    txt.getStyleClass().removeAll("error");
                } else {
                    if (!txt.getStyleClass().contains("error")) {
                        txt.getStyleClass().add("error");
                    }
                }
            }
        });
    }

    /**
     * Il vérifie si le champ de texte ne contient que des caractères alphanumériques, et sinon, il supprime les caractères
     * non alphanumériques
     *
     * @param txt Le TextField à valider
     */
    public static void playerName(TextField txt) {
        Tooltip nameError = new Tooltip("Le nom du joueur peut contenir que des lettres ou des chiffres, ou les deux, et doit faire en moins 3 caractères");
        txt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\p{Alnum}*")) {
                txt.setText(newValue.replaceAll("[^\\p{Alnum}]", ""));
                if (!txt.getStyleClass().contains("error")) {
                    txt.getStyleClass().add("error");
                }
                Util.showTooltip(txt, nameError);
            } else {
                txt.getStyleClass().removeAll("error");
                nameError.hide();
            }
        });

        txt.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                if (txt.getText().length() < 1) {
                    if (!txt.getStyleClass().contains("error")) {
                        txt.getStyleClass().add("error");
                    }
                }
            }
        });
    }
}
