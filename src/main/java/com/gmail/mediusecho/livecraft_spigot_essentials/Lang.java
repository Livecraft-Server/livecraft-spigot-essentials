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

package com.gmail.mediusecho.livecraft_spigot_essentials;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public enum Lang {

    /** MISC **/
    UNKNOWN_COMMAND ("misc.messages.unknown"),
    NO_PERMISSION ("misc.messages.no-permission"),
    INFO ("misc.messages.info"),
    RELOAD ("misc.messages.reload"),
    PLAYER_ONLY ("misc.messages.player-only"),
    PLAYER_ERROR ("misc.messages.player-error"),

    /** Poke **/
    POKE_SUCCESS ("modules.poke.messages.success"),
    POKE_OPT_IN ("modules.poke.messages.opt-in"),
    POKE_OPT_OUT ("modules.poke.messages.opt-out"),
    POKE_IGNORING ("modules.poke.messages.ignoring"),
    POKE_NOT_IGNORING ("modules.poke.messages.not-ignoring"),
    POKE_COOLDOWN ("modules.poke.messages.cooldown"),

    /** Book **/
    BOOK_UPDATE_ERROR ("modules.book.messages.update-error"),
    BOOK_SAVE_ERROR ("modules.book.messages.save-error"),
    BOOK_SAVE_SUCCESS ("modules.book.messages.save-success"),
    BOOK_UPDATE_SUCCESS ("modules.book.messages.update-success"),
    BOOK_PARSE_ERROR ("modules.book.messages.parse-error"),
    BOOK_PARSED ("modules.book.messages.parsed"),
    BOOK_MISSING ("modules.book.messages.missing"),
    BOOK_CONFIRM ("modules.book.messages.confirm"),
    BOOK_DELETED ("modules.book.messages.delete"),
    BOOK_EXISTS ("modules.book.messages.exists");


    private final LivecraftSpigotEssentials plugin = LivecraftSpigotEssentials.instance;
    public final String key;

    private Lang (final String key)
    {
        this.key = key;
    }

    @NotNull
    public String get () {
        return ChatColor.translateAlternateColorCodes('&', getValue());
    }

    public String get (@NotNull Object... parameters)
    {
        // Return the un-parsed value if the parameters length is not even
        if ((parameters.length & 1) != 0) {
            LivecraftSpigotEssentials.logStatic("get parameters are not the correct length " + parameters.length);
            return get();
        }

        String value = getValue();
        for (int i = 0; i < parameters.length; i+=2) {
            value = value.replace(parameters[i].toString(), parameters[i+1].toString());
        }
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    private String getValue () {
        return plugin.getConfig().getString(key, "");
    }
}
