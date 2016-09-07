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

/**
 * <p>
 * This example code demonstrates how to setup a hardware supported PWM pin
 * GpioProvider
 * </p>
 *
 * @author Robert Savage
 */
public class Controller {

    /**
     * [ARGUMENT/OPTION "--pin (#)" | "-p (#)" ] This example program accepts an
     * optional argument for specifying the GPIO pin (by number) to use with
     * this GPIO listener example. If no argument is provided, then GPIO #1 will
     * be used. -- EXAMPLE: "--pin 4" or "-p 0".
     *
     * @param args
     */
    public static void main(String[] args) {

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
        
        GpioPinPwmOutput pwmRedLED = gpio.provisionPwmOutputPin(pinRedLED);
        GpioPinPwmOutput pwmGreenLED = gpio.provisionPwmOutputPin(pinGreenLED);
        GpioPinPwmOutput pwmBlueLED = gpio.provisionPwmOutputPin(pinBlueLED);

        // you can optionally use these wiringPi methods to further customize the PWM generator
        // see: http://wiringpi.com/reference/raspberry-pi-specifics/
        Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        Gpio.pwmSetRange(1000);
        Gpio.pwmSetClock(500);

        // set the PWM rate to 500
        pwmRedLED.setPwm(500);
        pwmGreenLED.setPwm(500);
        pwmBlueLED.setPwm(500);
        console.println("PWM rate is: " + pwmRedLED.getPwm());

        console.println("Press ENTER to set the PWM to a rate of 250");
        System.console().readLine();

        // set the PWM rate to 250
        pwmRedLED.setPwm(250);
        pwmGreenLED.setPwm(250);
        pwmBlueLED.setPwm(250);
        console.println("PWM rate is: " + pwmRedLED.getPwm());

        console.println("Press ENTER to set the PWM to a rate to 0 (stop PWM)");
        System.console().readLine();

        // set the PWM rate to 0
        pwmRedLED.setPwm(0);
        pwmGreenLED.setPwm(0);
        pwmBlueLED.setPwm(0);
        console.println("PWM rate is: " + pwmRedLED.getPwm());

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();
    }
}
