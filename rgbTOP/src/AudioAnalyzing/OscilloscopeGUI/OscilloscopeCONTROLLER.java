/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AudioAnalyzing.OscilloscopeGUI;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * FXML Controller class
 *
 * @author i01frajos445
 */
public class OscilloscopeCONTROLLER implements Initializable {

    @FXML
    Canvas canvas;

    //Settings
    int amountBars = 30;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void windowsResized(int width, int hight){
        canvas.setHeight(hight);
        canvas.setWidth(width);
    }
    
    /**
     * Writes the amplitudes
     *
     * @param data
     */
    public void setOscilloscopeData(float[] data) {
        float width = (float) canvas.getWidth();
        float height = (float) canvas.getHeight();
        float halfHeight = height / 2;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setLineWidth(2);
        for (int i = 0; i < data.length; i += 4) {
            gc.strokeLine((int) (data[i] * width), (int) (halfHeight - data[i + 1] * height), (int) (data[i + 2] * width), (int) (halfHeight - data[i + 3] * height));
        }
    }

}
