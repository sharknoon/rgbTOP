package Main;

import AudioAnalyzing.Detector;
import AudioAnalyzing.Detector.Method;
import LEDControlling.*;
import java.awt.Color;


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

    LEDs leds;
    int invervallInMillsec = 100;

    public Controller(String[] args) {

        boolean loop = true;
        leds = new LEDs(args);
        while (loop) {
            try {
                leds.setColor(Color.red);
                leds.setBrightness(Percentage.get100Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get75Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get50Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get25Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get0Percent());
                Thread.sleep(1000);
                leds.setColor(Color.green);
                leds.setBrightness(Percentage.get100Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get75Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get50Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get25Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get0Percent());
                Thread.sleep(1000);
                leds.setColor(Color.blue);
                leds.setBrightness(Percentage.get100Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get75Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get50Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get25Percent());
                Thread.sleep(1000);
                leds.setBrightness(Percentage.get0Percent());
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println("Konnte nicht warten!");
            }
        }

        Detector dec = new Detector();

        Method handleSilence = (parameter) -> handleSilence((double) parameter[0]);
        Method handleSpectrum = (parameter) -> handleSpectrum((double[]) parameter[0]);
        Method handlePercussion = (parameter) -> handlePercussion((double) parameter[0], (double) parameter[1]);
        Method handlePitch = (parameter) -> handlePitch((float) parameter[0], (float) parameter[1], (double) parameter[2]);
        Method handleOscilloscope = (parameter) -> handleOscilloscope((float[]) parameter[0]);

        //dec.addSilenceDetector(handleSilence);
        dec.addSpectrumDetector(handleSpectrum);
        //dec.addPercussionDetector(handlePercussion, 50, 8);
        //dec.addPitchDetector(handlePitch);
        //dec.addOscilloscopeDetector(handleOscilloscope);
    }

    public void handleSilence(double silence) {
        System.out.println("Silence: " + silence);
    }

    Percentage lastBass = Percentage.get0Percent();
    long lastTime = System.currentTimeMillis();
    double averageBass = 0.0;
    double counter = 1;

    public void handleSpectrum(double[] spectrum) {
        if (lastTime + invervallInMillsec > System.currentTimeMillis()) {
            leds.setBrightness(Percentage.getPercent((byte) ((averageBass / counter) * 100.0)));
            averageBass = 0.0;
            counter = 0.0;
        } else {
            averageBass += spectrum[0];
            counter++;
        }

        //leds.removeBrightness(lastBass, false);
        //leds.addBrightness(lastBass = Percentage.getPercent((byte) (spectrum[0] * 50)), true);
    }

    public void handlePercussion(double time, double salience) {
        System.out.println("Percussion: Time: " + time + ", Salience: " + salience);
    }

    public void handlePitch(float pitch, float probability, double rms) {
        System.out.println("Pitch: " + pitch + ", Prob.: " + probability + ", RMS: " + rms);
    }

    public void handleOscilloscope(float[] data) {
        System.out.println("Oscilloscope");
    }

    public static void main(String[] args) {
        Controller controller = new Controller(args);
    }

}
