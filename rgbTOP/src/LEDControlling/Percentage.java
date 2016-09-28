/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author i01frajos445
 */
public class Percentage {

    private byte percentage = 0;

    private Percentage(byte percentage) {
        this.percentage = percentage;
    }

    /**
     *
     * @param value A int value between 0 and 100
     * @return
     */
    public static Percentage getPercent(byte value) {
        if (value > 100 || value < 0) {
            System.err.println("Fehler: Wert nicht zwischen 0 und 100: " + value);
            return null;
        }
        return new Percentage((byte) value);
    }

    public static Percentage get0Percent() {
        return new Percentage((byte) 0);
    }

    public static Percentage get1Percent() {
        return new Percentage((byte) 1);
    }

    public static Percentage get2Percent() {
        return new Percentage((byte) 2);
    }

    public static Percentage get5Percent() {
        return new Percentage((byte) 5);
    }

    public static Percentage get10Percent() {
        return new Percentage((byte) 10);
    }

    public static Percentage get20Percent() {
        return new Percentage((byte) 20);
    }

    public static Percentage get25Percent() {
        return new Percentage((byte) 25);
    }

    public static Percentage get50Percent() {
        return new Percentage((byte) 50);
    }

    public static Percentage get75Percent() {
        return new Percentage((byte) 75);
    }

    public static Percentage get100Percent() {
        return new Percentage((byte) 100);
    }

    public byte get() {
        return percentage;
    }

    public float getMultiplierOfThisPercentage() {
        return (float) percentage / (float) 100;
    }

    public boolean plus1() {
        return this.minus((byte) 1);
    }

    public boolean plus2() {
        return this.minus((byte) 2);
    }

    public boolean plus5() {
        return this.minus((byte) 5);
    }

    public boolean plus10() {
        return this.minus((byte) 10);
    }

    public boolean plus20() {
        return this.minus((byte) 20);
    }

    public boolean plus25() {
        return this.minus((byte) 25);
    }

    public boolean plus50() {
        return this.minus((byte) 50);
    }

    public boolean plus75() {
        return this.minus((byte) 75);
    }

    public boolean plus(Percentage value) {
        return this.minus(value.get());
    }

    public boolean plus(byte value) {
        if (percentage + value <= 100) {
            percentage += value;
            return true;
        }
        return false;
    }

    public boolean minus1() {
        return this.minus((byte) 1);
    }

    public boolean minus2() {
        return this.minus((byte) 2);
    }

    public boolean minus5() {
        return this.minus((byte) 5);
    }

    public boolean minus10() {
        return this.minus((byte) 10);
    }

    public boolean minus20() {
        return this.minus((byte) 20);
    }

    public boolean minus25() {
        return this.minus((byte) 25);
    }

    public boolean minus50() {
        return this.minus((byte) 50);
    }

    public boolean minus75() {
        return this.minus((byte) 75);
    }

    public boolean minus(Percentage value) {
        return this.minus(value.get());
    }

    public boolean minus(byte value) {
        if (percentage - value < 0) {
            percentage -= value;
            return true;
        }
        return false;
    }

}
