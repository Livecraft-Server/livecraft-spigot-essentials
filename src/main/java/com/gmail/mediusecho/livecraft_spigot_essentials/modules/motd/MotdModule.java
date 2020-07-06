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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.motd;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MotdModule extends Module {

    private final Pattern boldPattern = Pattern.compile("&l(.*?)&");

    private int lineWidth;

    private List<MotdData> motdDataList;
    private Map<Character, Integer> characterWidthMap;

    // Stores the last used ip address of each player.
    // This is necessary to use the %player% variable in motd's
    // as there is no way to query which player is pinging the server
    // until after they have connected.
    private Map<String, String> playerAddressMap;

    public MotdModule(LivecraftSpigotEssentials plugin)
    {
        super(plugin, Settings.MOTD_ENABLED);

        motdDataList = new ArrayList<>();
        characterWidthMap = new HashMap<>();
        playerAddressMap = new HashMap<>();

        setCharacterWidth(".i';:!|,", 1);
        setCharacterWidth("l", 2);
        setCharacterWidth("\"[]{}() *", 3);
        setCharacterWidth("abcdefghjkmnopqrstuvwxyz1234567890-_#$%^&/?", 4);
        setCharacterWidth("<>", 5);
        setCharacterWidth("@", 6);
        setCharacterWidth("❤", 7);
    }

    @Override
    protected void onReload()
    {
        lineWidth = Settings.MOTD_LINE_WIDTH.getValue();

        motdDataList.clear();
        FileConfiguration config = plugin.getConfig();

        motdDataList.add(new MotdData(Settings.MOTD_FALLBACK_MOTD.getValue(), this));
        for (String motd : config.getStringList(Settings.MOTD_LIST_POINTER.getPath())) {
            motdDataList.add(new MotdData(motd, this));
        }
    }

    @EventHandler
    public void onServerListPing (@NotNull ServerListPingEvent event)
    {
        String address = event.getAddress().toString();
        MotdData data = getNextMotd(address);
        String motd = data.getMotd();

        // This motd contains a dynamic %player% variable that needs to be centered after it's set
        if (!data.isCached()) {
            motd = getCenteredMotd(motd.replace("%player%", playerAddressMap.get(address)));
        }

        event.setMotd(motd);
    }

    @EventHandler
    public void onPlayerJoin (@NotNull PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        InetSocketAddress address = player.getAddress();
        if (address == null) {
            return;
        }
        playerAddressMap.put(player.getAddress().getAddress().toString(), player.getName());
    }

    @NotNull
    public String getCenteredMotd (@NotNull String motd)
    {
        String[] splitMotd = motd.split("/n");
        String[] lines = new String[]{"", ""};

        int count = Math.min(splitMotd.length, 2);
        for (int i = 0; i < count; i++)
        {
            String line = splitMotd[i] + "&r";
            String s = ChatColor.translateAlternateColorCodes('&',line);
            String ss = ChatColor.stripColor(s);

            int width = 0;
            for (int j = 0; j < ss.length(); j++) {
                width += characterWidthMap.getOrDefault(Character.toLowerCase(ss.charAt(j)), 0) + 1;
            }

            Matcher matcher = boldPattern.matcher(line);
            while (matcher.find()) {
                width += matcher.group(1).replaceAll(" ", "").length();
            }

            lines[i] = centerText(s, width, lineWidth);
        }

        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (!line.equals("")) {
                sb.append(line).append("\n");
            }
        }

        sb.setLength(sb.length() - 3);
        return sb.toString();
    }

    @NotNull
    private MotdData getNextMotd (@NotNull String ipAddress)
    {
        MotdData data = motdDataList.get(new Random().nextInt(motdDataList.size()));

        // Skip the player name motd if the players ip address is unknown
        if (!playerAddressMap.containsKey(ipAddress))
        {
            int attempts = 0;
            int limit = motdDataList.size();
            while (!data.isCached())
            {
                data = motdDataList.get(new Random().nextInt(motdDataList.size()));
                attempts++;

                // Return the fallback motd
                if (attempts >= limit) {
                    return motdDataList.get(0);
                }
            }
        }

        return data;
    }

    @NotNull
    private String centerText(String text, int width, int lineLength)
    {
        StringBuilder builder = new StringBuilder(text);
        int distance = ((lineLength - width) / 2) / 4;
        for (int ignored = 0; ignored < distance; ++ignored) {
            builder.insert(0, ' ');
        }
        return builder.toString();
    }

    @Contract(pure = true)
    private void setCharacterWidth (@NotNull String characters, int width)
    {
        for (int i = 0; i < characters.length(); i++) {
            characterWidthMap.put(characters.charAt(i), width);
        }
    }
}
