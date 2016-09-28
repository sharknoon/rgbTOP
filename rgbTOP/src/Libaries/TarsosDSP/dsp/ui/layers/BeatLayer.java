/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/

package Libaries.TarsosDSP.dsp.ui.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import Libaries.TarsosDSP.dsp.AudioDispatcher;
import Libaries.TarsosDSP.dsp.AudioEvent;
import Libaries.TarsosDSP.dsp.AudioProcessor;
import Libaries.TarsosDSP.dsp.beatroot.BeatRootOnsetEventHandler;
import Libaries.TarsosDSP.dsp.io.jvm.AudioDispatcherFactory;
import Libaries.TarsosDSP.dsp.onsets.ComplexOnsetDetector;
import Libaries.TarsosDSP.dsp.onsets.OnsetHandler;
import Libaries.TarsosDSP.dsp.ui.Axis;
import Libaries.TarsosDSP.dsp.ui.CoordinateSystem;

public class BeatLayer implements Layer {

	private final List<Double> onsets; // in seconds
	private final List<Double> beats; //in seconds
	private final CoordinateSystem cs; 
	private final Color onsetColor;
	private final Color beatColor;
	private final int frameSize;
	private final int overlap;
	private final File audioFile;
	private final boolean showBeats;
	private final boolean showOnsets;
	

	
	public BeatLayer(CoordinateSystem cs, File audioFile  , boolean showBeats, boolean showOnsets) {
		this.onsets = new ArrayList<Double>();
		this.beats = new ArrayList<Double>();
		this.cs = cs;
		this.frameSize = 256;
		this.overlap = 0;
		this.onsetColor = Color.blue;
		this.beatColor = Color.red;
		this.showBeats = showBeats;
		this.showOnsets = showOnsets;
		this.audioFile = audioFile;	
		initialise();
	}
	
	public void draw(Graphics2D graphics){
		int maxY = Math.round(cs.getMax(Axis.Y));
		int minY = Math.round(cs.getMin(Axis.Y));
		if(!onsets.isEmpty() && showOnsets){
			graphics.setColor(onsetColor);
			for(Double onset : onsets){
				int onsetTime = (int) Math.round(onset*1000);//in ms
				graphics.drawLine(onsetTime,minY, onsetTime, maxY);
			}
		}
		if(!beats.isEmpty() && showBeats ){
			graphics.setColor(beatColor);
			for(Double beat : beats){
				int beatTime = (int) Math.round(beat*1000);//in ms
				graphics.drawLine(beatTime,minY, beatTime, maxY);
			}
		}
	}
	

	public void initialise() {
	
		try {
			AudioDispatcher adp = AudioDispatcherFactory.fromFile(audioFile, frameSize,overlap);
			float sampleRate = adp.getFormat().getSampleRate();
			
			final double lag =  frameSize / sampleRate / 2.0;// in seconds
			final ComplexOnsetDetector detector = new ComplexOnsetDetector(frameSize);
			final BeatRootOnsetEventHandler broeh = new BeatRootOnsetEventHandler();
			adp.addAudioProcessor(detector);
			adp.addAudioProcessor(new AudioProcessor() {
				public void processingFinished() {
					broeh.trackBeats(new OnsetHandler() {
//						@Override
						public void handleOnset(double time, double salience) {
							beats.add(time-lag);
						}
					});
				}
				public boolean process(AudioEvent audioEvent) {
					return true;
				}
			});
			detector.setHandler(new OnsetHandler() {
//				@Override
				public void handleOnset(double time, double salience) {
					onsets.add(time - lag);
					broeh.handleOnset(time - lag, salience);
				}
			});
			new Thread(adp).start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e2){
			e2.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Beats Layer";
	}
}
