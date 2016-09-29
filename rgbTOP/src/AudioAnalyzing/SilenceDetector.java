package AudioAnalyzing;

import AudioAnalyzing.Detector.Method;
import Libaries.TarsosDSP.dsp.AudioDispatcher;
import Libaries.TarsosDSP.dsp.AudioEvent;
import Libaries.TarsosDSP.dsp.AudioProcessor;

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
public class SilenceDetector implements AudioProcessor {

    private final Libaries.TarsosDSP.dsp.SilenceDetector silenceDetector;

    final Method toCall;

    /**
     *
     * @param pToCall should have a parameter called "double silence"
     * @param dispatcher
     * @param threshold The threshold which defines when a buffer is silent (in
     * dB). Normal values are [-70.0,-30.0] dB SPL.
     * @param breakProcessingQueueOnSilence
     */
    public SilenceDetector(Method pToCall, AudioDispatcher dispatcher, double threshold, boolean breakProcessingQueueOnSilence) {
        toCall = pToCall;

        // add a processor, handle percussion event.
        silenceDetector = new Libaries.TarsosDSP.dsp.SilenceDetector(threshold, breakProcessingQueueOnSilence);
        dispatcher.addAudioProcessor(silenceDetector);
        dispatcher.addAudioProcessor(this);

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }

    /**
     *
     * @param pToCall should have parameter called "double silence"
     * @param dispatcher
     */
    public SilenceDetector(Method pToCall, AudioDispatcher dispatcher) {
        toCall = pToCall;

        // add a processor, handle percussion event.
        silenceDetector = new Libaries.TarsosDSP.dsp.SilenceDetector();
        dispatcher.addAudioProcessor(silenceDetector);
        dispatcher.addAudioProcessor(this);

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        toCall.execute(silenceDetector.currentSPL());
        return true;
    }

    @Override
    public void processingFinished() {
        //No action
    }

}
