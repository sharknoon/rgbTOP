package AudioAnalyzing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import be.hogent.tarsos.dsp.AudioProcessor;
import be.hogent.tarsos.dsp.ContinuingSilenceDetector;

/**
 * Dient als Lautstärkemessung, z.B. um vor zu lauter Lautstärke zu warnen
 * @author i01frajos445
 */
public class SoundPressureDetector implements AudioProcessor {

    ContinuingSilenceDetector silenceDetector;

    public SoundPressureDetector() {
        Detector dec = new Detector(Detector.MAINMIC);

        // add a processor, handle percussion event.
        silenceDetector = new ContinuingSilenceDetector();
        dec.dispatcher.addAudioProcessor(silenceDetector);
        dec.dispatcher.addAudioProcessor(this);

        // run the dispatcher (on a new thread).
        new Thread(dec.dispatcher, "Audio dispatching").start();
    }

    @Override
    public boolean processFull(float[] audioFloatBuffer, byte[] audioByteBuffer) {
        handleSound();
        return true;
    }

    @Override
    public boolean processOverlapping(float[] audioFloatBuffer, byte[] audioByteBuffer) {
        handleSound();
        return true;
    }

    @Override
    public void processingFinished() {
        //Do nothing
    }

    private void handleSound() {
        String currentSPL = String.valueOf((int) (silenceDetector.currentSPL()));
        while (currentSPL.length() < 5) {
            currentSPL += " ";
        }
        System.out.println("Current sound pressure level: " + currentSPL + "dB");
    }

    public static void main(String[] args) {
        new SoundPressureDetector();
    }
}
