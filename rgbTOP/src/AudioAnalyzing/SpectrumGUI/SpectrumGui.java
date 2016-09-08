/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AudioAnalyzing.SpectrumGUI;

import AudioAnalyzing.SpectrumDetector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author i01frajos445
 */
public class SpectrumGui extends Application {

    SpectrumFXMLController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SpectrumFXML.fxml"));//Hier holt er sich die FXML Datei

        Parent root = (Parent) loader.load();//Aus der Datei holt er sich das Grundlayout
        controller = loader.getController();//Aus der Datei holt er sich die Controllerreferenz

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        SpectrumDetector dec = new SpectrumDetector(controller);
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
