/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AudioAnalyzing.SilenceGUI;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.shape.Ellipse;

/**
 *
 * @author Josua Frank
 */
public class SilenceCONTROLLER implements Initializable {

    @FXML
    Ellipse volumeEllipse;

    @FXML
    Ellipse averageEllipse;

    @FXML
    Ellipse volumeOverAverageEllipse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    protected void setVolumeEllipse(double density) {
        volumeEllipse.setOpacity(density);
    }

    protected void setAverageEllipse(double density) {
        averageEllipse.setOpacity(density);
    }

    protected void setVolumeOverAverageEllipse(double density) {
        volumeOverAverageEllipse.setOpacity(density);
    }

}
