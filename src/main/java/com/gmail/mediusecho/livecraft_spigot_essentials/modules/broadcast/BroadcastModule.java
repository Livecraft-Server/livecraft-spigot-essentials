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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.broadcast;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class BroadcastModule extends Module {

    private long interval;
    private List<BaseComponent[]> messages;

    private BroadcastTask broadcastTask;

    public BroadcastModule(LivecraftSpigotEssentials plugin)
    {
        super(plugin, Settings.BROADCAST_ENABLED);

        messages = new ArrayList<>();
        broadcastTask = new BroadcastTask(this);
    }

    @Override
    protected void onReload()
    {
        interval = Settings.BROADCAST_INTERVAL.getValue() * 1200;
        broadcastTask.cancel();
        broadcastTask.runTaskTimer(plugin, 0, interval);

        FileConfiguration config = plugin.getConfig();
        messages.clear();
        for (String message : config.getStringList(Settings.BROADCAST_MESSAGE_POINTER.getPath())) {
            messages.add(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
        }
    }

    @Override
    public void disable()
    {
        super.disable();
        broadcastTask.cancel();
    }

    /**
     * Returns the message found at the given index.
     * If the index is out of range then the first message
     * in the list is returned.
     *
     * @param index
     *      The index of the message to get
     * @return
     *      BaseComponent[]
     */
    public BaseComponent[] getMessage (int index)
    {
        if (index < 0 || index >= messages.size()) {
            return messages.get(0);
        }
        return messages.get(index);
    }

    /**
     * Returns how many messages are loaded
     *
     * @return
     *      int
     */
    public int getMessgeCount () {
        return messages.size();
    }
}
