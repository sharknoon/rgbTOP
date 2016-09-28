package AudioAnalyzing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




import Libaries.TarsosDSP.be.tarsos.dsp.onsets.OnsetHandler;
import Libaries.TarsosDSP.be.tarsos.dsp.onsets.PercussionOnsetDetector;



/**
 * Dient als Schlagerkennung, z.B. beim Klatschen
 * @author i01frajos445
 */
public class PercussionDetector implements OnsetHandler{

    public PercussionDetector() {
        Detector dec = new Detector();        

        // add a processor, handle percussion event.
        PercussionOnsetDetector pod = new PercussionOnsetDetector(Detector.sampleRate, Detector.bufferSize, this,0,0);//EVTL noch sensitivity und threshold
        dec.dispatcher.addAudioProcessor(pod);

        // run the dispatcher (on a new thread).
        new Thread(dec.dispatcher, "Audio dispatching").start();
    }

    public static void main(String[] args) {
        new PercussionDetector();
    }

    @Override
    public void handleOnset(double time, double salience) {
        System.out.println("Percussion happened!");
    }

}
