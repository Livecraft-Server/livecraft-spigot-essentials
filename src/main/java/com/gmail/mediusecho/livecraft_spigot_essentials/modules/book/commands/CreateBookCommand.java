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

import com.gmail.mediusecho.fusion.annotations.*;
import com.gmail.mediusecho.fusion.command.BukkitCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.fusion.commands.properties.Sender;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookModule;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Command(argument = "create", contexts = "book_name")
@Usage("modules.book.messages.general-usage")
public class CreateBookCommand extends CommandListener {

    @Inject private BookModule bookModule;

    @Default
    @Permission(permission = "lce.command.modules.book.create")
    @SenderPolicy(Sender.PLAYER_ONLY)
    public void createBook (@NotNull BukkitCommandSender sender)
    {
        String bookName = sender.getArgument(2);

        if (bookModule.bookExists(bookName))
        {
            sender.sendMessage(Lang.BOOK_EXISTS.get("{1}", bookName));
            return;
        }

        ItemStack book = bookModule.createBook(bookName);
        sender.getPlayer().getInventory().addItem(book);
    }
}
