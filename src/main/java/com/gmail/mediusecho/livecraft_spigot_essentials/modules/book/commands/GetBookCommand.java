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

import java.util.List;

@Command(argument = "get", contexts = "@book")
public class GetBookCommand extends CommandListener {

    private final BookModule bookModule;

    public GetBookCommand (final BookModule bookModule)
    {
        this.bookModule = bookModule;
    }

    @Default
    @Permission(permission = "lce.command.modules.book.get.@book")
    @SenderPolicy(Sender.PLAYER_ONLY)
    public void getBook (@NotNull BukkitCommandSender sender)
    {
        List<String> arguments = sender.getFullArguments();
        String bookName = arguments.get(2);

        if (!bookModule.bookExists(bookName))
        {
            bookError(sender, bookName);
            return;
        }

        ItemStack book = bookModule.getBook(bookName);
        if (book == null)
        {
            bookError(sender, bookName);
            return;
        }

        sender.getPlayer().getInventory().addItem(book);
    }

    private void bookError (@NotNull BukkitCommandSender sender, String bookName) {
        sender.sendMessage(Lang.BOOK_MISSING.get("{1}", bookName));
    }
}
