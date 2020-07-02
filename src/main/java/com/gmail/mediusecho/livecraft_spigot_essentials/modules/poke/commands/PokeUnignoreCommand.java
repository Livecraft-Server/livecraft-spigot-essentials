package com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.commands;

import com.gmail.mediusecho.fusion.annotations.*;
import com.gmail.mediusecho.fusion.command.BukkitCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.fusion.commands.properties.Sender;
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
public class PokeUnignoreCommand extends CommandListener {

    private final PokeModule pokeModule;

    public PokeUnignoreCommand (final PokeModule pokeModule)
    {
        this.pokeModule = pokeModule;
    }

    @Default
    @SenderPolicy(Sender.PLAYER_ONLY)
    @Permission(permission = "lce.command.modules.poke.unignore")
    public void unignorePokes (@NotNull BukkitCommandSender sender)
    {
        List<String> arguments = sender.getFullArguments();
        Player player = Bukkit.getPlayer(arguments.get(2));

        if (player == null || !player.isOnline())
        {
            sender.sendMessage(Lang.PLAYER_ERROR.get().replace("{1}", arguments.get(2)));
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
        return results;
    }
}