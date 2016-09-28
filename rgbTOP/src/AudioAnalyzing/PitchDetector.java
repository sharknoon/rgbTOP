package AudioAnalyzing;

import Libaries.TarsosDSP.dsp.AudioEvent;
import Libaries.TarsosDSP.dsp.pitch.PitchDetectionHandler;
import Libaries.TarsosDSP.dsp.pitch.PitchDetectionResult;
import Libaries.TarsosDSP.dsp.pitch.PitchProcessor;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Dient als Stimmlageerkennung, z.B. für Singprogramme
 * @author i01frajos445
 */
public class PitchDetector implements PitchDetectionHandler {

    public PitchDetector() {
        Detector dec = new Detector(Detector.sampleRate, Detector.bufferSize * 2, Detector.overlap);
        // add a processor, handle percussion event.
        PitchProcessor pp = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, Detector.sampleRate, Detector.bufferSize, this);
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
