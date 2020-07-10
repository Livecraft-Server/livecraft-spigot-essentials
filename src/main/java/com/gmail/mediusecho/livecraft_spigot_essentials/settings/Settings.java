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

package com.gmail.mediusecho.livecraft_spigot_essentials.settings;

import org.bukkit.Particle;
import org.bukkit.Sound;

public class Settings {

    public static final SettingsValue<Boolean> DEBUGGING = new SettingsValue<>("misc.debugging", false);

    /** Poke **/
    public static final SettingsValue<Boolean> POKE_ENABLED = new SettingsValue<>("modules.poke.enabled", true);
    public static final SettingsPointer POKE_PLAYER_IGNORED_LIST_POINTER = new SettingsPointer("modules.poke.ignored");
    public static final SettingsValue<Integer> POKE_COMMAND_COOLDOWN = new SettingsValue<>("modules.poke.command-cooldown", 5);
    public static final SettingsValue<Particle> POKE_PARTICLE = new SettingsValue<>("modules.poke.particle", Particle.HEART);
    public static final SettingsValue<Sound> POKE_SOUND = new SettingsValue<>("modules.poke.sound.id", Sound.ENTITY_CHICKEN_EGG);
    public static final SettingsValue<Float> POKE_SOUND_VOLUME = new SettingsValue<>("modules.poke.sound.volume", 1f);
    public static final SettingsValue<Float> POKE_SOUND_PITCH = new SettingsValue<>("modules.poke.sound.pitch", 1f);

    /** Emote **/
    public static final SettingsValue<Boolean> EMOTE_ENABLED = new SettingsValue<>("modules.emote.enabled", true);
    public static final SettingsValue<Boolean> EMOTE_COMMANDS_ENABLED = new SettingsValue<>("modules.emote.commands.enabled", true);
    public static final SettingsValue<Boolean> EMOTE_SIGNS_ENABLED = new SettingsValue<>("modules.emote.signs", true);
    public static final SettingsValue<Boolean> EMOTE_ANVILS_ENABLED = new SettingsValue<>("modules.emote.anvils", true);
    public static final SettingsPointer EMOTE_COMMAND_WHITELIST_POINTER = new SettingsPointer("modules.emote.commands.whitelisted-commands");

    /** Ping **/
    public static final SettingsValue<Boolean> PING_ENABLED = new SettingsValue<>("modules.ping.enabled", true);
    public static final SettingsValue<Boolean> PING_COMMANDS_ENABLED = new SettingsValue<>("modules.ping.commands.enabled", true);
    public static final SettingsValue<Boolean> PING_SEARCH_NICKNAMES = new SettingsValue<>("modules.ping.include-nicknames", true);
    public static final SettingsValue<Particle> PING_PARTICLE = new SettingsValue<>("modules.ping.particle", Particle.VILLAGER_HAPPY);
    public static final SettingsValue<Sound> PING_SOUND = new SettingsValue<>("modules.ping.sound.id", Sound.BLOCK_NOTE_BLOCK_PLING);
    public static final SettingsValue<Float> PING_SOUND_VOLUME = new SettingsValue<>("modules.ping.sound.volume", 0.3f);
    public static final SettingsValue<Float> PING_SOUND_PITCH = new SettingsValue<>("modules.ping.sound.pitch", 2.0f);
    public static final SettingsValue<Boolean> PING_GROUPS_ENABLED = new SettingsValue<>("modules.ping.groups.enabled", true);
    public static final SettingsPointer PING_GROUPS_POINTER = new SettingsPointer("modules.ping.groups.groups");
    public static final SettingsValue<String> PING_PLAYER_HIGHLIGHT = new SettingsValue<>("modules.ping.player-highlight", "&3{1}&r");
    public static final SettingsValue<Boolean> PING_RANDOM_GROUP_ENABLED = new SettingsValue<>("modules.ping.special-pings.random.enabled", true);
    public static final SettingsPointer PING_RANDOM_GROUP_POINTER = new SettingsPointer("modules.ping.special-pings.random");
    public static final SettingsPointer PING_WHITELISTED_COMMANDS_POINTER = new SettingsPointer("modules.ping.commands.whitelist");

    /** Book **/
    public static final SettingsValue<Boolean> BOOK_ENABLED = new SettingsValue<>("modules.book.enabled", true);

    /** Markdown **/
    public static final SettingsValue<Boolean> MARKDOWN_ENABLED = new SettingsValue<>("modules.markdown.enabled", true);
    public static final SettingsValue<Boolean> MARKDOWN_COMMANDS_ENABLED = new SettingsValue<>("modules.markdown.commands.enabled", true);
    public static final SettingsValue<Boolean> MARKDOWN_SIGNS_ENABLED = new SettingsValue<>("modules.markdown.signs", true);
    public static final SettingsValue<Boolean> MARKDOWN_ANVILS_ENABLED = new SettingsValue<>("modules.markdown.anvils", true);
    public static final SettingsPointer MARKDOWN_WHITELISTED_COMMANDS_POINTER = new SettingsPointer("modules.markdown.commands.whitelisted-commands");
    public static final SettingsPointer MARKDOWN_FORMATS_POINTER = new SettingsPointer("modules.markdown.formats");

    /** Motd **/
    public static final SettingsValue<Boolean> MOTD_ENABLED = new SettingsValue<>("modules.motd.enabled", true);
    public static final SettingsValue<Integer> MOTD_LINE_WIDTH = new SettingsValue<>("modules.motd.line-width", 250);
    public static final SettingsValue<String> MOTD_FALLBACK_MOTD = new SettingsValue<>("modules.motd.fallback-motd", "Default MOTD");
    public static final SettingsPointer MOTD_LIST_POINTER = new SettingsPointer("modules.motd.motds");

    /** Broadcase **/
    public static final SettingsValue<Boolean> BROADCAST_ENABLED = new SettingsValue<>("modules.broadcast.enabled", true);
    public static final SettingsValue<Integer> BROADCAST_INTERVAL = new SettingsValue<>("modules.broadcast.interval", 10);
    public static final SettingsPointer BROADCAST_MESSAGE_POINTER = new SettingsPointer("modules.broadcast.messages");

    /** Sleepvote **/
    public static final SettingsValue<Boolean> SLEEPVOTE_ENABLED = new SettingsValue<>("modules.sleepvote.enabled", true);
    public static final SettingsPointer SLEEPVOTE_WORLDS_POINTER = new SettingsPointer("modules.sleepvote.worlds");
    public static final SettingsValue<Float> SLEEPVOTE_SLEEP_PERCENTAGE = new SettingsValue<>("modules.sleepvote.sleep-percentage", 0.5f);
    public static final SettingsValue<Boolean> SLEEPVOTE_IGNORE_STAFF = new SettingsValue<>("modules.sleepvote.ignore-staff", false);
    public static final SettingsValue<Boolean> SLEEPVOTE_CHECK_WEATHER = new SettingsValue<>("modules.sleepvote.check-weather", true);
    public static final SettingsValue<Boolean> SLEEPVOTE_BROADCAST_TO_SLEEPING_PLAYERS_ONLY = new SettingsValue<>("modules.sleepvote.broadcast-wake-up-to-sleeping-players-only", false);
}
