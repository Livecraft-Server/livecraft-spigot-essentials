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
import com.gmail.mediusecho.fusion.api.CommandListener;
import com.gmail.mediusecho.fusion.api.PendingPlayer;
import com.gmail.mediusecho.fusion.api.annotations.*;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.PokeData;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.PokeModule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Command(argument = "unignore", contexts = "#ignoring", asContext = true)
@Usage("modules.poke.messages.unignore-usage")
public class PokeUnignoreCommand extends CommandListener {

    @Inject private PokeModule pokeModule;

    @Default
    @Contract("player_only")
    @Permission("lce.command.modules.poke.unignore")
    public void unignorePokes (@NotNull BukkitCommandSender sender, @NotNull PendingPlayer pendingPlayer)
    {
        if (!pendingPlayer.isValid())
        {
            sender.sendMessage(Lang.PLAYER_ERROR.get().replace("{1}", pendingPlayer.getName()));
            return;
        }

        Player player = pendingPlayer.getValue();
        PokeData pokeData = pokeModule.getPokeData(sender.getPlayer().getUniqueId());
        if (pokeData.stopIgnoringPlayer(player))
        {
            sender.sendMessage(Lang.POKE_OPT_IN.get().replace("{1}", player.getDisplayName()));
            return;
        }
        sender.sendMessage(Lang.POKE_NOT_IGNORING.get().replace("{1}", player.getDisplayName()));
    }

    @Context(value = "#ignoring", providingContext = true)
    public List<String> getPlayersNotIgnored(@NotNull BukkitCommandSender sender)
    {
        if (!sender.isPlayer()) {
            return Collections.singletonList("");
        }

        UUID id = sender.getPlayer().getUniqueId();
        PokeData pokeData = pokeModule.getPokeData(id);
        List<String> players = pokeData.getIgnoredPlayers().stream()
                .map(Player::getName).collect(Collectors.toList());

        if (players.isEmpty()) {
            return Collections.singletonList("No one to unignore");
        }
        return players;
    }
}
