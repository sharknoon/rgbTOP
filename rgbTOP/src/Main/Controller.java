package Main;

import AudioAnalyzing.Detector;
import AudioAnalyzing.SilenceDetector;
import AudioAnalyzing.SpectrumDetector;
import LEDControlling.LEDs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author i01frajos445
 */
public class Controller implements Interfaces.SilenceDetector, Interfaces.BassDetector {

    public Controller(String[] args) {
        //LEDs leds = new LEDs(args);
        Detector dec = new Detector(Detector.MAINMIC);
        SpectrumDetector std = (SpectrumDetector) dec.startDetector(this, Detector.SPECTRUMDETECTOR);
        SilenceDetector sld = (SilenceDetector) dec.startDetector(this, Detector.SILENCEDETECTOR);
    }

    @Override
    public void silenceChanged(boolean newSilence) {
        if (newSilence) {
            System.out.println("Leise");
        } else {
            System.out.println("Laut");
        }
    }

    @Override
    public void bassDropped(int bass) {
        System.out.println("Bass: " + bass);
    }

    public static void main(String[] args) {
        Controller controller = new Controller(args);
    }

}
