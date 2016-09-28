/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Libaries.Pi4J.com.pi4j.io.gpio.GpioController;
import Libaries.Pi4J.com.pi4j.io.gpio.GpioFactory;
import Libaries.Pi4J.com.pi4j.io.gpio.GpioPinPwmOutput;
import Libaries.Pi4J.com.pi4j.io.gpio.Pin;
import Libaries.Pi4J.com.pi4j.io.gpio.RaspiPin;
import Libaries.Pi4J.com.pi4j.util.CommandArgumentParser;
import Libaries.Pi4J.com.pi4j.util.Console;
import Libaries.Pi4J.com.pi4j.wiringpi.Gpio;
import java.awt.Color;

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
    private static final float PWM_RANGE = 1024;

    public static void main(String[] args) {
        LEDs leds = new LEDs(args);
    }

    public LEDs(String[] args) {
        start(args);
    }

    /**
     * Sets the Brightness of all LEDs from 0% to 100%
     *
     * @param brightness 0-100
     */
    public void setBrightness(Percentage brightness) {
        setBrightness(brightness, true);
    }

    private void setBrightness(Percentage brightness, boolean refresh) {
        overallBrightness = brightness;
        if (refresh) {
            refresh();
        }
    }

    /**
     * Sets the resulting Color of the three LEDs
     *
     * @param color The Color class from JavaFX
     */
    public void setColor(Color color) {
        setColor(color, true);
    }

    private void setColor(Color color, boolean refresh) {
        brightnessRedLED = Percentage.getPercent((byte) Math.round(map(0, 255, 0, 100, color.getRed())));
        brightnessGreenLED = Percentage.getPercent((byte) Math.round(map(0, 255, 0, 100, color.getGreen())));
        brightnessBlueLED = Percentage.getPercent((byte) Math.round(map(0, 255, 0, 100, color.getBlue())));
        if (refresh) {
            refresh();
        }
    }

    /**
     * Sets the Brightness of all LEDs (check Static Methods of Percentage!!)
     * and the resulting Color of the three LEDs
     *
     * @param color
     * @param brightness
     */
    public void setColorAndBrightness(Color color, Percentage brightness) {
        setColor(color, false);
        setBrightness(brightness, true);
    }

    private void refresh() {
        pwmRedLED.setPwm((int) Math.round(map(0, 100, 0, PWM_RANGE, brightnessRedLED.get()) * overallBrightness.getMultiplierOfThisPercentage()));
        pwmGreenLED.setPwm((int) Math.round(map(0, 100, 0, PWM_RANGE, brightnessGreenLED.get()) * overallBrightness.getMultiplierOfThisPercentage()));
        pwmBlueLED.setPwm((int) Math.round(map(0, 100, 0, PWM_RANGE, brightnessBlueLED.get()) * overallBrightness.getMultiplierOfThisPercentage()));
        System.out.println("Red   PWM rate is: " + pwmRedLED.getPwm() + " of " + PWM_RANGE);
        System.out.println("Green PWM rate is: " + pwmGreenLED.getPwm() + " of " + PWM_RANGE);
        System.out.println("Blue  PWM rate is: " + pwmBlueLED.getPwm() + " of " + PWM_RANGE);
    }

    private double map(double srcMin, double srcMax, double tgtMin, double tgtMax, double value) {
        double srcDifference = srcMax - srcMin;
        double tgtDifference = tgtMax - tgtMin;
        value -= srcMin;
        value /= srcDifference;
        value *= tgtDifference;
        return value += tgtMin;
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

        // All Raspberry Pi models support a hardware PWM pin on GPIO_01. (A, A+, B, B+, 2B, 3B, 0)
        // Raspberry Pi models A+, B+, 2B, 3B also support hardware PWM pins: GPIO_23, GPIO_24, GPIO_26
        //
        // by default we will use gpio pin #01; however, if an argument
        // has been provided, then lookup the pin by address
        Pin pinRedLED = CommandArgumentParser.getPin(
                RaspiPin.class, // pin provider class to obtain pin instance from
                RaspiPin.GPIO_23, // default pin if no pin argument found
                args);             // argument array to search in
        Pin pinGreenLED = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.GPIO_24, args);
        Pin pinBlueLED = CommandArgumentParser.getPin(RaspiPin.class, RaspiPin.GPIO_26, args);

        pwmRedLED = gpio.provisionPwmOutputPin(pinRedLED);
        pwmGreenLED = gpio.provisionPwmOutputPin(pinGreenLED);
        pwmBlueLED = gpio.provisionPwmOutputPin(pinBlueLED);

        // you can optionally use these wiringPi methods to further customize the PWM generator
        // see: http://wiringpi.com/reference/raspberry-pi-specifics/
        Gpio.pwmSetMode(Libaries.Pi4J.com.pi4j.wiringpi.Gpio.PWM_MODE_BAL);
        Gpio.pwmSetRange((int) PWM_RANGE);
        Gpio.pwmSetClock(500);

        // set the PWM rate to 512
        setColor(Color.white);

        console.println("Press ENTER to set Color to Yellow");
        System.console().readLine();

        setColor(Color.yellow);

        console.println("Press ENTER to set the Brightness to 50%");
        System.console().readLine();

        setBrightness(Percentage.get50Percent());

        console.println("Press ENTER to EXIT");
        System.console().readLine();
        
        stop(gpio);
    }

    private void stop(GpioController gpio) {
        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        pwmRedLED.setPwm(0);

        pwmGreenLED.setPwm(0);
        pwmBlueLED.setPwm(0);
        gpio.shutdown();
    }

}
