package Main;

import AudioAnalyzing.Detector;
import AudioAnalyzing.Detector.Method;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author i01frajos445 
 */
public class Controller {

    public Controller(String[] args) {
        //LEDs leds = new LEDs(args);
        Detector dec = new Detector();

        Method toCallOnSilenceChanged = (parameter) -> silenceChanged((boolean) parameter[0]);
        Method toCallOnBassDropped = (parameter) -> bassDropped((int) parameter[0]);

        //dec.addDetector(toCallOnSilenceChanged, Detector.SILENCEDETECTOR);
        //dec.addDetector(toCallOnBassDropped, Detector.SPECTRUMDETECTOR);
    }

    public void silenceChanged(boolean newSilence) {
        if (newSilence) {
            System.out.println("Leise");
        } else {
            System.out.println("Laut");
        }
    }

    public void bassDropped(int bass) {
        System.out.println("Bass: " + bass);
    }

    public static void main(String[] args) {
        Controller controller = new Controller(args);
    }

}
