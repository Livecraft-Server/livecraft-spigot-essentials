package com.gmail.mediusecho.livecraft_spigot_essentials.modules.ping.groups;

import com.gmail.mediusecho.livecraft_spigot_essentials.Permission;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Group {

    protected final String name;
    protected final String highlight;
    protected List<String> aliases;

    public Group (final String name, final String highlight)
    {
        this.name = name;
        this.highlight = ChatColor.translateAlternateColorCodes('&', highlight);
        aliases = new ArrayList<>();
    }

    /**
     * Adds this alias to the group's alias list
     *
     * @param alias
     *      Alias to add
     */
    public void addAlias (String alias)
    {
        if (aliases.contains(alias)) {
            return;
        }
        aliases.add(alias);
    }

    /**
     * Parses this message for any group @pings
     *
     * @param message
     *      The message to parse
     * @return
     *      Returns the parsed message
     */
    public String parseMessage (String mention, String name, String message, @Nullable Player player, List<Player> pingedPlayers)
    {
        if (player != null) {
            if (!Permission.PING_CHAT_NOTIFY_GROUP.hasPermission(player, this.name)) {
                return message;
            }
        }

        if (!aliases.contains(name)) {
            return message;
        }

        String prefix = message.substring(0, message.indexOf(mention));
        String suffix = StringUtil.getLastUsedColorCode(prefix);

        message = message.replaceFirst(mention, highlight.replace("{1}", mention) + suffix);
        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (!Permission.PING_CHAT_RECEIVE_GROUP.hasPermission(p, this.name)) {
                continue;
            }
            pingedPlayers.add(p);
        }

        return message;
    }
}
