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
