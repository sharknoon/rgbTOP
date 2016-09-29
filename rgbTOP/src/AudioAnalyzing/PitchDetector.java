package AudioAnalyzing;

import AudioAnalyzing.Detector.Method;
import Libaries.TarsosDSP.dsp.AudioDispatcher;
import Libaries.TarsosDSP.dsp.AudioEvent;
import Libaries.TarsosDSP.dsp.pitch.PitchDetectionHandler;
import Libaries.TarsosDSP.dsp.pitch.PitchDetectionResult;
import Libaries.TarsosDSP.dsp.pitch.PitchProcessor;
import Libaries.TarsosDSP.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;

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

    final Method toCall;

        /**
     *
     * @param pToCall should have 3 parameters "float pitch", "float
     * probability", "double rms"
     * @param dispatcher
     * @param algorithm An enum defining the algorithm. default PitchEstimationAlgorithm.YIN
     */
    public PitchDetector(Method pToCall, AudioDispatcher dispatcher, PitchEstimationAlgorithm algorithm) {
        toCall = pToCall;

        // add a processor, handle percussion event.
        PitchProcessor pp = new PitchProcessor(algorithm, Detector.pitchSampleRate, Detector.pitchBufferSize, this);
        dispatcher.addAudioProcessor(pp);

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }
    
    /**
     *
     * @param pToCall should have 3 parameters "float pitch", "float
     * probability", "double rms"
     * @param dispatcher
     */
    public PitchDetector(Method pToCall, AudioDispatcher dispatcher) {
        toCall = pToCall;

        // add a processor, handle percussion event.
        PitchProcessor pp = new PitchProcessor(PitchEstimationAlgorithm.YIN, Detector.pitchSampleRate, Detector.pitchBufferSize, this);
        dispatcher.addAudioProcessor(pp);

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }

    float pitch = 0;
    float probability = 0;
    double rms = 0;

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        if (pitchDetectionResult.getPitch() != -1) {
            pitch = pitchDetectionResult.getPitch();
            probability = pitchDetectionResult.getProbability();
            rms = audioEvent.getRMS();// * 100 ???
            toCall.execute(pitch, probability, rms);
        }
    }

}
