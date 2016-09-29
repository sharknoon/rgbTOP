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

    private final Mixer mixer;

    //Default SilenceSettings
    static float silenceSampleRate = 44100;
    static int silenceBufferSize = 512;
    static int silenceOverlap = 0;
    //Default SpectrumSettings
    static float spectrumSampleRate = 44100;
    static int spectrumBufferSize = 1024 * 4;
    static int spectrumOverlap = 768 * 4;
    //Default PercussionSettings
    static float percussionSampleRate = 44100;
    static int percussionBufferSize = 512;
    static int percussionOverlap = 0;
    //Default PitchSettings
    static float pitchSampleRate = 44100;
    static int pitchBufferSize = 1024;
    static int pitchOverlap = 0;

    public Detector() {
        mixer = getMainMic();
    }

    public interface Method {

        void execute(Object... parameters);
    }

    /**
     * Dient als Lautstärkemessung, z.B. um vor zu lauter Lautstärke zu warnen
     * @param toCall should have parameter called "double silence"
     */
    public void addSilenceDetector(Method toCall) {
        AudioDispatcher silenceDispatcher = init(silenceSampleRate, silenceBufferSize, silenceOverlap);
        SilenceDetector silD = new SilenceDetector(toCall, silenceDispatcher);
    }

    /**
     * Dient als Spektrometer, der wie ein Equalizer alle Frequenzen anzeigt, z.B. für Basserkennung
     * @param toCall should have one parameter "double[] spectrum"
     */
    public void addSpectrumDetector(Method toCall) {
        AudioDispatcher spectrumDdispatcher = init(spectrumSampleRate, spectrumBufferSize, spectrumOverlap);
        SpectrumDetector specD = new SpectrumDetector(toCall, spectrumDdispatcher);
    }

    /**
     * Dient als Schlagerkennung, z.B. beim Klatschen
     * @param toCall should have those two parameters "double time", "double salience"
     */
    public void addPercussionDetector(Method toCall) {
        AudioDispatcher percussionDispatcher = init(percussionSampleRate, percussionBufferSize, percussionOverlap);
        PercussionDetector perD = new PercussionDetector(toCall, percussionDispatcher);
    }

    /**
     * Dient als Stimmlageerkennung, z.B. für Singprogramme
     * @param toCall should have 3 parameters "float pitch", "float probability", "double rms"
     */
    public void addPitchDetector(Method toCall) {
        AudioDispatcher pitchDispatcher = init(pitchSampleRate, pitchBufferSize, pitchOverlap);
        PitchDetector pitD = new PitchDetector(toCall, pitchDispatcher);
    }

    private AudioDispatcher init(float sampleRate, int bufferSize, int bufferOverlap) {
        AudioDispatcher dispatcher = null;
        try {
            System.out.println("Started listening with " + mixer.getMixerInfo().getName());

            final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, true);//Bei Spectogram bigendian = false
            TargetDataLine line = (TargetDataLine) mixer.getLine(new DataLine.Info(TargetDataLine.class, format));
            line.open(format, bufferSize);
            line.start();

            // create a new dispatcher
            dispatcher = new AudioDispatcher(new JVMAudioInputStream(new AudioInputStream(line)), bufferSize, bufferOverlap);
            System.out.println("fertig");

        } catch (LineUnavailableException ex) {
            System.err.println("Could not load MixerLine: " + ex);
        } catch (NullPointerException e) {
            System.err.println("Nullpointer: " + e);
        }
        return dispatcher;
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
