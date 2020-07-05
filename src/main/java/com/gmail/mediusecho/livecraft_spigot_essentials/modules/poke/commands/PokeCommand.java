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

import com.gmail.mediusecho.fusion.annotations.Command;
import com.gmail.mediusecho.fusion.annotations.Context;
import com.gmail.mediusecho.fusion.annotations.Permission;
import com.gmail.mediusecho.fusion.command.BukkitCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.PokeModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Command(argument = "poke", contexts = "@player|ignore|unignore")
public class PokeCommand extends CommandListener {

    private final PokeModule pokeModule;

    public PokeCommand (final PokeModule pokeModule)
    {
        this.pokeModule = pokeModule;
        registerCommand(new PokeIgnoreCommand(pokeModule));
        registerCommand(new PokeUnignoreCommand(pokeModule));
    }

    @Context(context = "@player")
    @Permission(permission = "lce.command.modules.poke.player")
    public void pokePlayer (@NotNull BukkitCommandSender sender)
    {
        List<String> arguments = sender.getFullArguments();
        Player player = Bukkit.getPlayer(arguments.get(1));

        if (player == null || !player.isOnline())
        {
            sender.sendMessage(Lang.PLAYER_ERROR.get().replace("{1}", arguments.get(1)));
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
