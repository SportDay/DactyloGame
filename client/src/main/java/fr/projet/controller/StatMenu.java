package fr.projet.controller;

import fr.projet.DactyloClient;
import fr.projet.model.Client;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

/**
 * Contrôleur pour le menu statistiques.
 */
public class StatMenu {

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private Label playerName;

    @FXML
    private LineChart<String, Number> allChart;

    @FXML
    private LineChart<String, Number> presChart;

    @FXML
    private LineChart<String, Number> regChart;

    @FXML
    private LineChart<String, Number> speedChart;

    @FXML
    private LineChart<String, Number> timeChart;


    /**
     * Initialisation des graphiques.
     */
    @FXML
    void initialize() {
        Client client = DactyloClient.getClient();

        playerName.setText("Pseudo : " + client.getName() + " | Niveaux : " + client.getLevel());

        XYChart.Series<String, Number> precision = new XYChart.Series<>();
        XYChart.Series<String, Number> vitesse = new XYChart.Series<>();
        XYChart.Series<String, Number> regularite = new XYChart.Series<>();
        XYChart.Series<String, Number> temps = new XYChart.Series<>();

        XYChart.Series<String, Number> precision2 = new XYChart.Series<>();
        XYChart.Series<String, Number> vitesse2 = new XYChart.Series<>();
        XYChart.Series<String, Number> regularite2 = new XYChart.Series<>();
        XYChart.Series<String, Number> temps2 = new XYChart.Series<>();

        precision.setName("Précision (%)");
        vitesse.setName("Vitesse (MPM)");
        regularite.setName("Régularité");
        temps.setName("Temps (Secondes)");

        precision2.setName("Précision (%)");
        vitesse2.setName("Vitesse (MPM)");
        regularite2.setName("Régularité");
        temps2.setName("Temps (Secondes)");

        for (int i = 1; i <= client.getLevel(); i++) {
            precision.getData().add(new XYChart.Data<>("" + (i - 1), client.getPrecision().get(i)));
            vitesse.getData().add(new XYChart.Data<>("" + (i - 1), client.getSpeed().get(i)));
            regularite.getData().add(new XYChart.Data<>("" + (i - 1), client.getRegularity().get(i)));
            temps.getData().add(new XYChart.Data<>("" + (i - 1), client.getTime().get(i)));

            precision2.getData().add(new XYChart.Data<>("" + (i - 1), client.getPrecision().get(i)));
            vitesse2.getData().add(new XYChart.Data<>("" + (i - 1), client.getSpeed().get(i)));
            regularite2.getData().add(new XYChart.Data<>("" + (i - 1), client.getRegularity().get(i)));
            temps2.getData().add(new XYChart.Data<>("" + (i - 1), client.getTime().get(i)));
        }
        allChart.getData().add(precision);
        allChart.getData().add(regularite);
        allChart.getData().add(vitesse);
        allChart.getData().add(temps);

        presChart.getData().add(precision2);
        regChart.getData().add(regularite2);
        speedChart.getData().add(vitesse2);
        timeChart.getData().add(temps2);

        // Rajout des tooltips pour les graphiques
        for (int i = 0; i < client.getLevel(); i++) {
            Tooltip t = new Tooltip(precision.getData().get(i).getYValue() + " %");
            Tooltip.install(precision.getData().get(i).getNode(), t);
            t = new Tooltip(temps.getData().get(i).getYValue() + " Sec");
            Tooltip.install(temps.getData().get(i).getNode(), t);
            t = new Tooltip(vitesse.getData().get(i).getYValue() + " MPM");
            Tooltip.install(vitesse.getData().get(i).getNode(), t);
            t = new Tooltip(regularite.getData().get(i).getYValue() + " %");
            Tooltip.install(regularite.getData().get(i).getNode(), t);

            t = new Tooltip(precision2.getData().get(i).getYValue() + " %");
            Tooltip.install(precision2.getData().get(i).getNode(), t);
            t = new Tooltip(temps2.getData().get(i).getYValue() + " Sec");
            Tooltip.install(temps2.getData().get(i).getNode(), t);
            t = new Tooltip(vitesse2.getData().get(i).getYValue() + " MPM");
            Tooltip.install(vitesse2.getData().get(i).getNode(), t);
            t = new Tooltip(regularite2.getData().get(i).getYValue() + "");
            Tooltip.install(regularite2.getData().get(i).getNode(), t);
        }
    }
}
