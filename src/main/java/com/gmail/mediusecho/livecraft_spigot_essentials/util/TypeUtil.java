package com.gmail.mediusecho.livecraft_spigot_essentials.util;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TypeUtil {

    private static final Map<Class<?>, Function<String, ?>> parserMap = new HashMap<>();
    static
    {
        parserMap.put(Boolean.class, Boolean::parseBoolean);
        parserMap.put(Float.class, Float::parseFloat);
        parserMap.put(Double.class, Double::parseDouble);
        parserMap.put(Integer.class, Integer::parseInt);
        parserMap.put(Particle.class, Particle::valueOf);
        parserMap.put(Sound.class, Sound::valueOf);
    }

    public static Object parseType (String value, Class<?> clazz)
    {
        Function<String, ?> func = parserMap.get(clazz);
        if (func != null) {
            return func.apply(value);
        }
        throw new UnsupportedOperationException("Cannot parse the string: " + value);
    }
}
