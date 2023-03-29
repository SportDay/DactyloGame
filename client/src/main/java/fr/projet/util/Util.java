package fr.projet.util;

import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;

public final class Util {

    /**
     * Il prend un contrôle et une info-bulle, et affiche l'info-bulle au bas du contrôle
     *
     * @param control Le contrôle sur lequel vous souhaitez afficher l'info-bulle.
     * @param customTooltip L'info-bulle que vous souhaitez afficher.
     */
    public static void showTooltip(Control control, Tooltip customTooltip) {
        Point2D p = control.localToScene(0.0, -40.0);
        customTooltip.setAutoHide(true);
        customTooltip.setAutoFix(true);
        customTooltip.show(control, p.getX() + control.getScene().getX() + control.getScene().getWindow().getX(), p.getY() + control.getScene().getY() + control.getScene().getWindow().getY());
    }
}
