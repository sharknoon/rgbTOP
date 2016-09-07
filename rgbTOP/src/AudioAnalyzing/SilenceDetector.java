package AudioAnalyzing;

import Main.Controller;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

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

    private final be.tarsos.dsp.SilenceDetector silenceDetector;
    private Controller controller;
    private long time = 0;

    //Settings
    private static final double THRESHOLD = -75;
    private static final char TIMETOCHANGE = 5000;//when in TIMETOCHANGE milisecs 85% of the loudness above THRESHOLD is -> loud

    public SilenceDetector(Controller pController) {
        controller = pController;
        Detector dec = new Detector(Detector.MAINMIC);

        // add a processor, handle percussion event.
        silenceDetector = new be.tarsos.dsp.SilenceDetector(THRESHOLD, false);
        dec.dispatcher.addAudioProcessor(silenceDetector);
        dec.dispatcher.addAudioProcessor(this);

        // run the dispatcher (on a new thread).
        new Thread(dec.dispatcher, "Audio dispatching").start();
    }

    boolean silence = true;

    @Override
    public boolean process(AudioEvent audioEvent) {
        double spl;
        if ((spl = silenceDetector.currentSPL()) > THRESHOLD) {
            if (silence) {//When prev. state was silent
                System.out.println("Changed to Loudness (" + spl + " db)");
                time = System.currentTimeMillis();
            }
            if ((time + TIMETOCHANGE) < System.currentTimeMillis()) {
                controller.silenceChanged(silence);
            }
            silence = false;
        } else {
            if (!silence) {
                System.out.println("Changed to Silence (" + spl + " db)");
                time = System.currentTimeMillis();
            }
            if ((time + TIMETOCHANGE) < System.currentTimeMillis()) {
                controller.silenceChanged(silence);
            }
            silence = true;
        }
        return true;
    }

    @Override
    public void processingFinished() {
        //No action
    }

}
