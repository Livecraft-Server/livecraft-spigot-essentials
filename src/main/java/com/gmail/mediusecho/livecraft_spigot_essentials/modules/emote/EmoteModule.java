package com.gmail.mediusecho.livecraft_spigot_essentials.modules.emote;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.Permission;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.SettingsValue;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmoteModule extends Module {

    private final Pattern emotePattern = Pattern.compile(":([^ ].*?):");
    private final Map<String, String> emoteMap;

    private List<String> whitelistedCommands;

    private boolean checkCommands;
    private boolean checkSigns;
    private boolean checkAnvils;

    public EmoteModule(LivecraftSpigotEssentials plugin)
    {
        super(plugin, Settings.EMOTE_ENABLED);

        emoteMap = new HashMap<>();
        whitelistedCommands = new ArrayList<>();
    }

    @Override
    protected void onReload()
    {
        checkCommands = Settings.EMOTE_COMMANDS_ENABLED.getValue();
        checkSigns = Settings.EMOTE_SIGNS_ENABLED.getValue();
        checkAnvils = Settings.EMOTE_ANVILS_ENABLED.getValue();

        emoteMap.clear();
        FileConfiguration config = plugin.getConfig();
        Set<String> keys = config.getConfigurationSection("modules.emote.emotes").getKeys(false);

        for (String key : keys)
        {
            String emote = config.getString("modules.emote.emotes." + key);
            if (emote != null) {
                emoteMap.put(":" + key + ":", emote);
            }
        }

        whitelistedCommands = config.getStringList(Settings.EMOTE_COMMAND_WHITELIST_POINTER.getPath());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat (@NotNull AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        if (Permission.EMOTE_CHAT.hasPermission(player)) {
            event.setMessage(parseEmotes(event.getMessage()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandPreProcess (PlayerCommandPreprocessEvent event)
    {
        if (!checkCommands) {
            return;
        }

        Player player = event.getPlayer();
        if (Permission.EMOTE_CHAT.hasPermission(player))
        {
            String command = event.getMessage().split(" ", 2)[0];
            if (whitelistedCommands.contains(command)) {
                event.setMessage(parseEmotes(event.getMessage()));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand (ServerCommandEvent event)
    {
        if (!checkCommands) {
            return;
        }

        String command = "/" + event.getCommand().split(" ", 2)[0];
        if (whitelistedCommands.contains(command)) {
            event.setCommand(parseEmotes(event.getCommand()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange (SignChangeEvent event)
    {
        if (!checkSigns) {
            return;
        }

        Player player = event.getPlayer();
        if (Permission.EMOTE_SIGNS.hasPermission(player))
        {
            for (int i = 0; i < 4; i++) {
                event.setLine(i, parseEmotes(event.getLine(i)));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAnvilInteract (InventoryClickEvent event)
    {
        if (!checkAnvils) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        // Anvil result slot
        if (event.getRawSlot() != 2) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().equals(Material.AIR)) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        String dislayName = itemMeta.getDisplayName();
        if (dislayName.equals("")) {
            return;
        }

        Player player = (Player)event.getWhoClicked();
        if (!Permission.EMOTE_ANVIL.hasPermission(player)) {
            return;
        }

        if (player.hasPermission("essentials.chat.color")) {
            dislayName = ChatColor.translateAlternateColorCodes('&', dislayName);
        }

        itemMeta.setDisplayName(parseEmotes(dislayName));
        item.setItemMeta(itemMeta);

        event.setCurrentItem(item);
    }

    /**
     * Searches the input string for any emote matches.
     *
     * @param message
     *      The message to parse
     * @return
     *      The parsed message
     */
    public String parseEmotes (String message)
    {
        Matcher matcher = emotePattern.matcher(message);
        while (matcher.find())
        {
            String emote = matcher.group(0);
            if (emoteMap.containsKey(emote)) {
                message = message.replace(emote, emoteMap.get(emote));
            }
        }
        return message;
    }
}
