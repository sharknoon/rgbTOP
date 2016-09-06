package AudioAnalyzing;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.util.fft.FFT;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
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
                System.out.print("Output: Amplituden: ");
                for (float amplitude : amplitudes) {
                    amplitude *= 1000;
                    amplitude = Math.round(amplitude);
                    amplitude /= 1000;
                    System.out.print(amplitude + ", ");
                }
                System.out.println();
                return true;
            }
        };

        dec.dispatcher.addAudioProcessor(fftProcessor);

        // run the dispatcher (on a new thread).
        new Thread(dec.dispatcher, "Audio dispatching").start();
    }

    public static void main(String[] args) {
        new SpectrumDetector();
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
