package com.gmail.mediusecho.livecraft_spigot_essentials.settings;

import org.bukkit.Particle;
import org.bukkit.Sound;

public class Settings {

    public static final SettingsValue<Boolean> DEBUGGING = new SettingsValue<>("misc.debugging", false);

    public static final SettingsValue<Boolean> POKE_ENABLED = new SettingsValue<>("modules.poke.enabled", true);
    public static final SettingsPointer POKE_PLAYER_IGNORED_LIST_POINTER = new SettingsPointer("modules.poke.ignored");
    public static final SettingsValue<Particle> POKE_PARTICLE = new SettingsValue<>("modules.poke.particle", Particle.HEART);
    public static final SettingsValue<Sound> POKE_SOUND = new SettingsValue<>("modules.poke.sound.id", Sound.ENTITY_CHICKEN_EGG);
    public static final SettingsValue<Float> POKE_SOUND_VOLUME = new SettingsValue<>("modules.poke.sound.volume", 1f);
    public static final SettingsValue<Float> POKE_SOUND_PITCH = new SettingsValue<>("modules.poke.sound.pitch", 1f);
}
