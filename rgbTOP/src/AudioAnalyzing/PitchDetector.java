package AudioAnalyzing;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Dient als Stimmlageerkennung, z.B. für Singprogramme
 *
 * @author i01frajos445
 */
public class PitchDetector implements PitchDetectionHandler {

    public PitchDetector() {
        Detector dec = new Detector(Detector.MAINMIC, Detector.defaultSampleRate, Detector.defaultBufferSize * 2, Detector.defaultOverlap);
        // add a processor, handle percussion event.
        PitchProcessor pp = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, Detector.defaultSampleRate, Detector.defaultBufferSize, this);
        dec.dispatcher.addAudioProcessor(pp);

        // run the dispatcher (on a new thread).
        new Thread(dec.dispatcher, "Audio dispatching").start();
    }

    public static void main(String[] args) {
        new PitchDetector();
    }

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        if (pitchDetectionResult.getPitch() != -1) {
            float pitch = pitchDetectionResult.getPitch();
            float probability = pitchDetectionResult.getProbability();
            double rms = audioEvent.getRMS() * 100;
            System.out.println("Pitch angekommen: " + pitch + " Probability: " + probability + " RMS: " + rms);//Progress???
        }
    }

}
