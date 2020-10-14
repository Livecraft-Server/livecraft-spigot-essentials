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
import com.gmail.mediusecho.fusion.api.annotations.*;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.config.CustomConfig;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Command(argument = "save", contexts = "@book")
@Usage("modules.book.messages.general-usage")
@SharedPermission("lce.command.modules.book.all")
public class SaveBookCommand extends CommandListener {

    @Inject private BookModule bookModule;

    private Map<UUID, Boolean> confirmationMap;
    private Map<UUID, String> bookNameMap;

    public SaveBookCommand ()
    {
        confirmationMap = new HashMap<>();
        bookNameMap = new HashMap<>();
    }

    @Default
    @Permission("lce.command.modules.book.save.@book")
    @Contract("player_only")
    public void saveBook (@NotNull BukkitCommandSender sender, String bookName)
    {
        Player player = sender.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (!heldItem.getType().equals(Material.WRITABLE_BOOK))
        {
            sender.sendMessage(Lang.BOOK_UPDATE_ERROR.get());
            return;
        }

        if (!bookModule.bookExists(bookName))
        {
            sender.sendMessage(Lang.BOOK_MISSING.get("{1}", bookName));
            return;
        }

        UUID id = player.getUniqueId();
        if (!hasConfirmed(id))
        {
            sender.sendMessage(Lang.BOOK_CONFIRM.get("{1}", bookName));
            confirmationMap.put(id, true);
            bookNameMap.put(id, bookName);
            return;
        }

        if (!bookNameMap.get(id).equals(bookName))
        {
            sender.sendMessage(Lang.BOOK_SAVE_ERROR.get());
            confirmationMap.remove(id);
            bookNameMap.remove(id);
            return;
        }

        CustomConfig bookConfig = bookModule.getBookConfig(bookName);
        if (bookConfig == null)
        {
            sender.sendMessage(Lang.BOOK_SAVE_ERROR.get());
            confirmationMap.remove(id);
            bookNameMap.remove(id);
            return;
        }

        BookMeta bookMeta = (BookMeta)heldItem.getItemMeta();
        int pages = bookMeta.getPageCount();
        for (int i = 1; i <= pages; i++)
        {
            String page = bookMeta.getPage(i).replaceAll("\\n", "\\\\n");
            bookConfig.set("pages." + i, page);
        }

        bookConfig.save();
        bookConfig.reload();
        bookModule.reload();

        confirmationMap.remove(id);
        bookNameMap.remove(id);

        sender.sendMessage(Lang.BOOK_SAVE_SUCCESS.get("{1}", bookName));
    }

    private boolean hasConfirmed (UUID id)
    {
        if (confirmationMap.containsKey(id)) {
            return confirmationMap.get(id);
        }
        return false;
    }
}
