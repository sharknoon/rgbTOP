/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LEDControlling;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;
import com.pi4j.wiringpi.Gpio;
import javafx.scene.paint.Color;

/**
 * <p>
 * This example code demonstrates how to setup a hardware supported PWM pin
 * GpioProvider
 * </p>
 *
 * @author Robert Savage
 */
public class LEDs {

    private Percentage brightnessRedLED;//From 0 - 100
    private Percentage brightnessGreenLED;//From 0 - 100
    private Percentage brightnessBlueLED;//From 0 - 100

    private Percentage overallBrightness;//From 0 - 100

    private GpioPinPwmOutput pwmRedLED;
    private GpioPinPwmOutput pwmGreenLED;
    private GpioPinPwmOutput pwmBlueLED;

    //Settings
    private static final float PWM_RANGE = 1000;

    public LEDs(String[] args) {
       start(args);
    }

    /**
     * Sets the Brightness of all LEDs from 0% to 100%
     *
     * @param brightness 0-100
     */
    public void setBrightness(Percentage brightness) {
        overallBrightness = brightness;
        refresh();
    }

    /**
     * Sets the resulting Color of the three LEDs
     *
     * @param color The Color class from JavaFX
     */
    public void setColor(Color color) {
        brightnessRedLED = Percentage.getPercent((byte) Math.round(color.getRed() * 100));
        brightnessGreenLED = Percentage.getPercent((byte) Math.round(color.getGreen() * 100));
        brightnessBlueLED = Percentage.getPercent((byte) Math.round(color.getBlue() * 100));
        refresh();
    }

    /**
     * Sets the Brightness of all LEDs (check Static Methods of Percentage!!) and the resulting Color of the three LEDs
     * @param color
     * @param brightness 
     */
    public void setColorAndBrightness(Color color, Percentage brightness) {
        overallBrightness = brightness;
        brightnessRedLED = Percentage.getPercent((byte) Math.round(color.getRed() * 100));
        brightnessGreenLED = Percentage.getPercent((byte) Math.round(color.getGreen() * 100));
        brightnessBlueLED = Percentage.getPercent((byte) Math.round(color.getBlue() * 100));
        refresh();
    }

    private void refresh() {
        float multiplicator = ((float) PWM_RANGE / (float) 100) * overallBrightness.getMultiplierOfThisPercentage();
        pwmRedLED.setPwm((int) Math.round((float) brightnessRedLED.get() * multiplicator));
        pwmGreenLED.setPwm((int) Math.round((float) brightnessGreenLED.get() * multiplicator));
        pwmBlueLED.setPwm((int) Math.round((float) brightnessBlueLED.get() * multiplicator));
    }

    /**
     * [ARGUMENT/OPTION "--pin (#)" | "-p (#)" ] This example program accepts an
     * optional argument for specifying the GPIO pin (by number) to use with
     * this GPIO listener example. If no argument is provided, then GPIO #1 will
     * be used. -- EXAMPLE: "--pin 4" or "-p 0".
     *
     * @param args
     */
    private void start(String[] args) {
        brightnessRedLED = Percentage.get0Percent();
        brightnessGreenLED = Percentage.get0Percent();
        brightnessBlueLED = Percentage.get0Percent();

        overallBrightness = Percentage.get100Percent();

        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
        final Console console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "PWM Example");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // create GPIO controller instance
        GpioController gpio = GpioFactory.getInstance();

        // All Raspberry Pi models support a hardware PWM pin on GPIO_01.
        // Raspberry Pi models A+, B+, 2B, 3B also support hardware PWM pins: GPIO_23, GPIO_24, GPIO_26
        //
        // by default we will use gpio pin #01; however, if an argument
        // has been provided, then lookup the pin by address
        Pin pinRedLED = CommandArgumentParser.getPin(
                RaspiPin.class, // pin provider class to obtain pin instance from
                RaspiPin.GPIO_01, // default pin if no pin argument found
                args);             // argument array to search in
        Pin pinGreenLED = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.GPIO_23, args);
        Pin pinBlueLED = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.GPIO_24, args);

        pwmRedLED = gpio.provisionPwmOutputPin(pinRedLED);
        pwmGreenLED = gpio.provisionPwmOutputPin(pinGreenLED);
        pwmBlueLED = gpio.provisionPwmOutputPin(pinBlueLED);

        // you can optionally use these wiringPi methods to further customize the PWM generator
        // see: http://wiringpi.com/reference/raspberry-pi-specifics/
        Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        Gpio.pwmSetRange((int) LEDs.PWM_RANGE);
        Gpio.pwmSetClock(500);

        // set the PWM rate to 500
        pwmRedLED.setPwm(500);
        pwmGreenLED.setPwm(500);
        pwmBlueLED.setPwm(500);
        console.println("PWM rate is: " + pwmRedLED.getPwm());

        console.println("Press ENTER to set Color to Crimson");
        System.console().readLine();

        setColor(Color.CRIMSON);
        console.println("Red   PWM rate is: " + pwmRedLED.getPwm());
        console.println("Green PWM rate is: " + pwmGreenLED.getPwm());
        console.println("Vlue  PWM rate is: " + pwmBlueLED.getPwm());

        console.println("Press ENTER to set the Brightness to 50%");
        System.console().readLine();

        setBrightness(Percentage.get50Percent());
        console.println("Red   PWM rate is: " + pwmRedLED.getPwm());
        console.println("Green PWM rate is: " + pwmGreenLED.getPwm());
        console.println("Vlue  PWM rate is: " + pwmBlueLED.getPwm());

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();
    }

}
