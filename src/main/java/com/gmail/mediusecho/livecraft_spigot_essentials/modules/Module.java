/*
 * Copyright (c) 2020 Jacob (MediusEcho)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
