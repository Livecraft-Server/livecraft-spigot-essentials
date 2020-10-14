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

import com.gmail.mediusecho.fusion.api.BukkitCommandSender;
import com.gmail.mediusecho.fusion.api.CommandListener;
import com.gmail.mediusecho.fusion.api.PendingPlayer;
import com.gmail.mediusecho.fusion.api.annotations.*;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.BookUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(argument = "open", contexts = "#book @player")
@SharedPermission("lce.command.modules.book.all")
public class OpenBookCommand extends CommandListener {

    @Inject private BookModule bookModule;

    @Context("#book")
    @Permission(value = "lce.command.modules.book.open.#book", deniedKey = "modules.book.messages.open-permission")
    @Contract("player_only")
    public void openBook (@NotNull BukkitCommandSender sender, String bookName) {
        openBook(sender.getPlayer(), bookName);
    }

    @Context("@player")
    @Permission("lce.command.modules.book.open.player")
    public void openPlayerBook (@NotNull BukkitCommandSender sender, String bookName, @NotNull PendingPlayer player)
    {
        if (!player.isValid())
        {
            sender.sendMessage(Lang.PLAYER_ERROR.get("{1}", player.getName()));
            return;
        }
        openBook(player.getValue(), bookName);
    }

    @Context(value = "#book", providingContext = true)
    public List<String> getBooks (@NotNull BukkitCommandSender sender)
    {
        if (sender.hasPermission("lce.command.*") || sender.hasPermission("lce.command.all")) {
            return new ArrayList<>(bookModule.getBookNames());
        }

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
