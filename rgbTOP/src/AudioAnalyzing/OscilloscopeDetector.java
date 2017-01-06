/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AudioAnalyzing;

import Libaries.TarsosDSP.dsp.AudioDispatcher;
import Libaries.TarsosDSP.dsp.AudioEvent;
import Libaries.TarsosDSP.dsp.Oscilloscope;
import Libaries.TarsosDSP.dsp.Oscilloscope.OscilloscopeEventHandler;

/**
 *
 * @author Josua Frank
 */
public class OscilloscopeDetector implements OscilloscopeEventHandler {

    final Detector.Method toCall;

    /**
     *
     * @param pToCall should have a parameter called "double silence"
     * @param dispatcher
     */
    public OscilloscopeDetector(Detector.Method pToCall, AudioDispatcher dispatcher) {
        toCall = pToCall;

        // add a processor, handle percussion event.
        Oscilloscope oscilloscope = new Oscilloscope(this);
        dispatcher.addAudioProcessor(oscilloscope);
        Detector.PROCESSORS.add(oscilloscope);

        // run the dispatcher (on a new thread).
        new Thread(dispatcher, "Audio dispatching").start();
    }

    @Override
    public void handleEvent(float[] data, AudioEvent event) {
        toCall.execute(data);
    }
//
//    public void paintComponent() {
//            float width = getWidth();
//            float height = getHeight();
//            float halfHeight = height / 2;
//            for (int i = 0; i < data.length; i += 4) {
//                g.drawLine((int) (data[i] * width), (int) (halfHeight - data[i + 1] * height), (int) (data[i + 2] * width), (int) (halfHeight - data[i + 3] * height));
//            }
//    }

}
