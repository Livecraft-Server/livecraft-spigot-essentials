package com.gmail.mediusecho.livecraft_spigot_essentials;

import com.gmail.mediusecho.fusion.BukkitCommandFramework;
import com.gmail.mediusecho.fusion.LanguageProvider;
import com.gmail.mediusecho.fusion.properties.LangKey;
import com.gmail.mediusecho.livecraft_spigot_essentials.commands.LCMainCommand;
import com.gmail.mediusecho.livecraft_spigot_essentials.config.CustomConfig;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookContext;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.emote.EmoteModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.markdown.MarkdownModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.ping.PingModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.PokeModule;
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

    private PingModule pingModule;
    private EmoteModule emoteModule;
    private BookModule bookModule;

    private LCMainCommand mainCommand;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();

        instance = this;
        mainCommand = new LCMainCommand(this);
        pingModule = new PingModule(this);
        emoteModule = new EmoteModule(this);
        bookModule = new BookModule(this, emoteModule);

        playerConfigMap = new HashMap<>();
        moduleList = new ArrayList<>();
        moduleList.add(new PokeModule(this));
        moduleList.add(new MarkdownModule(this));
        moduleList.add(emoteModule);
        moduleList.add(pingModule);
        moduleList.add(bookModule);

        for (Module m : moduleList) {
            m.reload();
        }

        Messenger messenger = getServer().getMessenger();
        messenger.registerIncomingPluginChannel(this, "lce:message", this);
        messenger.registerOutgoingPluginChannel(this, "lce:message");

        BukkitCommandFramework bukkitCommandFramework = new BukkitCommandFramework(this, this);
        bukkitCommandFramework.registerMainCommand(mainCommand);
        bukkitCommandFramework.registerContext("@book", new BookContext(bookModule));
        bukkitCommandFramework.registerDefaultLangKey(LangKey.UNKNOWN_COMMAND, Lang.UNKNOWN_COMMAND.key);
        bukkitCommandFramework.registerDefaultLangKey(LangKey.NO_PERMISSION, Lang.NO_PERMISSION.key);
        bukkitCommandFramework.registerDefaultLangKey(LangKey.PLAYER_ONLY, Lang.PLAYER_ONLY.key);
    }

    @Override
    public void onDisable()
    {

    }

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

        }

        // The network is requesting the players location in the server
        if (subChannel.equals("request-location"))
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
