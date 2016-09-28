package AudioAnalyzing;

import AudioAnalyzing.Detector.Method;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.util.PitchConverter;
import be.tarsos.dsp.util.fft.FFT;

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

    //Settings
    private static final int AMOUNT_OF_AMPLITUDE_VALUES = 30;

    double pitch = 0;

    public SpectrumDetector(Method toCall, Detector detector) {

        // add a processor, handle pitch event.
        detector.dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, detector.sampleRate, detector.bufferSize, this));//EVTL algorthmus ändern

        AudioProcessor fftProcessor = new AudioProcessor() {
            FFT fft = new FFT(detector.bufferSize);
            float[] amplitudes = new float[detector.bufferSize / 2];

            @Override
            public void processingFinished() {
                // Do Nothing
            }

            @Override
            public boolean process(AudioEvent audioEvent) {
                float[] audioFloatBuffer = audioEvent.getFloatBuffer();
                float[] transformbuffer = new float[detector.bufferSize * 2];
                System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0, audioFloatBuffer.length);
                fft.forwardTransform(transformbuffer);
                fft.modulus(transformbuffer, amplitudes);//Amplituden sind 2048 lang

//                System.out.print("Output: Pitch: " + pitch + ", Amplituden: ");
//                for (float amplitude : amplitudes) {
//                    amplitude *= 1000;
//                    amplitude = Math.round(amplitude);
//                    amplitude /= 1000;
//                    System.out.print(amplitude + ", ");
//                }
//                System.out.println();
//                return true;
                double amplitudeLimit = 11000;
                double maxAmplitude = 0;

                //for every pixel calculate an amplitude
                float[] correctedAmplitudes = new float[AMOUNT_OF_AMPLITUDE_VALUES];
                //iterate the lage arrray and map to pixels
                for (int i = amplitudes.length / 800; i < amplitudes.length; i++) {
                    int index = frequencyToBin(i * 44100 / (amplitudes.length * 8));
                    correctedAmplitudes[index] += amplitudes[i];
                }

                //draw the pixels 
                double[] finished = new double[AMOUNT_OF_AMPLITUDE_VALUES];
                if (amplitudeLimit != 0) {
                    for (int i = 0; i < correctedAmplitudes.length; i++) {
                        finished[i] = (double) (Math.log1p(correctedAmplitudes[i] / amplitudeLimit) / Math.log1p(1.0000001) * 100);
                        maxAmplitude = Math.max(finished[i], maxAmplitude);
                    }
                    finished[1] = maxAmplitude;
                } else {
                    System.err.print("Max Amplitude was null, ignoring line");
                }

                //System.out.println("Amplitude: " + finished[0]);
                //if (finished[0] > 0.75) {
                //    toCall.execute((int) (finished[0] * 100));
                //}
                //        System.out.println("Max Ampl.: " + Math.round(maxAmplitude * 1000) + " ");
//
//                for (double d : finished) {
//                    //d = Math.round(d);
//                    System.out.print(d + ", ");
//                }
//                System.out.println("");
                toCall.execute(finished);

                return true;
            }
        };

        detector.dispatcher.addAudioProcessor(fftProcessor);

        // run the dispatcher (on a new thread).
        new Thread(detector.dispatcher, "Audio dispatching").start();
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

    @Override
    public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
        this.pitch = pitchDetectionResult.getPitch();
    }

}
