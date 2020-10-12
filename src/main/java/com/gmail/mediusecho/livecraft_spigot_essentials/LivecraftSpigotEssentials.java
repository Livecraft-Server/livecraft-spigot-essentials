
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

import com.gmail.mediusecho.fusion.BukkitCommandFramework;
import com.gmail.mediusecho.fusion.api.LangKey;
import com.gmail.mediusecho.fusion.api.LanguageProvider;
import com.gmail.mediusecho.livecraft_spigot_essentials.commands.LCMainCommand;
import com.gmail.mediusecho.livecraft_spigot_essentials.config.CustomConfig;
import com.gmail.mediusecho.livecraft_spigot_essentials.listeners.ConnectionListener;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.sleepvote.SleepvoteModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookContext;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.broadcast.BroadcastModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.emote.EmoteModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.markdown.MarkdownModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.motd.MotdModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.ping.PingModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.PokeModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.stack.StackModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.packet.PlayerLocationPacket;
import com.gmail.mediusecho.livecraft_spigot_essentials.packet.PlayerTeleportPacket;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.plugin2.message.PluginMessage;

import java.io.File;
import java.util.*;

public class LivecraftSpigotEssentials extends JavaPlugin implements LanguageProvider, PluginMessageListener {

    public static LivecraftSpigotEssentials instance;

    private Map<UUID, CustomConfig> playerConfigMap;
    private List<Module> moduleList;

    private PokeModule pokeModule;
    private PingModule pingModule;
    private EmoteModule emoteModule;
    private BookModule bookModule;

    private LCMainCommand mainCommand;
    private ConnectionListener connectionListener;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        instance = this;
        mainCommand = new LCMainCommand();
        pokeModule = new PokeModule(this);
        pingModule = new PingModule(this);
        emoteModule = new EmoteModule(this);
        bookModule = new BookModule(this, emoteModule);
        bookModule = new BookModule(this);

        playerConfigMap = new HashMap<>();
        moduleList = new ArrayList<>();
        moduleList.add(pokeModule);
        moduleList.add(new MarkdownModule(this));
        moduleList.add(new MotdModule(this));
        moduleList.add(new BroadcastModule(this));
        moduleList.add(new SleepvoteModule(this));
        moduleList.add(new StackModule(this));
        moduleList.add(emoteModule);
        moduleList.add(pingModule);
        moduleList.add(bookModule);

        for (Module m : moduleList) {
            m.reload();
        }

        connectionListener = new ConnectionListener(this);

        Messenger messenger = getServer().getMessenger();
        messenger.registerIncomingPluginChannel(this, "lce:message", this);
        messenger.registerOutgoingPluginChannel(this, "lce:message");

        BukkitCommandFramework commandFramework = new BukkitCommandFramework(this, this);
        commandFramework.registerDependency(LivecraftSpigotEssentials.class, this);
        commandFramework.registerDependency(PokeModule.class, pokeModule);
        commandFramework.registerDependency(BookModule.class, bookModule);
        commandFramework.registerDependency(EmoteModule.class, emoteModule);
        commandFramework.registerDependency(RecipeModule.class, recipeModule);
        commandFramework.registerDependency(StackModule.class, stackModule);
        commandFramework.registerDependency(MarkdownModule.class, markdownModule);

        commandFramework.registerContext("@book", () -> new ArrayList<>(bookModule.getBookNames()));
        commandFramework.registerContext("@craftable", new RecipeContext());

        commandFramework.registerLangKey(LangKey.UNKNOWN_COMMAND, Lang.UNKNOWN_COMMAND.key);
        commandFramework.registerLangKey(LangKey.NO_PERMISSION, Lang.NO_PERMISSION.key);
        commandFramework.registerLangKey(LangKey.PLAYER_ONLY, Lang.PLAYER_ONLY.key);

        commandFramework.registerMainCommand(mainCommand);

        promptTask = new PromptTask();
        promptTask.runTaskTimer(this, 0L, 40L);
    }

    @Override
    public void onDisable() { }

    @Override
    public String getLangTranslation(@NotNull String s) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(s));
    }

    @Override
    public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, @NotNull byte[] bytes)
    {
        if (!s.equals("lce:message")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String subChannel = in.readUTF();

        // Ping message
        if (subChannel.equals("ping"))
        {
            pingModule.parseNetworkPings(in.readUTF());
            return;
        }

        // Player is teleporting somewhere in this server
        if (subChannel.equals("teleport"))
        {
            PlayerTeleportPacket playerTeleportPacket = new PlayerTeleportPacket(in);
            Location location = playerTeleportPacket.getLocation();
            if (location == null) {
                return;
            }

            // Add this location to the pending teleport requests for when
            // the player joins this server.
            if (playerTeleportPacket.isPendingTeleport())
            {
                connectionListener.addPendingPlayerTeleport(playerTeleportPacket.getPlayerId(), location);
                return;
            }

            // Teleport the player since they're connected to this server
            Player p = playerTeleportPacket.getPlayer();
            if (p != null) {
                p.teleport(location);
            }
            return;
        }

        // The network is requesting the players location in the server
        if (subChannel.equals("request-location"))
        {
            String requestId = in.readUTF();
            String playerId = in.readUTF();
            Player p = Bukkit.getPlayer(UUID.fromString(playerId));

            if (p == null || !p.isOnline()) {
                return;
            }

            PlayerLocationPacket playerLocationPacket = new PlayerLocationPacket(p, requestId);
            p.sendPluginMessage(this, "lce:message", playerLocationPacket.toByteArray());
            return;
        }
        {

        }

    }

    public void reload ()
    {
        reloadConfig();
        for (Module m : moduleList) {
            m.reload();
        }
    }

    public boolean fileExists (String folder, String fileName)
    {
        String directory = getDataFolder() + File.separator + folder + File.separator + fileName;
        File file = new File(directory);

        return file.exists();
    }

    /**
     * Returns a {@link CustomConfig} class containing configuration
     * data for this player
     *
     * @param id
     *      The unique id of the player
     * @return
     *      CustomConfig or null if there is no file in the directory
     */
    @Nullable
    public CustomConfig getPlayerConfig (UUID id)
    {
        if (playerConfigMap.containsKey(id)) {
            return playerConfigMap.get(id);
        }

        if (!fileExists("players", id.toString())) {
            return null;
        }

        return getNewPlayerConfig(id);
    }

    /**
     * Returns a new {@link CustomConfig} for the give player
     *
     * @param id
     *      The unique id of the player
     * @return
     *      CustomConfig
     */
    public CustomConfig getNewPlayerConfig (@NotNull UUID id)
    {
        CustomConfig playerConfig = new CustomConfig(this, "players", id.toString(), false);
        playerConfigMap.put(id, playerConfig);
        return playerConfig;
    }

    public LCMainCommand getMainCommand () {
        return mainCommand;
    }

    public void log (Object object) {
        logStatic(object);
    }

    public static void logStatic (@NotNull Object object) {
        Bukkit.getConsoleSender().sendMessage("[LivecraftSpigotEssentials] " + object.toString());
    }
}
