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

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class BroadcastTask implements Runnable {

    private final BroadcastModule broadcastModule;
    private int currentMessageIndex = 0;
    private int taskId = -1;

    public BroadcastTask (final BroadcastModule broadcastModule)
    {
        this.broadcastModule = broadcastModule;
    }

    public synchronized void runTaskTimer (Plugin plugin, long delay, long period) {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, delay, period);
    }

    public synchronized void cancel ()
    {
        Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1;
    }

    @Override
    public void run()
    {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) {
            return;
        }

        BaseComponent[] message = broadcastModule.getMessage(currentMessageIndex++);
        if (currentMessageIndex >= broadcastModule.getMessgeCount()) {
            currentMessageIndex = 0;
        }

        if (message == null) {
            return;
        }

        for (Player player : players) {
            player.spigot().sendMessage(message);
        }
    }
}
