package AudioAnalyzing;

import AudioAnalyzing.SpectrumGUI.SpectrumFXMLController;
import AudioAnalyzing.SpectrumGUI.SpectrumGui;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.util.PitchConverter;
import be.tarsos.dsp.util.fft.FFT;
import com.sun.javafx.application.LauncherImpl;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;

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
public class SpectrumDetector implements PitchDetectionHandler {

    SpectrumGui gui;

    SpectrumFXMLController controller;

    //Settings
    private static final int AMOUNT_OF_AMPLITUDE_VALUES = 30;

    public SpectrumDetector(SpectrumFXMLController pController) {
        controller = pController;
        Detector dec = new Detector(Detector.MAINMIC, Detector.defaultSampleRate, Detector.defaultBufferSize * 8, 768 * 4);

        // add a processor, handle pitch event.
        dec.dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, dec.sampleRate, dec.bufferSize, this));//EVTL algorthmus ändern

        AudioProcessor fftProcessor = new AudioProcessor() {
            FFT fft = new FFT(dec.bufferSize);
            float[] amplitudes = new float[dec.bufferSize / 2];

            @Override
            public void processingFinished() {
                // Do Nothing
            }

            @Override
            public boolean process(AudioEvent audioEvent) {
                float[] audioFloatBuffer = audioEvent.getFloatBuffer();
                float[] transformbuffer = new float[dec.bufferSize * 2];
                System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0, audioFloatBuffer.length);
                fft.forwardTransform(transformbuffer);
                fft.modulus(transformbuffer, amplitudes);

                double maxAmplitude = 11000;

                //for every pixel calculate an amplitude
                float[] correctedAmplitudes = new float[AMOUNT_OF_AMPLITUDE_VALUES];
                //iterate the lage arrray and map to pixels
                for (int i = amplitudes.length / 800; i < amplitudes.length; i++) {
                    int index = frequencyToBin(i * 44100 / (amplitudes.length * 8));
                    correctedAmplitudes[index] += amplitudes[i];
                    //maxAmplitude = Math.max(correctedAmplitudes[index], maxAmplitude);
                }

                //draw the pixels 
                double[] finished = new double[AMOUNT_OF_AMPLITUDE_VALUES];
                if (maxAmplitude != 0) {
                    for (int i = 0; i < correctedAmplitudes.length; i++) {
                        finished[i] = (Math.log1p(correctedAmplitudes[i] / maxAmplitude) / Math.log1p(1.0000001) * 1);
                    }
                } else {
                    System.err.print("Max Amplitude was null, ignoring line");
                }
                controller.setAmplitudes(finished);
                System.out.println();

                return true;
            }
        };

        dec.dispatcher.addAudioProcessor(fftProcessor);

        // run the dispatcher (on a new thread).
        new Thread(dec.dispatcher, "Audio dispatching").start();
    }

    private int frequencyToBin(final double frequency) {
        final double minFrequency = 50; // Hz
        final double maxFrequency = 11000; // Hz
        int bin = 0;
        final boolean logaritmic = true;
        if (frequency != 0 && frequency > minFrequency && frequency < maxFrequency) {
            double binEstimate;
            if (logaritmic) {
                final double minCent = PitchConverter.hertzToAbsoluteCent(minFrequency);
                final double maxCent = PitchConverter.hertzToAbsoluteCent(maxFrequency);
                final double absCent = PitchConverter.hertzToAbsoluteCent(frequency * 2);
                binEstimate = (absCent - minCent) / maxCent * AMOUNT_OF_AMPLITUDE_VALUES;
            } else {
                binEstimate = (frequency - minFrequency) / maxFrequency * AMOUNT_OF_AMPLITUDE_VALUES;
            }
            if (binEstimate > 700) {
                System.out.println("Binestimate: " + binEstimate + "");
            }
            bin = AMOUNT_OF_AMPLITUDE_VALUES - 1 - (int) binEstimate;
        }
        return bin;
    }

    double pitch = 0;

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        if (pitchDetectionResult.isPitched()) {
            pitch = pitchDetectionResult.getPitch();
        } else {
            pitch = -1;
        }
    }

}
