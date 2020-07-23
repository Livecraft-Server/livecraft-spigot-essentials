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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.book;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.config.CustomConfig;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.book.commands.BookCommand;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.emote.EmoteModule;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.ResourceUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.File;
import java.util.*;

public class BookModule extends Module {

    private Map<String, ItemStack> bookMap;
    private Map<String, CustomConfig> bookConfigMap;

    public BookModule(LivecraftSpigotEssentials plugin)
    {
        super(plugin, Settings.BOOK_ENABLED);
        bookMap = new HashMap<>();
        bookConfigMap = new HashMap<>();

        plugin.getMainCommand().registerCommand(new BookCommand());
    }

    @Override
    protected void onReload()
    {
        bookMap.clear();
        bookConfigMap.clear();

        File booksFolder = new File(plugin.getDataFolder() + File.separator + "books");
        if (!booksFolder.isDirectory())
        {
            plugin.log("Unable to find books folder, skipping book loading");
            return;
        }

        File[] bookFiles = booksFolder.listFiles();
        for (int i = 0; i < bookFiles.length; i++)
        {
            if (bookFiles[i].isFile())
            {
                File bookFile = bookFiles[i];
                if (bookFile.getName().endsWith(".yml"))
                {
                    CustomConfig bookConfig = new CustomConfig(plugin, "books", bookFile.getName(), false);
                    FileConfiguration config = bookConfig.getConfig();
                    Set<String> keys = config.getConfigurationSection("pages").getKeys(false);

                    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                    BookMeta bookMeta = (BookMeta)book.getItemMeta();
                    bookMeta.setTitle("Livecraft Essentials");
                    bookMeta.setAuthor("Someone Special");

                    List<String> pages = new ArrayList<>();
                    for (String key : keys)
                    {
                        String path = "pages." + key;
                        String page = ChatColor.translateAlternateColorCodes('&', config.getString(path).replaceAll("\\\\n", "\n"));
                        pages.add(page);
                    }

                    bookMeta.setPages(pages);
                    book.setItemMeta(bookMeta);

                    String bookName = ResourceUtil.removeExtension(bookFile.getName());
                    bookConfigMap.put(bookName, bookConfig);
                    bookMap.put(bookName, book);
                }
            }
        }
    }

    /**
     * Returns a Set<String> of all the book names
     * currently loaded.
     *
     * @return
     *      Set<String>
     */
    public Set<String> getBookNames () {
        return bookMap.keySet();
    }

    /**
     * Checks to see if a book exists with
     * the given name.
     *
     * @param name
     *      The name of the book to check.
     * @return
     *      Boolean
     */
    public boolean bookExists (String name) {
        return bookMap.containsKey(name);
    }

    /**
     * Returns the {@link ItemStack} book item associated
     * with this name.
     *
     * @param name
     *      The name of the book.
     * @return
     *      ItemStack
     */
    public ItemStack getBook (String name) {
        return bookMap.get(name);
    }

    /**
     * Returns a WRITABLE_BOOK loaded from a configuration
     * file.
     *
     * @param name
     *      The name of the book
     * @return
     *      ItemStack
     */
    public ItemStack getEditableBook (String name)
    {
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta bookMeta = (BookMeta)book.getItemMeta();

        bookMeta.setTitle(name);
        bookMeta.setAuthor("Someone Special");
        bookMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Book of " + name));

        CustomConfig bookConfig = new CustomConfig(plugin, "books", name + ".yml", false);
        FileConfiguration config = bookConfig.getConfig();
        Set<String> keys = config.getConfigurationSection("pages").getKeys(false);

        List<String> pages = new ArrayList<>();
        for (String key : keys)
        {
            String path = "pages." + key;
            String page = ChatColor.translateAlternateColorCodes('&', config.getString(path).replaceAll("\\\\n", "\n"));
            pages.add(page);
        }

        bookMeta.setPages(pages);
        book.setItemMeta(bookMeta);

        return book;
    }

    /**
     * Returns the {@link CustomConfig} associated with
     * this book
     *
     * @param name
     *      The name of the book
     * @return
     *      CustomConfig
     */
    public CustomConfig getBookConfig (String name) {
        return bookConfigMap.get(name);
    }

    /**
     * Deletes a book with the given name.
     *
     * @param name
     *      The name of the book to delete.
     * @return
     *      True if the book was deleted.
     */
    public boolean deleteBook (String name)
    {
        if (!bookExists(name)) {
            return false;
        }

        if (bookConfigMap.get(name).delete())
        {
            bookMap.remove(name);
            bookConfigMap.remove(name);
            return true;
        }

        return false;
    }

    /**
     * Creates and returns a new Book item.
     *
     * Creates a new book .yml file associated with this name
     *
     * @param name
     *      The name of the new book
     * @return
     *      ItemStack
     */
    public ItemStack createBook (String name)
    {
        CustomConfig bookConfig = new CustomConfig(plugin, "books", name + ".yml", true);

        bookConfig.set("pages.1", name);
        bookConfig.save();
        bookConfig.reload();

        List<String> pages = new ArrayList<String>();
        pages.add(name);

        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta bookMeta = (BookMeta)book.getItemMeta();

        bookMeta.setPages(pages);
        book.setItemMeta(bookMeta);

        bookConfigMap.put(name, bookConfig);
        bookMap.put(name, book);
        return book;
    }
}
