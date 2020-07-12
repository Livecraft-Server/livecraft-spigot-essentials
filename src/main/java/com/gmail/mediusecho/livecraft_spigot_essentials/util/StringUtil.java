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

package com.gmail.mediusecho.livecraft_spigot_essentials.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static final Pattern COLOR_PREFIX_PATTERN = Pattern.compile("[&§][a-f0-9klmnor]");

    /**
     * Attempts to interpret the boolean value of the argument
     * Acceptable strings include... (yes, no, on, off, 1, 0, true, false)
     * @param arg String to parse
     * @return Boolean value of the argument
     */
    public static boolean getToggleValue (@NotNull String arg)
    {
        switch (arg.toLowerCase())
        {
            case "yes":
            case "on":
            case "1":
            case "true":
                return true;

            case "no":
            case "off":
            case "0":
            case "false":
                return false;
        }

        return false;
    }

    /**
     * Returns the largest permission integer for the give player
     * @param player Player to check
     * @param permissionPrefix Permission to check
     * @return Largest int found for the given permission
     */
    public static int getIntegerValueForPermission (@NotNull Player player, @NotNull String permissionPrefix)
    {
        int maxSize = 0;
        for (PermissionAttachmentInfo perms : player.getEffectivePermissions())
        {
            if (perms.getPermission().startsWith(permissionPrefix))
            {
                String s = perms.getPermission().split(permissionPrefix)[1];
                maxSize = Math.max(maxSize, Integer.parseInt(s));
            }
        }
        return maxSize;
    }

    /**
     * Attempts to find the last used color code in a given string
     *
     * @param message The string to parse
     * @return The last known color code used
     */
    public static String getLastUsedColorCode (String message)
    {
        Matcher matcher = COLOR_PREFIX_PATTERN.matcher(message);
        String color = "";
        while (matcher.find()) {
            color = matcher.group();
        }
        return color;
    }

    /**
     * Returns a string with the fist letter capitalized
     * @param s
     *      String to capitalize
     * @return
     *      String
     */
    @NotNull
    public static String capitalizeFirstLetter (@NotNull String s)
    {
        String original = s.replaceAll("_", " ");
        String[] words = original.split(" ");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < words.length; i++)
        {
            builder.append(Character.toUpperCase(words[i].charAt(0)));
            builder.append(words[i].substring(1));

            if (i < words.length - 1) {
                builder.append(' ');
            }
        }

        return builder.toString();
    }

    /**
     * Removes the fileNames extension
     * @param fileName Name of the file
     * @return The file name without extensions
     */
    @NotNull
    @Contract(pure = true)
    public static String removeExtension (@NotNull String fileName) {
        return fileName.replaceFirst("[.][^.]+$", "");
    }

    @NotNull
    @Contract(pure = true)
    public static String getColorValue (@NotNull String string) {
        return string.replaceAll(ChatColor.COLOR_CHAR + "", "&");
    }

}
