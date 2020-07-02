package com.gmail.mediusecho.livecraft_spigot_essentials.modules;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.SettingsValue;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Module implements Listener {

    protected final LivecraftSpigotEssentials plugin;
    protected final SettingsValue<Boolean> enabledFlag;

    protected boolean isEnabled = false;

    public Module (final LivecraftSpigotEssentials plugin, final SettingsValue<Boolean> enabledFlag)
    {
        this.plugin = plugin;
        this.enabledFlag = enabledFlag;
    }

    public boolean isEnabled () {
        return isEnabled;
    }

    public void enable ()
    {
        isEnabled = true;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void disable ()
    {
        isEnabled = false;
        HandlerList.unregisterAll(this);
    }

    public void reload ()
    {
        // Disable if the enable flag is false
        if(isEnabled && !enabledFlag.getValue())
        {
            disable();
            return;
        }

        // Enable if the enable flag is true
        if (!isEnabled && enabledFlag.getValue()) {
            enable();
        }

        // Return if we're still disabled
        if (!isEnabled) {
            return;
        }

        onReload();
    }

    protected abstract void onReload ();
}
