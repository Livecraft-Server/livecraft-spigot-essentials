package com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.commands;

import com.gmail.mediusecho.fusion.annotations.Command;
import com.gmail.mediusecho.fusion.annotations.Default;
import com.gmail.mediusecho.fusion.annotations.Permission;
import com.gmail.mediusecho.fusion.annotations.SenderPolicy;
import com.gmail.mediusecho.fusion.command.BukkitCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.fusion.commands.properties.Sender;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookModule;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Command(argument = "create", contexts = "book_name")
public class CreateBookCommand extends CommandListener {

    private final BookModule bookModule;

    public CreateBookCommand (final BookModule bookModule)
    {
        this.bookModule = bookModule;
    }

    @Default
    @Permission(permission = "lce.command.modules.book.create")
    @SenderPolicy(Sender.PLAYER_ONLY)
    public void createBook (@NotNull BukkitCommandSender sender)
    {
        String bookName = sender.getFullArguments().get(2);

        if (bookModule.bookExists(bookName))
        {
            sender.sendMessage(Lang.BOOK_EXISTS.get("{1}", bookName));
            return;
        }

        ItemStack book = bookModule.createBook(bookName);
        sender.getPlayer().getInventory().addItem(book);
    }
}
