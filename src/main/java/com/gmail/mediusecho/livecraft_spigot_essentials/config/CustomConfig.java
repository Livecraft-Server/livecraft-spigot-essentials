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

package com.gmail.mediusecho.livecraft_spigot_essentials.config;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.ResourceUtil;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.StringUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class CustomConfig {

    private final LivecraftSpigotEssentials plugin;

    private File file;
    private FileConfiguration config;

    private final String path;
    private final String fileName;
    private final String name;
    private final String directory;

    public CustomConfig (@NotNull final LivecraftSpigotEssentials plugin, final String path, final String name, boolean logOutput)
    {
        this.plugin = plugin;
        this.path = path;
        this.fileName = name;
        this.name = StringUtil.removeExtension(name);
        this.directory = plugin.getDataFolder() + File.separator + path;
        this.file = new File(directory + File.separator + name);
        this.config = new YamlConfiguration();

        // Load the configuration file
        if (!file.exists()) {
            file = createNewFile(logOutput);
        } else if (logOutput) {
            plugin.log("Loading " + path + File.separator + fileName);
        }

        try {
            config.load(file);
        } catch (Exception exception) {
            plugin.log("There was an error loading " + name + ", error: " + exception.getClass().getSimpleName());
        }
    }

    /**
     * Saves this configuration file
     */
    public void save ()
    {
        try {
            config.save(file);
        } catch (IOException ignored) {}
    }

    /**
     * Reloads any changes made to the configuration file
     */
    public void reload ()
    {
        try {
            config = YamlConfiguration.loadConfiguration(file);
        } catch (Exception exception) {
            plugin.log("There was an error loading " + name + ", error: " + exception.getClass().getSimpleName());
        }
    }

    /**
     * Sets a value in this config
     *
     * @param path
     *      The configuration path to set
     * @param value
     *      The value to set
     */
    public void set (String path, Object value) {
        config.set(path, value);
    }

    /**
     * Returns the FileConfiguration object
     *
     * @return
     *      FileConfiguration
     */
    public FileConfiguration getConfig () {
        return  config;
    }

    /**
     * Tries to delete this Configuration File
     *
     * @return
     *      True if the configuration file was successfully deleted
     */
    public boolean delete () {
        return file.delete();
    }

    /**
     * Creates a new file
     *
     * @param logOutput Logs the output to the server console
     * @return A new File
     */
    private File createNewFile (boolean logOutput)
    {
        file.getParentFile().mkdirs();
        file = new File(directory + File.separator + fileName);

        // Try to copy an existing .yml file into this one
        if (plugin.getResource(fileName) != null)
        {
            try {
                ResourceUtil.copyFile(plugin.getResource(fileName), file);
            } catch (IOException ignored) {}
        }

        else
        {
            if (!file.exists())
            {
                try {
                    file.createNewFile();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        if (logOutput) {
            plugin.log("Creating " + path + File.separator + fileName);
        }

        return file;
    }

}
