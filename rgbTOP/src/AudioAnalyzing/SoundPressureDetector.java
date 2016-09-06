package AudioAnalyzing;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Dient als Lautstärkemessung, z.B. um vor zu lauter Lautstärke zu warnen
 *
 * @author i01frajos445
 */
public class SoundPressureDetector implements AudioProcessor {

    private SilenceDetector silenceDetector;
    
    //Settings
    private final double threshold = 0;

    public SoundPressureDetector() {
        Detector dec = new Detector(Detector.MAINMIC);

        // add a processor, handle percussion event.
        silenceDetector = new SilenceDetector(threshold, false);
        dec.dispatcher.addAudioProcessor(silenceDetector);
        dec.dispatcher.addAudioProcessor(this);

        // run the dispatcher (on a new thread).
        new Thread(dec.dispatcher, "Audio dispatching").start();
    }

    private void handleSound() {
        String currentSPL = String.valueOf((int) (silenceDetector.currentSPL()));
        while (currentSPL.length() < 5) {
            currentSPL += " ";
        }
        System.out.println("Current sound pressure level: " + currentSPL + "dB");
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        handleSound();
        return true;
    }

    public static void main(String[] args) {
        new SoundPressureDetector();
    }

    @Override
    public void processingFinished() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
