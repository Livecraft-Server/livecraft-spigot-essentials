package com.gmail.mediusecho.livecraft_spigot_essentials;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum Permission {

    /** Emote **/
    EMOTE_CHAT ("modules.emote.chat"),
    EMOTE_SIGNS ("modules.emote.signs"),
    EMOTE_ANVILS ("modules.emote.anvil"),
    EMOTE_COMMANDS ("modules.emote.command"),

    /** Ping **/
    PING_CHAT ("modules.ping"),
    PING_CHAT_RECEIVE_GROUP ("modules.ping.receive.group"),
    PING_CHAT_NOTIFY_GROUP ("modules.ping.notify.group"),

    /** Markdown **/
    MARKDOWN_CHAT ("modules.markdown.chat"),
    MARKDOWN_SIGNS ("modules.markdown.signs"),
    MARKDOWN_ANVILS ("modules.markdown.anvil"),
    MARKDOWN_COMMANDS ("modules.markdown.command"),

    /** Poke **/
    POKE ("modules.poke");

    private final String ROOT = "lce.";
    private final String permission;

    private Permission (final String permission)
    {
        this.permission = ROOT + permission;
    }

    /**
     * Checks to see if the player has this permission value
     *
     * @param player
     *      The player to check
     * @return
     *      Returns true if the player has permission
     */
    public boolean hasPermission (@NotNull Player player) {
        return player.hasPermission(permission);
    }

    /**
     * Get this Permission's string value
     *
     * @return
     *      The permission value
     */
    public String getPermission () {
        return permission;
    }

    /**
     * Checks to see if the player has this permission value
     *
     * @param player
     *      The player to check
     * @param append
     *      An extra permission value appended to the end of the permission
     * @return
     *      Returns true if the player has permission
     */
    public boolean hasPermission (@NotNull Player player, String append) {
        return player.hasPermission(permission + "." + append);
    }
}
