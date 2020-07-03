package com.gmail.mediusecho.livecraft_spigot_essentials.modules.ping.groups;

import com.gmail.mediusecho.livecraft_spigot_essentials.Permission;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RandomGroup extends Group {

    public RandomGroup(String name, String highlight)
    {
        super(name, highlight);
    }

    @Override
    public String parseMessage(String mention, String name, String message, @Nullable Player player, List<Player> pingedPlayers)
    {
        if (player != null)
        {
            if (!Permission.PING_CHAT_NOTIFY_GROUP.hasPermission(player, name)) {
                return message;
            }
        }

        if (!aliases.contains(name)) {
            return message;
        }

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Optional<? extends Player> optionalPlayer = players.stream().skip((int) (players.size() * Math.random())).findFirst();

        if (optionalPlayer.isPresent())
        {
            Player p = optionalPlayer.get();

            String prefix = message.substring(0, message.indexOf(mention));
            String suffix = StringUtil.getLastUsedColorCode(prefix);

            message = message.replaceFirst(mention, highlight.replace("{1}", "@" + p.getName()) + suffix);
            pingedPlayers.add(p);
        }

        return message;
    }

}
