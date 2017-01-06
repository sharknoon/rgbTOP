package AudioAnalyzing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Libaries.TarsosDSP.dsp.AudioDispatcher;
import Libaries.TarsosDSP.dsp.AudioProcessor;
import Libaries.TarsosDSP.dsp.io.jvm.JVMAudioInputStream;
import Libaries.TarsosDSP.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author i01frajos445
 */
public class Detector {

    private final AudioDispatcher dispatcher;
    private final Mixer mixer;
    public static final ArrayList<AudioProcessor> PROCESSORS = new ArrayList<>();

    //Default Settings
    static float sampleRate = 44100;
    static int bufferSize = 4096;//= 1024;
    static int overlap = 0;

    public Detector() {
        mixer = getMainMic();
        dispatcher = init(sampleRate, bufferSize, overlap, true);
    }

    public interface Method {

        void execute(Object... parameters);
    }

    /**
     * Dient als Lautstärkemessung, z.B. um vor zu lauter Lautstärke zu warnen
     *
     * @param toCall should have parameter called "double silence"
     */
    public void addSilenceDetector(Method toCall) {
        SilenceDetector silD = new SilenceDetector(toCall, dispatcher);
    }

    /**
     * Dient als Lautstärkemessung, z.B. um vor zu lauter Lautstärke zu warnen
     *
     * @param toCall should have parameter called "double silence"
     * @param threshold default -70
     */
    public void addSilenceDetector(Method toCall, double threshold) {
        SilenceDetector silD = new SilenceDetector(toCall, dispatcher, threshold, false);
    }

    /**
     * Dient als Spektrometer, der wie ein Equalizer alle Frequenzen anzeigt,
     * z.B. für Basserkennung
     *
     * @param toCall should have one parameter "double[] spectrum"
     */
    public void addSpectrumDetector(Method toCall) {
        SpectrumDetector specD = new SpectrumDetector(toCall, dispatcher);
    }

    /**
     * Dient als Spektrometer, der wie ein Equalizer alle Frequenzen anzeigt,
     * z.B. für Basserkennung
     *
     * @param toCall should have one parameter "double[] spectrum"
     * @param amountOfAmplitudes default 50
     * @param minFrequency default 50Hz
     * @param maxFrequency default 11000Hz
     */
    public void addSpectrumDetector(Method toCall, int amountOfAmplitudes, double minFrequency, double maxFrequency) {
        SpectrumDetector specD = new SpectrumDetector(toCall, dispatcher, amountOfAmplitudes, minFrequency, maxFrequency);
    }

    /**
     * Dient als Schlagerkennung, z.B. beim Klatschen
     *
     * @param toCall should have those two parameters "double time", "double
     * salience"
     */
    public void addPercussionDetector(Method toCall) {
        PercussionDetector perD = new PercussionDetector(toCall, dispatcher);
    }

    /**
     * Dient als Schlagerkennung, z.B. beim Klatschen
     *
     * @param toCall should have those two parameters "double time", "double
     * salience"
     * @param sensitivity default 20, Sensitivity of peak detector applied to
     * broadband detection function (%). In [0-100].
     * @param threshold default 8, Energy rise within a frequency bin necessary
     * to count toward broadband total (dB). In [0-20].
     */
    public void addPercussionDetector(Method toCall, double sensitivity, double threshold) {
        PercussionDetector perD = new PercussionDetector(toCall, dispatcher, sensitivity, threshold);
    }

    /**
     * Dient als Stimmlageerkennung, z.B. für Singprogramme
     *
     * @param toCall should have 3 parameters "float pitch", "float
     * probability", "double rms"
     */
    public void addPitchDetector(Method toCall) {
        PitchDetector pitD = new PitchDetector(toCall, dispatcher);
    }

    /**
     * Dient als Oscilloskop, der angezeigt werden kann, dient z.B. zur
     * Beaterkennung
     *
     * @param toCall should have a parameter called "float[] data"
     */
    public void addOscilloscopeDetector(Method toCall) {
        OscilloscopeDetector oscD = new OscilloscopeDetector(toCall, dispatcher);
    }

    public static final int AMDF = 0;
    public static final int DYNAMIC_WAVELET = 1;
    public static final int FFT_PITCH = 2;
    public static final int FFT_YIN = 3;
    public static final int MPM = 4;
    public static final int YIN = 5;

    /**
     * Dient als Stimmlageerkennung, z.B. für Singprogramme
     *
     * @param toCall should have 3 parameters "float pitch", "float
     * probability", "double rms"
     * @param algorithm use Detector.AMDF as an example
     */
    public void addPitchDetector(Method toCall, int algorithm) {
        AudioDispatcher pitchDispatcher = init(sampleRate, bufferSize, overlap, true);
        PitchEstimationAlgorithm algo = PitchEstimationAlgorithm.YIN;
        switch (algorithm) {
            case 0:
                algo = PitchEstimationAlgorithm.AMDF;
                break;
            case 1:
                algo = PitchEstimationAlgorithm.DYNAMIC_WAVELET;
                break;
            case 2:
                algo = PitchEstimationAlgorithm.FFT_PITCH;
                break;
            case 3:
                algo = PitchEstimationAlgorithm.FFT_YIN;
                break;
            case 4:
                algo = PitchEstimationAlgorithm.MPM;
                break;
            case 5:
                algo = PitchEstimationAlgorithm.YIN;
                break;
        }
        PitchDetector pitD = new PitchDetector(toCall, pitchDispatcher, algo);
    }

    public void removeAllDetectors() {
        PROCESSORS.stream().forEach((processor) -> {
            try {
                dispatcher.removeAudioProcessor(processor);
            } catch (Exception e) {
            }
        });
    }

    private AudioDispatcher init(float sampleRate, int bufferSize, int bufferOverlap, boolean bigEndian) {
        AudioDispatcher aDispatcher = null;
        try {
            System.out.println("Started listening with " + mixer.getMixerInfo().getName());

            final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, bigEndian);//Bei Spectogram bigendian = false
            TargetDataLine line = (TargetDataLine) mixer.getLine(new DataLine.Info(TargetDataLine.class, format));
            line.open(format, bufferSize);
            line.start();

            // create a new dispatcher
            aDispatcher = new AudioDispatcher(new JVMAudioInputStream(new AudioInputStream(line)), bufferSize, bufferOverlap);
            System.out.println("fertig");

        } catch (LineUnavailableException ex) {
            System.err.println("Could not load MixerLine: " + ex);
        } catch (NullPointerException e) {
            System.err.println("Nullpointer: " + e);
        }
        return aDispatcher;
    }

    private static Mixer getMainMic() {
        Mixer aMixer = null;
        for (final Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            if ((aMixer = AudioSystem.getMixer(mixerInfo)).getTargetLineInfo().length != 0) {
                // Mixer capable of recording audio if target line length != 0
                return aMixer;
            }
        }
        System.err.println("Es konnte kein Mikrophon gefunden werden!");
        return aMixer;
    }

    private static Mixer getMainSpeaker() {
        Mixer aMixer = null;
        for (final Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            if ((aMixer = AudioSystem.getMixer(mixerInfo)).getSourceLineInfo().length != 0) {
                // Mixer capable of audio play back if source line length != 0
                return aMixer;
            }
        }
        System.err.println("Es konnte kein Lautsprecher gefunden werden!");
        return aMixer;
    }
}
