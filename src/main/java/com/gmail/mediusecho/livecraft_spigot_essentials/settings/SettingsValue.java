package com.gmail.mediusecho.livecraft_spigot_essentials.settings;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.TypeUtil;
import org.jetbrains.annotations.NotNull;

public class SettingsValue<T> extends SettingsPointer {

    private final LivecraftSpigotEssentials plugin = LivecraftSpigotEssentials.instance;
    private final T defaultValue;
    private final Class<T> type;

    public SettingsValue (final String path, @NotNull T defaultValue)
    {
        super(path);
        this.defaultValue = defaultValue;
        this.type = (Class<T>) defaultValue.getClass();
    }

    public T getValue ()
    {
        String value = plugin.getConfig().getString(path);
        return (T)TypeUtil.parseType(value, type, defaultValue);
    }
}
