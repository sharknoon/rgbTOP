package AudioAnalyzing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import be.hogent.tarsos.dsp.PitchProcessor;

/**
 * Dient als Stimmlageerkennung, z.B. für Singprogramme
 *
 * @author i01frajos445
 */
public class PitchDetector implements PitchProcessor.DetectedPitchHandler {

    public PitchDetector() {
        Detector dec = new Detector(Detector.MAINMIC);
        // add a processor, handle percussion event.
        PitchProcessor pp = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, dec.defaultSampleRate, dec.defaultBufferSize, dec.defaultOverlap, 0, this);
        dec.dispatcher.addAudioProcessor(pp);

        // run the dispatcher (on a new thread).
        new Thread(dec.dispatcher, "Audio dispatching").start();
    }

    @Override
    public void handlePitch(float pitch, float probability, float timeStamp, float progress) {
        if (pitch != -1) {
            System.out.println("Pitch angekommen: " + pitch + " Probability: " + probability + " Progess: " + progress);//Progress???
        }
    }

    public static void main(String[] args) {
        new PitchDetector();
    }

}
