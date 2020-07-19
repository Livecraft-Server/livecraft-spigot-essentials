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
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.PokeModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Command(argument = "poke", contexts = "@player|ignore|unignore")
@Usage("modules.poke.messages.poke-usage")
public class PokeCommand extends CommandListener {

    @Inject private PokeModule pokeModule;

    public PokeCommand ()
    {
        registerCommand(new PokeIgnoreCommand());
        registerCommand(new PokeUnignoreCommand());
    }

    @Context(context = "@player")
    @Permission(permission = "lce.command.modules.poke.player")
    public void pokePlayer (@NotNull BukkitCommandSender sender)
    {
        String playerName = sender.getArgument(1);
        Player player = Bukkit.getPlayer(playerName);

        if (player == null || !player.isOnline())
        {
            sender.sendMessage(Lang.PLAYER_ERROR.get().replace("{1}", playerName));
            return;
        }

        if (!sender.isPlayer())
        {
            pokeModule.poke(player);
            sender.sendMessage(Lang.POKE_SUCCESS.get().replace("{1}", player.getDisplayName()));
            return;
        }

        UUID id = sender.getPlayer().getUniqueId();
        if (pokeModule.isIgnoringPlayer(player.getUniqueId(), sender.getPlayer())) {
            return;
        }

        if (!pokeModule.canPoke(id)) {
            return;
        }

        pokeModule.poke(player);
        sender.sendMessage(Lang.POKE_SUCCESS.get().replace("{1}", player.getDisplayName()));
    }
}
