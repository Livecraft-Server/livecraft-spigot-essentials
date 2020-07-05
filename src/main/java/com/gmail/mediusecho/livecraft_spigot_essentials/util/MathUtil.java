package com.gmail.mediusecho.livecraft_spigot_essentials.util;

public class MathUtil {

    /**
     * Get a random offset value
     *
     * @return Returns a random double from -range to range
     */
    public static double getRandomOffset(double range) {
        return Math.random() - range;
    }
}
