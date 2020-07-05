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

    public static final SettingsValue<Boolean> POKE_ENABLED = new SettingsValue<>("modules.poke.enabled", true);
    public static final SettingsPointer POKE_PLAYER_IGNORED_LIST_POINTER = new SettingsPointer("modules.poke.ignored");
    public static final SettingsValue<Integer> POKE_COMMAND_COOLDOWN = new SettingsValue<>("modules.poke.command-cooldown", 5);
    public static final SettingsValue<Particle> POKE_PARTICLE = new SettingsValue<>("modules.poke.particle", Particle.HEART);
    public static final SettingsValue<Sound> POKE_SOUND = new SettingsValue<>("modules.poke.sound.id", Sound.ENTITY_CHICKEN_EGG);
    public static final SettingsValue<Float> POKE_SOUND_VOLUME = new SettingsValue<>("modules.poke.sound.volume", 1f);
    public static final SettingsValue<Float> POKE_SOUND_PITCH = new SettingsValue<>("modules.poke.sound.pitch", 1f);

    public static final SettingsValue<Boolean> EMOTE_ENABLED = new SettingsValue<>("modules.emote.enabled", true);
    public static final SettingsValue<Boolean> EMOTE_COMMANDS_ENABLED = new SettingsValue<>("modules.emote.commands.enabled", true);
    public static final SettingsValue<Boolean> EMOTE_SIGNS_ENABLED = new SettingsValue<>("modules.emote.signs", true);
    public static final SettingsValue<Boolean> EMOTE_ANVILS_ENABLED = new SettingsValue<>("modules.emote.anvils", true);
    public static final SettingsPointer EMOTE_COMMAND_WHITELIST_POINTER = new SettingsPointer("modules.emote.commands.whitelisted-commands");

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

    public static final SettingsValue<Boolean> BOOK_ENABLED = new SettingsValue<>("modules.book.enabled", true);

    public static final SettingsValue<Boolean> MARKDOWN_ENABLED = new SettingsValue<>("modules.markdown.enabled", true);
    public static final SettingsValue<Boolean> MARKDOWN_COMMANDS_ENABLED = new SettingsValue<>("modules.markdown.commands.enabled", true);
    public static final SettingsValue<Boolean> MARKDOWN_SIGNS_ENABLED = new SettingsValue<>("modules.markdown.signs", true);
    public static final SettingsValue<Boolean> MARKDOWN_ANVILS_ENABLED = new SettingsValue<>("modules.markdown.anvils", true);
    public static final SettingsPointer MARKDOWN_WHITELISTED_COMMANDS_POINTER = new SettingsPointer("modules.markdown.commands.whitelisted-commands");
    public static final SettingsPointer MARKDOWN_FORMATS_POINTER = new SettingsPointer("modules.markdown.formats");
}
