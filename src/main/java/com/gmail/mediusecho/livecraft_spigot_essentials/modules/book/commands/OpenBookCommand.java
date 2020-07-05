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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.commands;

import com.gmail.mediusecho.fusion.annotations.Command;
import com.gmail.mediusecho.fusion.annotations.Context;
import com.gmail.mediusecho.fusion.annotations.Permission;
import com.gmail.mediusecho.fusion.annotations.SenderPolicy;
import com.gmail.mediusecho.fusion.command.BukkitCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.fusion.commands.properties.Sender;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.BookUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(argument = "open", contexts = "#book @player")
public class OpenBookCommand extends CommandListener {

    private final BookModule bookModule;

    public OpenBookCommand (final BookModule bookModule)
    {
        this.bookModule = bookModule;
    }

    @Context(context = "#book")
    @Permission(permission = "lce.command.modules.book.open.#book", permissionDeniedKey = "modules.book.messages.open-permission")
    @SenderPolicy(Sender.PLAYER_ONLY)
    public void openBook (@NotNull BukkitCommandSender sender) {
        openBook(sender.getPlayer(), sender.getFullArguments().get(2));
    }

    @Context(context = "@player")
    @Permission(permission = "lce.command.modules.book.open.player")
    public void openPlayerBook (@NotNull BukkitCommandSender sender)
    {
        List<String> arguments = sender.getFullArguments();
        Player player = Bukkit.getPlayer(arguments.get(3));

        if (player == null || !player.isOnline())
        {
            sender.sendMessage(Lang.PLAYER_ERROR.get("{1}", arguments.get(3)));
            return;
        }

        openBook(player, arguments.get(2));
    }

    @Context(context = "#book", providingContext = true)
    public List<String> getBooks (BukkitCommandSender sender)
    {
        List<String> books = new ArrayList<>();
        for (String name : bookModule.getBookNames())
        {
            if (sender.hasPermission("lce.command.modules.book.open." + name)) {
                books.add(name);
            }
        }
        return books;
    }

    private void openBook (Player player, String bookName)
    {
        if (!bookModule.bookExists(bookName))
        {
            player.sendMessage(Lang.BOOK_MISSING.get("{1}", bookName));
            return;
        }

        ItemStack book = bookModule.getBook(bookName);
        if (book == null)
        {
            player.sendMessage(Lang.BOOK_MISSING.get("{1}", bookName));
            return;
        }

        BookUtil.openBook(book, player);
    }
}
