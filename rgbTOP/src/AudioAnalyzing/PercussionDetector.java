package AudioAnalyzing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import AudioAnalyzing.Detector.Method;
import Libaries.TarsosDSP.dsp.AudioDispatcher;
import Libaries.TarsosDSP.dsp.onsets.OnsetHandler;
import Libaries.TarsosDSP.dsp.onsets.PercussionOnsetDetector;

/**
 * Dient als Schlagerkennung, z.B. beim Klatschen
 *
 * @author i01frajos445
 */
public class PercussionDetector implements OnsetHandler {

    final Method toCall;

    /**
     * @param pToCall should have those two parameters "double time", "double
     * salience"
     * @param dispatcher
     * @param sensitivity Sensitivity of the peak detector applied to broadband
     * detection function (%). In [0-100].
     * @param threshold Energy rise within a frequency bin necessary to count
     * toward broadband total (dB). In [0-20].
     */
    public PercussionDetector(Detector.Method pToCall, AudioDispatcher dispatcher, double sensitivity, double threshold) {
        toCall = pToCall;

        // add a processor, handle percussion event.
        PercussionOnsetDetector pod = new PercussionOnsetDetector(Detector.sampleRate, Detector.bufferSize, this, sensitivity, threshold);
        dispatcher.addAudioProcessor(pod);

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }

    /**
     *
     * @param pToCall should have those two parameters "double time", "double
     * salience"
     * @param dispatcher
     */
    public PercussionDetector(Detector.Method pToCall, AudioDispatcher dispatcher) {
        toCall = pToCall;

        // add a processor, handle percussion event.
        PercussionOnsetDetector pod = new PercussionOnsetDetector(Detector.sampleRate, Detector.bufferSize, Detector.overlap, this);
        dispatcher.addAudioProcessor(pod);

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }

    @Override
    public void handleOnset(double time, double salience) {
        toCall.execute(time, salience);
    }

}
