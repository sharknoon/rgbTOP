package Main;


import AudioAnalyzing.SilenceDetector;
import LEDControlling.LEDs;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author i01frajos445
 */
public class Controller implements Interfaces.SilenceDetector {
    
    boolean silence;
    
    public Controller(String[] args){
        //LEDs leds = new LEDs(args);
        SilenceDetector spd = new SilenceDetector(this);
    }
    
    @Override
    public void silenceChanged(boolean newSilence){
        silence = newSilence;
    }
    
    public static void main(String[] args) {
        Controller controller = new Controller(args);
    }
    
}