package AudioAnalyzing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import be.hogent.tarsos.dsp.AudioProcessor;
import be.hogent.tarsos.dsp.PitchProcessor;
import be.hogent.tarsos.dsp.PitchProcessor.DetectedPitchHandler;
import be.hogent.tarsos.dsp.PitchProcessor.PitchEstimationAlgorithm;
import be.hogent.tarsos.dsp.util.FFT;

/**
 *
 * @author i01frajos445
 */
public class SpectrumDetector implements DetectedPitchHandler {

    double pitch = 0;
    
    public SpectrumDetector() {
        Detector dec = new Detector(Detector.MAINMIC, Detector.defaultSampleRate, Detector.defaultBufferSize * 8, 768 * 4);

        // add a processor, handle pitch event.
        dec.dispatcher.addAudioProcessor(new PitchProcessor(PitchEstimationAlgorithm.YIN, dec.sampleRate, dec.bufferSize, dec.overlap, 0, this));//EVTL algorthmus ändern

        AudioProcessor fftProcessor = new AudioProcessor() {
            FFT fft = new FFT(dec.bufferSize);
            float[] amplitudes = new float[dec.bufferSize / 2];

            @Override
            public boolean processFull(float[] audioFloatBuffer, byte[] audioByteBuffer) {
                processOverlapping(audioFloatBuffer, audioByteBuffer);
                return true;
            }

            @Override
            public boolean processOverlapping(float[] audioFloatBuffer, byte[] audioByteBuffer) {
                float[] transformbuffer = new float[dec.bufferSize * 2];
                System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0, audioFloatBuffer.length);
                fft.forwardTransform(transformbuffer);
                fft.modulus(transformbuffer, amplitudes);//Amplituden sind 2048 lang
                
                System.out.print("Output: Pitch: "+pitch+", Amplituden: ");
                for (float amplitude : amplitudes) {
                    amplitude *= 1000;
                    amplitude = Math.round(amplitude);
                    amplitude /= 1000;
                    System.out.print(amplitude+", ");
                }
                System.out.println();
                return true;
            }

            @Override
            public void processingFinished() {
                // Do Nothing
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
    public void handlePitch(float pitch, float probability, float timeStamp, float progress) {
        this.pitch = pitch;
    }

}
