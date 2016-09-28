package AudioAnalyzing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Libaries.TarsosDSP.dsp.AudioDispatcher;
import Libaries.TarsosDSP.dsp.io.jvm.JVMAudioInputStream;
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

    public AudioDispatcher dispatcher;

    public final static int SILENCEDETECTOR = 0;
    public final static int SPECTRUMDETECTOR = 1;

    //Defaults, Settings
    static float sampleRate = 44100;
    static int bufferSize = 512;
    static int overlap = 0;

    final Mixer mixer;

    public Detector() {
        mixer = setMainMic();
        init();
    }

    public Detector(float pSampleRate, int pBufferSize, int pOverlap) {
        Detector.sampleRate = pSampleRate;
        Detector.bufferSize = pBufferSize;
        Detector.overlap = pOverlap;
        mixer = setMainMic();
        init();
    }

    public interface Method {

        void execute(Object... parameters);
    }

    public void addDetector(Method toCall, int detector) {
        switch (detector) {
            case Detector.SILENCEDETECTOR:
                SilenceDetector silD = new SilenceDetector(toCall, this);
                break;
            case Detector.SPECTRUMDETECTOR:
                SpectrumDetector specD= new SpectrumDetector(toCall, this);
                break;
            default:
                System.err.println("Fehler, Detector gibts nicht!!");
                break;
        }
    }

    private void init() {
        try {
            if (dispatcher != null) {
                dispatcher.stop();
            }

            System.out.println("Started listening with " + mixer.getMixerInfo().getName());

            final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
            TargetDataLine line = (TargetDataLine) mixer.getLine(new DataLine.Info(TargetDataLine.class, format));
            line.open(format, bufferSize);
            line.start();

            // create a new dispatcher
            dispatcher = new AudioDispatcher(new JVMAudioInputStream(new AudioInputStream(line)), bufferSize, overlap);
            System.out.println("fertig");

        } catch (LineUnavailableException ex) {
            System.err.println("Could not load MixerLine: " + ex);
        } catch (NullPointerException e) {
            System.err.println("Nullpointer: " + e);
        }
    }

    private static Mixer setMainMic() {
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
