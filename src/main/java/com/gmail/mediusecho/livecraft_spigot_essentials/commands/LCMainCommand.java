package com.gmail.mediusecho.livecraft_spigot_essentials.commands;

import com.gmail.mediusecho.fusion.annotations.*;
import com.gmail.mediusecho.fusion.command.BukkitCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

@MainCommand
@Command(argument = "lc")
public class LCMainCommand extends CommandListener {

    private final LivecraftSpigotEssentials plugin;

    public LCMainCommand (final LivecraftSpigotEssentials plugin)
    {
        this.plugin = plugin;
    }

    @Default
    @Permission(permission = "lce.command.lc")
    public void showInfo (@NotNull BukkitCommandSender sender)
    {
        PluginDescriptionFile descriptionFile = plugin.getDescription();
        sender.sendMessage(Lang.INFO.get()
                .replace("{1}", descriptionFile.getName())
                .replace("{2}", descriptionFile.getVersion()));
    }

    @Command(argument = "reload")
    public class LCReloadCommand extends CommandListener {

        @Default
        @Permission(permission = "lce.command.reload")
        public void reload (@NotNull BukkitCommandSender sender)
        {
            plugin.reload();
            sender.sendMessage(Lang.RELOAD.get()
                    .replace("{1}", plugin.getDescription().getName()));
        }
    }
}