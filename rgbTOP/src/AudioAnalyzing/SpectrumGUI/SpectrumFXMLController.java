/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AudioAnalyzing.SpectrumGUI;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author i01frajos445
 */
public class SpectrumFXMLController implements Initializable {

    @FXML
    VBox vbox;

    //Settings
    int amountBars = 10;

    ArrayList<ProgressBar> progressBars = new ArrayList<>();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (int i = 0; i < amountBars; i++) {
            ProgressBar pb = new ProgressBar(0);
            progressBars.add(pb);
            vbox.getChildren().add(pb);
        }
    }

    /**
     * Writes the amplitudes
     *
     * @param amplitudes amplitude array with 10 values, ranging from 0.0 to 1.0
     */
    public void setAmplitudes(double[] amplitudes) {
        if (amplitudes.length != amountBars) {
            System.err.println("Fehler, zuwenig oder zu viele Amplituden angegeben: " + amplitudes.length + " (ArrayLength) != " + amountBars + " (max Allowed Bars)");
            return;
        }
        for (int i = 0; i < amountBars; i++) {
            progressBars.get(i).setProgress(amplitudes[i]);
        }
    }

}
