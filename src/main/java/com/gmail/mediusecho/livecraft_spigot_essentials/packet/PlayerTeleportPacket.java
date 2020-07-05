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

package com.gmail.mediusecho.livecraft_spigot_essentials.packet;

import com.google.common.io.ByteArrayDataInput;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerTeleportPacket {

    private UUID playerId;
    private Player player;
    private Location location;
    private boolean isPendingTeleport = false;

    public PlayerTeleportPacket (@NotNull final ByteArrayDataInput in)
    {
        String playerId = in.readUTF();
        String worldName = in.readUTF();
        double x = in.readDouble();
        double y = in.readDouble();
        double z = in.readDouble();
        float yaw = (float) in.readDouble();
        float pitch = (float) in.readDouble();

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return;
        }

        location = new org.bukkit.Location(world, x, y, z, yaw, pitch);
        this.playerId = UUID.fromString(playerId);
        player = Bukkit.getPlayer(this.playerId);
        isPendingTeleport = (player == null || !player.isOnline());
    }

    public UUID getPlayerId () {
        return playerId;
    }

    public Player getPlayer () {
        return player;
    }

    /**
     * Gets the location of this teleport packet
     *
     * @return
     */
    @Nullable
    public Location getLocation () {
        return location;
    }

    /**
     * Returns true if the target player for this teleport packet is not yet online
     *
     * @return
     *      Boolean
     */
    public boolean isPendingTeleport () {
        return isPendingTeleport;
    }
}
