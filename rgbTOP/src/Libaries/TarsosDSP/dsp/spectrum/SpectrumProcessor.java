/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Libaries.TarsosDSP.dsp.spectrum;

import Libaries.TarsosDSP.dsp.AudioEvent;
import Libaries.TarsosDSP.dsp.AudioProcessor;
import Libaries.TarsosDSP.dsp.util.PitchConverter;
import Libaries.TarsosDSP.dsp.util.fft.FFT;

/**
 *
 * @author Josua Frank
 */
public class SpectrumProcessor implements AudioProcessor {

    private int amountOfAmplitudes = 50;
    private int bufferSize;// default 1024 * 4;
    private double minFrequency = 50;//Hz
    private double maxFrequency = 11000;//Hz
    private final FFT fft;
    private final float[] amplitudes;
    private final SpectrumHandler handler;

    /**
     *
     * @param buffersize default 1024*4
     * @param amountOfAmplitudes default 50
     * @param minFrequency default 50 Hz
     * @param maxFrequency default 11000 Hz
     * @param handler
     */
    public SpectrumProcessor(int buffersize, SpectrumHandler handler, int amountOfAmplitudes, double minFrequency, double maxFrequency) {
        this.bufferSize = buffersize;
        this.amountOfAmplitudes = amountOfAmplitudes;
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
        this.handler = handler;
        fft = new FFT(bufferSize);
        amplitudes = new float[bufferSize / 2];
    }

    /**
     *
     * @param buffersize default 1024*4
     * @param handler
     */
    public SpectrumProcessor(int buffersize, SpectrumHandler handler) {
        this.bufferSize = buffersize;
        this.handler = handler;
        fft = new FFT(bufferSize);
        amplitudes = new float[bufferSize / 2];
    }

    @Override
    public void processingFinished() {
        // Do Nothing
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioFloatBuffer = audioEvent.getFloatBuffer();
        float[] transformbuffer = new float[bufferSize * 2];
        System.arraycopy(audioFloatBuffer, 0, transformbuffer, 0, audioFloatBuffer.length);
        fft.forwardTransform(transformbuffer);
        fft.modulus(transformbuffer, amplitudes);//Amplituden sind 2048 lang

        double maxAmplitude = 0;

        //for every pixel calculate an amplitude
        float[] correctedAmplitudes = new float[amountOfAmplitudes];
        //iterate the lage arrray and map to pixels
        for (int i = amplitudes.length / 800; i < amplitudes.length; i++) {
            int index = frequencyToBin(i * 44100 / (amplitudes.length * 8));
            correctedAmplitudes[index] += amplitudes[i];
        }

//        //draw the pixels 
//        double[] finished = new double[amountOfAmplitudes];
//        for (int i = 0; i < correctedAmplitudes.length; i++) {
//            finished[i] = (double) (Math.log1p(correctedAmplitudes[i] / 40000) / Math.log1p(1.0000001) * 100);//Muss hier noch schauen
//            maxAmplitude = Math.max(finished[i], maxAmplitude);
//        }
//        finished[1] = maxAmplitude;
        double[] finished = new double[correctedAmplitudes.length];
        for (int i = 0; i < correctedAmplitudes.length; i++) {
            finished[i] = (double) (correctedAmplitudes[i] / (double) 400.0);
        }

        handler.handleSpectrum(finished);

        return true;
    }

    private int frequencyToBin(final double frequency) {
        int bin = 0;
        final boolean logaritmic = true;
        if (frequency != 0 && frequency > minFrequency && frequency < maxFrequency) {
            double binEstimate;
            if (logaritmic) {
                final double minCent = PitchConverter.hertzToAbsoluteCent(minFrequency);
                final double maxCent = PitchConverter.hertzToAbsoluteCent(maxFrequency);
                final double absCent = PitchConverter.hertzToAbsoluteCent(frequency * 2);
                binEstimate = (absCent - minCent) / maxCent * amountOfAmplitudes;
            } else {
                binEstimate = (frequency - minFrequency) / maxFrequency * amountOfAmplitudes;
            }
            //if (binEstimate > 700) {
            //    System.out.println("Binestimate: " + binEstimate + "");
            //}
            bin = amountOfAmplitudes - 1 - (int) binEstimate;
        }
        return bin;
    }

}
