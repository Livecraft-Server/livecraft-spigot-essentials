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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.commands;

import com.gmail.mediusecho.fusion.api.BukkitCommandSender;
import com.gmail.mediusecho.fusion.api.annotations.*;
import com.gmail.mediusecho.fusion.api.commands.CommandListener;
import com.gmail.mediusecho.fusion.api.commands.Sender;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.PokeData;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.PokeModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Command(argument = "unignore", contexts = "#ignoring", asContext = true)
@Usage("modules.poke.messages.unignore-usage")
public class PokeUnignoreCommand extends CommandListener {

    @Inject private PokeModule pokeModule;

    @Default
    @SenderPolicy(Sender.PLAYER_ONLY)
    @Permission(permission = "lce.command.modules.poke.unignore")
    public void unignorePokes (@NotNull BukkitCommandSender sender)
    {
        String playerName = sender.getArgument(2);
        Player player = Bukkit.getPlayer(playerName);

        if (player == null || !player.isOnline())
        {
            sender.sendMessage(Lang.PLAYER_ERROR.get().replace("{1}", playerName));
            return;
        }

        PokeData pokeData = pokeModule.getPokeData(sender.getPlayer().getUniqueId());
        if (pokeData.stopIgnoringPlayer(player))
        {
            sender.sendMessage(Lang.POKE_OPT_IN.get().replace("{1}", player.getDisplayName()));
            return;
        }
        sender.sendMessage(Lang.POKE_NOT_IGNORING.get().replace("{1}", player.getDisplayName()));
    }

    @Context(context = "#ignoring", providingContext = true)
    public List<String> getPlayersNotIgnored(@NotNull BukkitCommandSender sender)
    {
        if (!sender.isPlayer()) {
            return Collections.singletonList("");
        }

        UUID id = sender.getPlayer().getUniqueId();
        PokeData pokeData = pokeModule.getPokeData(id);
        List<Player> ignoredPlayers = pokeData.getIgnoredPlayers();
        List<String> results = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (!ignoredPlayers.contains(player)) {
                continue;
            }
            results.add(player.getName());
        }

        if (results.isEmpty()) {
            return Collections.singletonList("No one to unignore");
        }
        return results;
    }
}
