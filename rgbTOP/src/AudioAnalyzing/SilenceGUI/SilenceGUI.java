/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AudioAnalyzing.SilenceGUI;

import AudioAnalyzing.Detector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import static javafx.application.Application.launch;

/**
 *
 * @author Josua Frank
 */
public class SilenceGUI extends Application {

    SilenceCONTROLLER controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SilenceFXML.fxml"));//Hier holt er sich die FXML Datei
        if (getClass().getResource("SilenceFXML.fxml") == null) {
            System.err.println("Konnte URL zu SilenceFXML.fxml nicht finden!");
        }
        Parent root = (Parent) loader.load();//Aus der Datei holt er sich das Grundlayout
        controller = loader.getController();//Aus der Datei holt er sich die Controllerreferenz

        primaryStage.setOnCloseRequest((WindowEvent value) -> {
            System.exit(0);
        });
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Detector.Method toCall = (values) -> onCall((double) values[0]);

        Detector dec = new Detector();
        dec.addSilenceDetector(toCall, 0);

    }

    double averageVolume = 0.5;
    double maxVolume = 0;
    double maxVolumeTemp = 0;
    double amountVolumes = 0;
    double volumes = 0;

    public void onCall(double volume) {
        volume += 85;//statt von -85 - -50 -> 0 - 35
        volume *= 0.02857;//statt von 0 - 35 -> 0.00 - 1.00

        if (amountVolumes < 10) {
            amountVolumes++;
            volumes += volume;
            if (volume > maxVolumeTemp) {
                maxVolumeTemp = volume;
            }
        } else {
            averageVolume = volumes / amountVolumes;
            amountVolumes = 1;
            volumes = volume;
            System.out.println("MAX: "+maxVolume);
            maxVolume = maxVolumeTemp;
            maxVolumeTemp = volume;
            System.out.println("AVG: " + averageVolume);
            controller.setAverageEllipse(averageVolume);
        }
        controller.setVolumeEllipse(volume);
        if (volume > averageVolume) {
            controller.setVolumeOverAverageEllipse((volume - averageVolume) / (maxVolume - averageVolume));
        }else{
            controller.setVolumeOverAverageEllipse(0);
        }
        //System.out.println(volume);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
