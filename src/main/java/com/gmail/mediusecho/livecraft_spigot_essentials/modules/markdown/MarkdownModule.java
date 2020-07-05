package com.gmail.mediusecho.livecraft_spigot_essentials.modules.markdown;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.Permission;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MarkdownModule extends Module {

    private Set<MarkdownFormat> formatSet;
    private List<String> whitelistedCommands;

    private boolean checkCommands;
    private boolean checkSigns;
    private boolean checkAnvils;

    public MarkdownModule(LivecraftSpigotEssentials plugin)
    {
        super(plugin, Settings.MARKDOWN_ENABLED);
        formatSet = new TreeSet<>();
    }

    @Override
    protected void onReload()
    {
        checkCommands = Settings.MARKDOWN_COMMANDS_ENABLED.getValue();
        checkSigns = Settings.MARKDOWN_SIGNS_ENABLED.getValue();
        checkAnvils = Settings.MARKDOWN_ANVILS_ENABLED.getValue();

        formatSet.clear();
        FileConfiguration config = plugin.getConfig();

        whitelistedCommands = config.getStringList(Settings.MARKDOWN_WHITELISTED_COMMANDS_POINTER.getPath());

        ConfigurationSection section = config.getConfigurationSection(Settings.MARKDOWN_FORMATS_POINTER.getPath());
        if (section == null) {
            return;
        }

        Set<String> keys = section.getKeys(false);
        for (String key : keys)
        {
            String path = Settings.MARKDOWN_FORMATS_POINTER.getPath() + "." + key + ".";
            String regex = config.getString(path + "regex", "");
            String replacement = config.getString(path + "replacement", "");
            int priority = config.getInt(path + "priority", 0);

            formatSet.add(new MarkdownFormat(key, regex, replacement, priority));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat (@NotNull AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        if (Permission.MARKDOWN_CHAT.hasPermission(player)) {
            event.setMessage(parseMarkdown(event.getMessage()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandPreProcess (PlayerCommandPreprocessEvent event)
    {
        if (!checkCommands) {
            return;
        }

        Player player = event.getPlayer();
        if (Permission.MARKDOWN_COMMANDS.hasPermission(player))
        {
            String command = event.getMessage().split(" ", 2)[0];
            if (whitelistedCommands.contains(command)) {
                event.setMessage(parseMarkdown(event.getMessage()));
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
            event.setCommand(parseMarkdown(event.getCommand()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange (SignChangeEvent event)
    {
        if (!checkSigns) {
            return;
        }

        Player player = event.getPlayer();
        if (Permission.MARKDOWN_SIGNS.hasPermission(player))
        {
            for (int i = 0; i < 4; i++) {
                event.setLine(i, parseMarkdown(event.getLine(i)));
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
        if (!Permission.MARKDOWN_ANVILS.hasPermission(player)) {
            return;
        }

        if (player.hasPermission("essentials.chat.color")) {
            dislayName = ChatColor.translateAlternateColorCodes('&', dislayName);
        }

        itemMeta.setDisplayName(parseMarkdown(dislayName));
        item.setItemMeta(itemMeta);

        event.setCurrentItem(item);
    }

    public String parseMarkdown (String string)
    {
        for (MarkdownFormat format : formatSet) {
            string = format.parseMarkdown(string);
        }
        return string;
    }
}
