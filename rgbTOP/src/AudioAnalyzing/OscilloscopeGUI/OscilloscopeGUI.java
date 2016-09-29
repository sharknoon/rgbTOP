/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AudioAnalyzing.OscilloscopeGUI;

import AudioAnalyzing.Detector;
import AudioAnalyzing.Detector.Method;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author i01frajos445
 */
public class OscilloscopeGUI extends Application {

    OscilloscopeCONTROLLER controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("OscilloscopeFXML.fxml"));//Hier holt er sich die FXML Datei
        if (getClass().getResource("OscilloscopeFXML.fxml") == null) {
            System.err.println("Konnte URL zu OscilloscopeFXML.fxml nicht finden!");
        }
        Parent root = (Parent) loader.load();//Aus der Datei holt er sich das Grundlayout
        controller = loader.getController();//Aus der Datei holt er sich die Controllerreferenz

        primaryStage.setOnCloseRequest((WindowEvent value) -> {
            System.exit(0);
        });
        Scene scene;
        primaryStage.setScene(scene = new Scene(root));

        scene.widthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> {
            controller.windowsResized((int) scene.getWidth(), (int) scene.getHeight());
        });
        scene.heightProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) -> {
            controller.windowsResized((int) scene.getWidth(), (int) scene.getHeight());
        });

        primaryStage.show();

        Method toCall = (values) -> onCall((float[]) values[0]);

        Detector dec = new Detector();
        dec.addOscilloscopeDetector(toCall);

    }

    public void onCall(float[] data) {
        controller.setOscilloscopeData(data);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
