package AudioAnalyzing;

import Libaries.TarsosDSP.dsp.spectrum.SpectrumProcessor;
import AudioAnalyzing.Detector.Method;
import Libaries.TarsosDSP.dsp.AudioDispatcher;
import Libaries.TarsosDSP.dsp.spectrum.SpectrumHandler;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Dient als Spektrometer, die wie ein Equalizer alle Frequenzen anzeigt, z.B.
 * für Basserkennung
 *
 * @author i01frajos445
 */
public class SpectrumDetector implements SpectrumHandler {

    Method aToCall;

    /**
     * 
     * @param toCall should have one parameter "double[] spectrum"
     * @param dispatcher
     * @param amountOfAmplitudes default = 50
     * @param minFrequency default = 50Hz
     * @param maxFrequency default = 11000 Hz
     */
    public SpectrumDetector(Method toCall, AudioDispatcher dispatcher, int amountOfAmplitudes, double minFrequency, double maxFrequency) {
        aToCall = toCall;

        // add a processor, handle pitch event.
        //detector.dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, Detector.sampleRate, Detector.bufferSize, this));//EVTL algorthmus ändern
        dispatcher.addAudioProcessor(new SpectrumProcessor(Detector.bufferSize, this, amountOfAmplitudes, minFrequency, maxFrequency));

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }

    /**
     * 
     * @param toCall should have one parameter "double[] spectrum"
     * @param dispatcher 
     */
    public SpectrumDetector(Method toCall, AudioDispatcher dispatcher) {
        aToCall = toCall;

        // add a processor, handle pitch event.
        //detector.dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, Detector.sampleRate, Detector.bufferSize, this));//EVTL algorthmus ändern
        dispatcher.addAudioProcessor(new SpectrumProcessor(Detector.bufferSize, this));

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }

    @Override
    public void handleSpectrum(double[] spectrum) {
        aToCall.execute(spectrum);
    }

}
