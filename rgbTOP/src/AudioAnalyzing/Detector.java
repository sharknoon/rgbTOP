package AudioAnalyzing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Main.Controller;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author i01frajos445
 */
public class Detector {

    public AudioDispatcher dispatcher;

    public static int MAINMIC = 0;
    public static int MAINSPEAKER = 1;

    public static int SILENCEDETECTOR = 0;
    public static int SPECTRUMDETECTOR = 1;

    //Settings
    static float defaultSampleRate = 44100;
    static int defaultBufferSize = 512;
    static int defaultOverlap = 0;

    float sampleRate = defaultSampleRate;
    int bufferSize = defaultBufferSize;
    int overlap = defaultOverlap;

    public Detector(int mode) {
        this.start(mode);
    }

    public Detector(int mode, float pSampleRate, int pBufferSize, int pOverlap) {
        this.sampleRate = pSampleRate;
        this.bufferSize = pBufferSize;
        this.overlap = pOverlap;
        this.start(mode);
    }

    public Object startDetector(Controller controller, int detector) {
        if (detector == 0) {
            return new SilenceDetector(controller, this);
        } else if (detector == 1) {
            return new SpectrumDetector(controller, this);
        }
        return null;
    }

    public void start(int mode) {
        Mixer mixer = null;
        if (mode == MAINMIC) {
            mixer = getMainMic();
        } else if (mode == MAINSPEAKER) {
            mixer = getMainSpeaker();
        }

        try {
            if (dispatcher != null) {
                dispatcher.stop();
            }

            System.out.println("Started listening with " + mixer.getMixerInfo().getName());

            final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);
            final DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line;
            line = (TargetDataLine) mixer.getLine(dataLineInfo);
            final int numberOfSamples = bufferSize;
            line.open(format, numberOfSamples);
            line.start();
            final AudioInputStream stream = new AudioInputStream(line);

            JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
            // create a new dispatcher
            dispatcher = new AudioDispatcher(audioStream, bufferSize, overlap);
            System.out.println("fertig");

        } catch (LineUnavailableException ex) {
            System.err.println("Could not load MixerLine: " + ex);
        } catch (NullPointerException e) {
            System.err.println("Nullpointer: " + e);
        }
    }

    private static Mixer getMainMic() {
        final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        Mixer mixer;
        for (final Mixer.Info mixerInfo : mixerInfos) {
            mixer = AudioSystem.getMixer(mixerInfo);
            if (mixer.getTargetLineInfo().length != 0) {
                // Mixer capable of recording audio if target line length != 0
                return mixer;
            }
        }
        return null;
    }

    private static Mixer getMainSpeaker() {
        final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        Mixer mixer;
        for (final Mixer.Info mixerInfo : mixerInfos) {
            mixer = AudioSystem.getMixer(mixerInfo);
            if (mixer.getSourceLineInfo().length != 0) {
                // Mixer capable of audio play back if source line length != 0
                return mixer;
            }
        }
        return null;
    }
}
