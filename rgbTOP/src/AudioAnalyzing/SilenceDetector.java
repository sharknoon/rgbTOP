package AudioAnalyzing;

import AudioAnalyzing.Detector.Method;
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
    
    Method toCall;

    //Settings
    private static final double THRESHOLD = -65;//Default -75
    private static final char TIMETOCHANGE = 5000;//when in TIMETOCHANGE milisecs 85% of the loudness above THRESHOLD is -> loud

    public SilenceDetector(Method pToCall, Detector detector) {
        toCall = pToCall;

        // add a processor, handle percussion event.
        silenceDetector = new be.tarsos.dsp.SilenceDetector();
        detector.dispatcher.addAudioProcessor(silenceDetector);
        detector.dispatcher.addAudioProcessor(this);

        // run the dispatcher (on a new thread).
        new Thread(detector.dispatcher, "Audio dispatching").start();
    }

    private long timeToReach = 0;
    private int amountDecibel = 0;
    private int counter = 0;
    private boolean silence = true;

    @Override
    public boolean process(AudioEvent audioEvent) {
        if (System.currentTimeMillis() > timeToReach) {
            int averageDecibel = (int) Math.round((double) amountDecibel / (double) counter);
            if ((averageDecibel > THRESHOLD) && silence) {//Lauter als Schwelle und davor war Stille
                silence = false;
                toCall.execute(silence);
            } else if ((averageDecibel < THRESHOLD) && !silence) {//Leiser als Schwelle und davor war Lautheit
                silence = true;
                toCall.execute(silence);
            }
            System.out.println("Average DB: " + averageDecibel + ", Threshold: " + THRESHOLD);
            counter = 0;
            amountDecibel = 0;
            timeToReach = System.currentTimeMillis() + TIMETOCHANGE;
        } else {
            counter++;
            amountDecibel += silenceDetector.currentSPL();
        }
        return true;
    }

    @Override
    public void processingFinished() {
        //No action
    }

}
