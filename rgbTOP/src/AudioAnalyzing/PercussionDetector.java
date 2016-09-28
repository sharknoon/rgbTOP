package AudioAnalyzing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import AudioAnalyzing.Detector.Method;
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
     * @param detector
     * @param sensitivity Sensitivity of the peak detector applied to broadband
     * detection function (%). In [0-100].
     * @param threshold Energy rise within a frequency bin necessary to count
     * toward broadband total (dB). In [0-20].
     */
    public PercussionDetector(Detector.Method pToCall, Detector detector, double sensitivity, double threshold) {
        toCall = pToCall;

        // add a processor, handle percussion event.
        PercussionOnsetDetector pod = new PercussionOnsetDetector(Detector.sampleRate, Detector.bufferSize, this, sensitivity, threshold);
        detector.dispatcher.addAudioProcessor(pod);

        // run the dispatcher (on a new thread).
        new Thread(detector.dispatcher, "Audio dispatching").start();
    }

    /**
     *
     * @param pToCall should have those two parameters "double time", "double
     * salience"
     * @param detector
     */
    public PercussionDetector(Detector.Method pToCall, Detector detector) {
        toCall = pToCall;

        // add a processor, handle percussion event.
        PercussionOnsetDetector pod = new PercussionOnsetDetector(Detector.sampleRate, Detector.bufferSize, Detector.overlap, this);
        detector.dispatcher.addAudioProcessor(pod);

        // run the dispatcher (on a new thread).
        new Thread(detector.dispatcher, "Audio dispatching").start();
    }

    @Override
    public void handleOnset(double time, double salience) {
        toCall.execute(time, salience);
    }

}
