package com.gmail.mediusecho.livecraft_spigot_essentials.modules.ping;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.Permission;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.ping.groups.Group;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.ping.groups.RandomGroup;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.SettingsValue;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.MathUtil;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.StringUtil;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingModule extends Module {

    private final Pattern pingPattern = Pattern.compile("\\B@\\w+");

    private List<Group> groupList;
    private List<String> whitelistedCommands;

    private String playerHighlight;
    private boolean checkCommands;

    private Sound sound;
    private float volume;
    private float pitch;
    private Particle particle;

    public PingModule(LivecraftSpigotEssentials plugin)
    {
        super(plugin, Settings.PING_ENABLED);
        groupList = new ArrayList<>();
    }

    @Override
    protected void onReload()
    {
        checkCommands = Settings.PING_COMMANDS_ENABLED.getValue();
        sound = Settings.PING_SOUND.getValue();
        volume = Settings.PING_SOUND_VOLUME.getValue();
        pitch = Settings.PING_SOUND_PITCH.getValue();
        particle = Settings.PING_PARTICLE.getValue();
        playerHighlight = ChatColor.translateAlternateColorCodes('&', Settings.PING_PLAYER_HIGHLIGHT.getValue());

        FileConfiguration config = plugin.getConfig();
        whitelistedCommands = config.getStringList(Settings.PING_WHITELISTED_COMMANDS_POINTER.getPath());
        groupList.clear();

        if (Settings.PING_RANDOM_GROUP_ENABLED.getValue())
        {
            String path = Settings.PING_RANDOM_GROUP_POINTER.getPath();
            String highlight = config.getString(path + ".highlight");
            List<String> aliases = config.getStringList(path + ".aliases");

            Group group = new RandomGroup("random", highlight);
            for (String alias : aliases) {
                group.addAlias(alias);
            }
            groupList.add(group);
        }

        if (Settings.PING_GROUPS_ENABLED.getValue())
        {
            ConfigurationSection section = config.getConfigurationSection(Settings.PING_GROUPS_POINTER.getPath());
            if (section == null) {
                return;
            }

            Set<String> groups = section.getKeys(false);
            for (String key : groups)
            {
                String path = Settings.PING_GROUPS_POINTER.getPath() + "." + key;

                String highlight = config.getString(path + ".highlight");
                List<String> aliases = config.getStringList(path + ".aliases");
                Group group = new Group(key, highlight);

                for (String alias : aliases) {
                    group.addAlias(alias);
                }

                this.groupList.add(group);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat (@NotNull AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        if (Permission.PING_CHAT.hasPermission(player)) {
            event.setMessage(parsePings(player, event.getMessage()));
        }
    }

    @EventHandler
    public void onCommandPreProcess (PlayerCommandPreprocessEvent event)
    {
        if (!checkCommands) {
            return;
        }

        Player player = event.getPlayer();
        if (Permission.PING_CHAT.hasPermission(player))
        {
            String command = event.getMessage().split(" ", 2)[0];
            if (whitelistedCommands.contains(command)) {
                event.setMessage(parsePings(player, event.getMessage()));
            }
        }
    }

    @EventHandler
    public void onServerCommand (ServerCommandEvent event)
    {
        if (!checkCommands) {
            return;
        }

        String command = "/" + event.getCommand().split(" ", 2)[0];
        if (whitelistedCommands.contains(command)) {
            event.setCommand(parsePings(null, event.getCommand()));
        }
    }

    public String parsePings (Player player, String string)
    {
        Matcher matcher = pingPattern.matcher(string);
        List<Player> pingedPlayers = new ArrayList<>();

        while (matcher.find())
        {
            String mention = matcher.group(0);
            String name = mention.replace("@", "").toLowerCase();

            // Check groups
            for (Group group : groupList) {
                string = group.parseMessage(mention, name, string, player, pingedPlayers);
            }

            // Look for players by their nickname
            List<Player> matchedPlayers = Bukkit.matchPlayer(name);
            if (matchedPlayers.size() == 0 && Settings.PING_SEARCH_NICKNAMES.getValue())
            {
                for (Player p : Bukkit.getOnlinePlayers())
                {
                    String strippedName = ChatColor.stripColor(p.getDisplayName().toLowerCase());
                    if (strippedName.contains(name)) {
                        matchedPlayers.add(p);
                    }
                }
            }

            if (matchedPlayers.size() == 1)
            {
                Player p = matchedPlayers.get(0);
                if (p != null && p.isOnline() && !pingedPlayers.contains(p))
                {
                    String prefix = string.substring(0, string.indexOf(mention));
                    String suffix = StringUtil.getLastUsedColorCode(prefix);

                    string = string.replaceFirst(mention, playerHighlight.replace("{1}", mention) + suffix);
                    pingedPlayers.add(p);
                }
            }
        }

        for (Player p : pingedPlayers) {
            pingPlayer(p);
        }

        return string;
    }

    public void parseNetworkPings (@NotNull String players)
    {
        String[] playerNames = players.split(",");
        for (String playerName : playerNames)
        {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null && player.isOnline()) {
                pingPlayer(player);
            }
        }
    }

    private void pingPlayer (@NotNull Player player)
    {
        Location location = player.getLocation();
        player.playSound(location, sound, volume, pitch);

        for (int i = 0; i < 5; i++)
        {
            double x = MathUtil.getRandomOffset(0.5);
            double y = MathUtil.getRandomOffset(0.5) + 0.5;
            double z = MathUtil.getRandomOffset(0.5);

            player.getWorld().spawnParticle(particle, location.clone().add(x, y, z), 1);
        }
    }
}
