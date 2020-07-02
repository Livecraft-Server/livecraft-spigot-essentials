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
    POKE_COOLDOWN ("modules.poke.messages.cooldown");

    private final LivecraftSpigotEssentials plugin = LivecraftSpigotEssentials.instance;
    public final String key;

    private Lang (final String key)
    {
        this.key = key;
    }

    @NotNull
    public String get ()
    {
        String s = plugin.getConfig().getString(key, "");
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
