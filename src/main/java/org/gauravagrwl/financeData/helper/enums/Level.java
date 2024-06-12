package org.gauravagrwl.financeData.helper.enums;

import lombok.Getter;

@Getter
public enum Level {
    LEVEL_ONE("levelOne", 1),
    LEVEL_TWO("levelTwo", 2),
    LEVEL_THREE("levelThree", 3),
    LEVEL_FOUR("levelFour", 4),

    ;

    private final String value;

    private final int code;

    Level(String value, int code) {
        this.value = value;
        this.code = code;
    }

    public static Level fromValue(String level) {
        for (Level status : Level.values()) {
            if (status.value.equals(level)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + level);
    }

    public static Level fromCode(int code) {
        for (Level status : Level.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

}
