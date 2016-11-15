package LEDControlling;

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

    public Percentage invert() {
        return Percentage.getPercent((byte) (100 - percentage));
    }

    /**
     *
     * @param value A int value between 0 and 100
     * @return
     */
    public static Percentage getPercent(byte value) {
        if (value > 100) {
            return Percentage.get100Percent();
        } else if (value < 0) {
            return Percentage.get0Percent();
        } else {
            return new Percentage((byte) value);
        }
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

    public Percentage plus1() {
        return this.plus((byte) 1);
    }

    public Percentage plus2() {
        return this.plus((byte) 2);
    }

    public Percentage plus5() {
        return this.plus((byte) 5);
    }

    public Percentage plus10() {
        return this.plus((byte) 10);
    }

    public Percentage plus20() {
        return this.plus((byte) 20);
    }

    public Percentage plus25() {
        return this.plus((byte) 25);
    }

    public Percentage plus50() {
        return this.plus((byte) 50);
    }

    public Percentage plus75() {
        return this.plus((byte) 75);
    }

    public Percentage plus(Percentage value) {
        return this.plus(value.get());
    }

    public Percentage plus(byte value) {
        if (percentage + value <= 100) {
            percentage += value;
        } else {
            percentage = 100;
        }
        return this;
    }

    public Percentage minus1() {
        return this.minus((byte) 1);
    }

    public Percentage minus2() {
        return this.minus((byte) 2);
    }

    public Percentage minus5() {
        return this.minus((byte) 5);
    }

    public Percentage minus10() {
        return this.minus((byte) 10);
    }

    public Percentage minus20() {
        return this.minus((byte) 20);
    }

    public Percentage minus25() {
        return this.minus((byte) 25);
    }

    public Percentage minus50() {
        return this.minus((byte) 50);
    }

    public Percentage minus75() {
        return this.minus((byte) 75);
    }

    public Percentage minus(Percentage value) {
        return this.minus(value.get());
    }

    public Percentage minus(byte value) {
        if (percentage - value >= 0) {
            percentage -= value;
        } else {
            percentage = 0;
        }
        return this;
    }

}
