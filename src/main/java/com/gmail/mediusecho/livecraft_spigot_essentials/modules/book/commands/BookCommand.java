package com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.commands;

import com.gmail.mediusecho.fusion.annotations.Command;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.emote.EmoteModule;

@Command(argument = "book")
public class BookCommand extends CommandListener {

    public BookCommand (BookModule bookModule, EmoteModule emoteModule)
    {
        registerCommand(new CreateBookCommand(bookModule));
        registerCommand(new GetBookCommand(bookModule));
        registerCommand(new SaveBookCommand(bookModule));
        registerCommand(new DeleteBookCommand(bookModule));
        registerCommand(new OpenBookCommand(bookModule));
        registerCommand(new PreviewBookCommand());
        registerCommand(new ParseBookCommand(emoteModule));
    }
}
