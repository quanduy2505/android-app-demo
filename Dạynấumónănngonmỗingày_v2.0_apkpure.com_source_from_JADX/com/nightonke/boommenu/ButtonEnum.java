package com.nightonke.boommenu;

public enum ButtonEnum {
    SimpleCircle(0),
    TextInsideCircle(1),
    TextOutsideCircle(2),
    Ham(3),
    Unknown(-1);
    
    private final int value;

    private ButtonEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ButtonEnum getEnum(int value) {
        if (value < 0 || value > values().length) {
            return Unknown;
        }
        return values()[value];
    }
}
