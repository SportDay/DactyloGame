package fr.projet.model;

import fr.projet.DactyloClient;
import fr.projet.util.Security;

import java.text.DecimalFormat;
import java.util.LinkedList;
/**
 * Classe caractérisant les statistiques temporaires d'un joueur
 */
public final class TmpStats {

    private final LinkedList<Double> precision = new LinkedList<>();
    private final LinkedList<Double> speed = new LinkedList<>();
    private final LinkedList<Double> regularity = new LinkedList<>();
    private int pressed = 0;
    private double startTime = 0;
    private double totalTime = 0;

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public int getPressed() {
        return pressed;
    }

    public void setPressed(int pressed) {
        this.pressed = pressed;
        Security.saveCrypt(false);
    }

    public void addPrecision(double precision) {
        this.precision.add(precision);
        Security.saveCrypt(false);
    }

    public void addSpeed(double speed) {
        this.speed.add(speed);
        Security.saveCrypt(false);
    }

    public void addRegularity(double regularity) {
        this.regularity.add(regularity);
        Security.saveCrypt(false);
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void updateTotalTime() {
        double endTime = System.currentTimeMillis();
        double tmp = endTime - startTime;
        this.totalTime += tmp;
    }

    public double getLastPrecision() {
        return precision.isEmpty() ? 0 : precision.getLast();
    }

    public double getLastSpeed() {
        return speed.isEmpty() ? 0 : speed.getLast();
    }

    public double getLastRegularity() {
        return regularity.isEmpty() ? 0 : regularity.getLast();
    }

    /**
     * Calcule la moyenne des statistiques
     */
    public void calcMedium() {
        DecimalFormat df = new DecimalFormat("#.##");

        double sum = 0;
        for (Double d : precision) {
            sum += d;
        }
        DactyloClient.getClient().addPrecision(DactyloClient.getClient().getLevel(), Double.parseDouble(df.format(sum / precision.size()).replace(",", ".")));
        sum = 0;
        for (Double d : speed) {
            sum += d;
        }
        DactyloClient.getClient().addSpeed(DactyloClient.getClient().getLevel(), Double.parseDouble(df.format(sum / speed.size()).replace(",", ".")));
        sum = 0;
        for (Double d : regularity) {
            sum += d;
        }
        DactyloClient.getClient().addRegularity(DactyloClient.getClient().getLevel(), Double.parseDouble(df.format(sum / regularity.size()).replace(",", ".")));
    }


    /**
     * Méthode permettant de réinitialiser les statistiques temporaires
     */
    public void clear() {
        calcMedium();
        pressed = 0;
        totalTime = 0;
        startTime = System.currentTimeMillis();
        precision.clear();
        speed.clear();
        regularity.clear();
        Security.saveCrypt(false);
    }
}
