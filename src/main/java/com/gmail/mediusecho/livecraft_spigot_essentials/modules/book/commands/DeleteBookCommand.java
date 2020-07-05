package com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.commands;

import com.gmail.mediusecho.fusion.annotations.Command;
import com.gmail.mediusecho.fusion.annotations.Default;
import com.gmail.mediusecho.fusion.annotations.Permission;
import com.gmail.mediusecho.fusion.command.BukkitCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookModule;
import org.jetbrains.annotations.NotNull;

@Command(argument = "delete", contexts = "@book")
public class DeleteBookCommand extends CommandListener {

    private final BookModule bookModule;

    public DeleteBookCommand (final BookModule bookModule)
    {
        this.bookModule = bookModule;
    }

    @Default
    @Permission(permission = "lce.command.modules.book.delete")
    public void deleteBook (@NotNull BukkitCommandSender sender)
    {
        String bookName = sender.getFullArguments().get(2);

        if (!bookModule.bookExists(bookName))
        {
            sender.sendMessage(Lang.BOOK_MISSING.get("{1}", bookName));
            return;
        }

        if (bookModule.deleteBook(bookName)) {
            sender.sendMessage(Lang.BOOK_DELETED.get("{1}", bookName));
        }
    }

}
