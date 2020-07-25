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

package com.gmail.mediusecho.livecraft_spigot_essentials.commands;

import com.gmail.mediusecho.fusion.api.BukkitCommandSender;
import com.gmail.mediusecho.fusion.api.MainCommand;
import com.gmail.mediusecho.fusion.api.annotations.*;
import com.gmail.mediusecho.fusion.api.commands.CommandListener;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

@MainCommand(defaults = "lce.command.*|lce.command.all")
@Command(argument = "lc")
public class LCMainCommand extends CommandListener {

    @Inject private LivecraftSpigotEssentials plugin;

    @Default
    @Permission(permission = "lce.command.lc")
    public void showInfo (@NotNull BukkitCommandSender sender)
    {
        PluginDescriptionFile descriptionFile = plugin.getDescription();
        sender.sendMessage(Lang.INFO.get(
                "{1}", descriptionFile.getName(),
                "{2}", descriptionFile.getVersion()
        ));
    }

    @Command(argument = "reload")
    @Permission(permission = "lce.command.reload")
    public void reload (@NotNull BukkitCommandSender sender)
    {
        plugin.reload();
        sender.sendMessage(Lang.RELOAD.get("{1}", plugin.getDescription().getName()));
    }
}
