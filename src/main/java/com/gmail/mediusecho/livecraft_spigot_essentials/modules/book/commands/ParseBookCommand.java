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
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.emote.EmoteModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.markdown.MarkdownModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.BookUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Command(argument = "parse")
@SharedPermission("lce.command.modules.book.all")
public class ParseBookCommand extends CommandListener {

    @Inject private EmoteModule emoteModule;
    @Inject private MarkdownModule markdownModule;

    @Default
    @Permission("lce.command.modules.book.parse")
    public void parseBook (@NotNull BukkitCommandSender sender)
    {
        Player player = sender.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem.getType() != Material.WRITABLE_BOOK)
        {
            sender.sendMessage(Lang.BOOK_PARSE_ERROR.get());
            return;
        }

        BookMeta bookMeta = (BookMeta)heldItem.getItemMeta();
        List<String> pages = new ArrayList<>();

        for (String page : bookMeta.getPages())
        {
            String p = emoteModule.parseEmotes(page);
            p = markdownModule.parseMarkdown(p);

            pages.add(p);
        }

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bMeta = (BookMeta)book.getItemMeta();

        bMeta.setPages(pages);
        bMeta.setAuthor(player.getName());
        bMeta.setTitle(ChatColor.translateAlternateColorCodes('&', "&3Book Preview"));
        book.setItemMeta(bMeta);

        BookUtil.openBook(book, player);
    }
}
