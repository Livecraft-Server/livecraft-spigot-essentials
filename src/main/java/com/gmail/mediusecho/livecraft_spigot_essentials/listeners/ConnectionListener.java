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

package com.gmail.mediusecho.livecraft_spigot_essentials.listeners;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConnectionListener implements Listener {

    private final LivecraftSpigotEssentials plugin;
    private final Map<UUID, Location> pendingPlayerTeleports;

    public ConnectionListener (@NotNull final LivecraftSpigotEssentials plugin)
    {
        this.plugin = plugin;
        this.pendingPlayerTeleports = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin (@NotNull PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!pendingPlayerTeleports.containsKey(id)) {
            return;
        }

        Location pendingLocation = pendingPlayerTeleports.get(id);
        player.teleport(pendingLocation);

        pendingPlayerTeleports.remove(id);
    }

    public void addPendingPlayerTeleport (UUID id, Location location) {
        pendingPlayerTeleports.put(id, location);
    }
}
