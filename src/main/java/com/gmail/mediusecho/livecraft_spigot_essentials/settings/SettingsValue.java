package com.gmail.mediusecho.livecraft_spigot_essentials.settings;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.TypeUtil;

public class SettingsValue<T> extends SettingsPointer {

    private final LivecraftSpigotEssentials plugin = LivecraftSpigotEssentials.instance;
    private final T defaultValue;
    private final Class<T> type;

    public SettingsValue (final String path, T defaultValue)
    {
        super(path);
        this.defaultValue = defaultValue;
        this.type = (Class<T>) defaultValue.getClass();
    }

    public T getValue ()
    {
        String value = plugin.getConfig().getString(path);
        if (value != null) {
            return (T)TypeUtil.parseType(value, type);
        }
        return defaultValue;
    }
}
