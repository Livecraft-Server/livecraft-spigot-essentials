package com.gmail.mediusecho.livecraft_spigot_essentials.modules.book;

import com.gmail.mediusecho.fusion.context.Context;

import java.util.ArrayList;
import java.util.List;

public class BookContext implements Context {

    private final BookModule bookModule;

    public BookContext (final BookModule bookModule)
    {
        this.bookModule = bookModule;
    }

    @Override
    public List<String> getContext() {
        return new ArrayList<>(bookModule.getBookNames());
    }
}
