package AudioAnalyzing;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.util.PitchConverter;
import be.tarsos.dsp.util.fft.FFT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    double pitch = 0;

    public SpectrumDetector() {
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

                double maxAmplitude = 0;
                //for every pixel calculate an amplitude
                float[] pixeledAmplitudes = new float[100];
                //iterate the lage arrray and map to pixels
                for (int i = amplitudes.length / 800; i < amplitudes.length; i++) {
                    int pixelY = frequencyToBin(i * 44100 / (amplitudes.length * 8));
                    pixeledAmplitudes[pixelY] += amplitudes[i];
                    maxAmplitude = Math.max(pixeledAmplitudes[pixelY], maxAmplitude);
                }

                //draw the pixels 
                if (maxAmplitude != 0) {
                    for (int i = 0; i < pixeledAmplitudes.length; i++) {
                        final int greyValue = (int) (Math.log1p(pixeledAmplitudes[i] / maxAmplitude) / Math.log1p(1.0000001) * 100);
                        System.out.print(greyValue + ", ");
                    }
                }
                System.out.println();

//                System.out.print("Output: Amplituden: ");
//                for (float amplitude : amplitudes) {//2048
//                    amplitude *= 1000;
//                    amplitude = Math.round(amplitude);
//                    amplitude /= 1000;
//                    System.out.print(amplitude + ", ");
//                }
//
//                System.out.println();
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
                binEstimate = (absCent - minCent) / maxCent * 100;
            } else {
                binEstimate = (frequency - minFrequency) / maxFrequency * 100;
            }
            if (binEstimate > 700) {
                System.out.println("Binestimate: " + binEstimate + "");
            }
            bin = 100 - 1 - (int) binEstimate;
        }
        return bin;
    }

    public static void main(String[] args) {
        SpectrumDetector spectrumDetector = new SpectrumDetector();
    }

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        if (pitchDetectionResult.isPitched()) {
            pitch = pitchDetectionResult.getPitch();
        } else {
            pitch = -1;
        }
    }

}
