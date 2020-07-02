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
