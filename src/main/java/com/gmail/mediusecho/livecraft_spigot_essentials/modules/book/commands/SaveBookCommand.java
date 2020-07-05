package com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.commands;

import com.gmail.mediusecho.fusion.annotations.Command;
import com.gmail.mediusecho.fusion.annotations.Default;
import com.gmail.mediusecho.fusion.annotations.Permission;
import com.gmail.mediusecho.fusion.annotations.SenderPolicy;
import com.gmail.mediusecho.fusion.command.BukkitCommandSender;
import com.gmail.mediusecho.fusion.commands.CommandListener;
import com.gmail.mediusecho.fusion.commands.properties.Sender;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.config.CustomConfig;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.BookModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Command(argument = "save", contexts = "@book")
public class SaveBookCommand extends CommandListener {

    private final BookModule bookModule;

    private Map<UUID, Boolean> confirmationMap;
    private Map<UUID, String> bookNameMap;

    public SaveBookCommand (final BookModule bookModule)
    {
        this.bookModule = bookModule;
        confirmationMap = new HashMap<>();
        bookNameMap = new HashMap<>();
    }

    @Default
    @Permission(permission = "lce.command.modules.book.save.@book")
    @SenderPolicy(Sender.PLAYER_ONLY)
    public void saveBook (@NotNull BukkitCommandSender sender)
    {
        List<String> arguments = sender.getFullArguments();
        Player player = sender.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem.getType().equals(Material.AIR))
        {
            sender.sendMessage(Lang.BOOK_UPDATE_ERROR.get());
            return;
        }

        String bookName = arguments.get(2);
        if (!bookModule.bookExists(bookName))
        {
            sender.sendMessage(Lang.BOOK_MISSING.get("{1}", bookName));
            return;
        }

        UUID id = player.getUniqueId();
        if (!hasConfirmed(id))
        {
            sender.sendMessage(Lang.BOOK_CONFIRM.get("{1}", bookName));
            confirmationMap.put(id, true);
            bookNameMap.put(id, bookName);
            return;
        }

        if (!bookNameMap.get(id).equals(bookName))
        {
            sender.sendMessage(Lang.BOOK_SAVE_ERROR.get());
            confirmationMap.remove(id);
            bookNameMap.remove(id);
            return;
        }

        CustomConfig bookConfig = bookModule.getBookConfig(bookName);
        if (bookConfig == null)
        {
            sender.sendMessage(Lang.BOOK_SAVE_ERROR.get());
            confirmationMap.remove(id);
            bookNameMap.remove(id);
            return;
        }

        BookMeta bookMeta = (BookMeta)heldItem.getItemMeta();
        int pages = bookMeta.getPageCount();
        for (int i = 1; i <= pages; i++)
        {
            String page = bookMeta.getPage(i).replaceAll("\\n", "\\\\n");
            bookConfig.set("pages." + i, page);
        }

        bookConfig.save();
        bookConfig.reload();
        bookModule.reload();

        confirmationMap.remove(id);
        bookNameMap.remove(id);

        sender.sendMessage(Lang.BOOK_SAVE_SUCCESS.get("{1}", bookName));
    }

    private boolean hasConfirmed (UUID id)
    {
        if (confirmationMap.containsKey(id)) {
            return confirmationMap.get(id);
        }
        return false;
    }
}
