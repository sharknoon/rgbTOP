package Main;

import AudioAnalyzing.Detector;
import AudioAnalyzing.Detector.Method;
import LEDControlling.*;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;


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
    int fadeCounter = 0;

    public Controller(String[] args) {

        leds = new LEDs(args);
        leds.setBrightness(Percentage.get100Percent());
        leds.setColor(Color.red);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
        }
        leds.setColor(Color.yellow);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
        }
        leds.setColor(Color.green);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
        }

        Detector dec = new Detector();

        Method handleSilence = (parameter) -> handleSilence((double) parameter[0]);
        Method handleSpectrum = (parameter) -> handleSpectrum((double[]) parameter[0]);
        Method handlePercussion = (parameter) -> handlePercussion((double) parameter[0], (double) parameter[1]);
        Method handlePitch = (parameter) -> handlePitch((float) parameter[0], (float) parameter[1], (double) parameter[2]);
        Method handleOscilloscope = (parameter) -> handleOscilloscope((float[]) parameter[0]);

        //Dient als Lautstärkemessung, z.B. um vor zu lauter Lautstärke zu warnen
        dec.addSilenceDetector(handleSilence);

        // Dient als Spektrometer, der wie ein Equalizer alle Frequenzen anzeigt,
        // z.B. für Basserkennung
        //dec.addSpectrumDetector(handleSpectrum);
        //Dient als Schlagerkennung, z.B. beim Klatschen
        //dec.addPercussionDetector(handlePercussion, 50, 8);
        //Dient als Stimmlageerkennung, z.B. für Singprogramme
        //dec.addPitchDetector(handlePitch);
        //Dient als Oscilloskop, der angezeigt werden kann, dient z.B. zur Beaterkennung
        //dec.addOscilloscopeDetector(handleOscilloscope);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dec.removeAllDetectors();
                leds.fadeSpectrum(5000);
            }
        }, 20 * 60 * 1000);//20 * 60 * 1000

    }

    double averageVolume = 0.5;
    double maxVolume = 0;
    double maxVolumeTemp = 0;
    double amountVolumes = 0;
    double volumes = 0;

    public void handleSilence(double volume) {
        volume += 85;//statt von -85 - -50 -> 0 - 35
        volume *= 0.02857;//statt von 0 - 35 -> 0.00 - 1.00

        if (amountVolumes < 10) {
            amountVolumes++;
            volumes += volume;
            if (volume > maxVolumeTemp) {
                maxVolumeTemp = volume;
                leds.changeColorRandom();
            }
        } else {
            averageVolume = volumes / amountVolumes;
            amountVolumes = 1;
            volumes = volume;
            //System.out.println("MAX: " + maxVolume);
            maxVolume = maxVolumeTemp;
            maxVolumeTemp = volume;
        }
        if (volume > averageVolume) {
            leds.setBrightness(Percentage.getPercent((byte) (((volume - averageVolume) / (maxVolume - averageVolume)) * 100)));
        } else {
            leds.setBrightness(Percentage.get0Percent());
        }
        //System.out.println(volume);
    }

    public void handleSpectrum(double[] spectrum) {

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
