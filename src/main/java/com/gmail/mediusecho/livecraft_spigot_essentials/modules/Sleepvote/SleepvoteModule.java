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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.Sleepvote;

import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.Permission;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.MetadataUtil;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SleepvoteModule extends Module {

    private boolean essentialsHookLoaded;
    private SleepvoteEssentialsHook sleepvoteEssentialsHook;

    private List<World> supportedWorlds;
    private boolean checkWeather;

    public SleepvoteModule(LivecraftSpigotEssentials plugin)
    {
        super(plugin, Settings.SLEEPVOTE_ENABLED);
        supportedWorlds = new ArrayList<>();

        if (plugin.getServer().getPluginManager().getPlugin("Essentials") != null)
        {
            essentialsHookLoaded = true;
            sleepvoteEssentialsHook = new SleepvoteEssentialsHook(this);
        }
    }

    @Override
    protected void onReload()
    {
        checkWeather = Settings.SLEEPVOTE_CHECK_WEATHER.getValue();
        supportedWorlds.clear();

        FileConfiguration config = plugin.getConfig();
        for (String worldName : config.getStringList(Settings.SLEEPVOTE_WORLDS_POINTER.getPath()))
        {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                supportedWorlds.add(world);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEnterBed (@NotNull PlayerBedEnterEvent enterEvent)
    {
        if (enterEvent.isCancelled()) {
            return;
        }
        enterSleepVote(enterEvent.getPlayer(), true, false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLeaveBed (@NotNull PlayerBedLeaveEvent event) {
        exitSleepVote(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit (@NotNull PlayerQuitEvent event) {
        exitSleepVote(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChangeWorld (@NotNull PlayerChangedWorldEvent event) {
        exitSleepVote(event.getPlayer());
    }

    public void onPlayerAfkStatusToggle (@NotNull Player player, boolean status)
    {
        World world = player.getWorld();
        if (!worldIsSleepVoting(world)) {
            return;
        }

        if (status) {
            enterSleepVote(player, false, true);
        } else {
            exitSleepVote(player);
        }
    }

    /**
     * Returns the sleep vote message best appropriate for the
     * world conditions.
     *
     * @param world
     *      The world to check
     * @return
     *      Lang
     */
    public Lang getSleepMessage (World world)
    {
        if (!WorldUtil.worldIsNightTime(world) && world.isThundering()) {
            return Lang.SLEEPVOTE_THUNDER;
        }
        return Lang.SLEEPVOTE_SLEEPING;
    }

    /**
     * Get how many players are sleep voting
     * in this world.
     *
     * @param world
     *      The world to check
     * @return
     *      The number of players currently sleep voting.
     */
    public int getPlayersSleeping (@NotNull World world)
    {
        int total = 0;
        for (Player player : world.getPlayers())
        {
            if (player.hasMetadata(MetadataUtil.SLEEPVOTE_WORLD)) {
                total++;
            }
        }
        return total;
    }

    private void enterSleepVote (@NotNull Player player, boolean delayed, boolean overrideAfkCheck)
    {
        // Stop here if the player is already sleep voting
        if (player.hasMetadata(MetadataUtil.SLEEPVOTE_WORLD)) {
            return;
        }

        World world = player.getWorld();
        if (!supportedWorlds.contains(world)) {
            return;
        }

        // Check for thunderstorms if the world is still in day time
        if (!WorldUtil.worldIsNightTime(world))
        {
            if (!world.isThundering()) {
                return;
            }

            if (!checkWeather) {
                return;
            }
        }

        long delay = delayed ? 30L : 1L;
        player.setMetadata(MetadataUtil.SLEEPVOTE_TASK, new FixedMetadataValue(plugin, new BukkitRunnable()
        {
            @Override
            public void run()
            {
                player.setMetadata(MetadataUtil.SLEEPVOTE_WORLD, new FixedMetadataValue(plugin, world));
                broadcastSleep(player, world, overrideAfkCheck);
            }
        }.runTaskLater(plugin, delay)));
    }

    private void exitSleepVote (@NotNull Player player)
    {
        player.removeMetadata(MetadataUtil.SLEEPVOTE_WORLD, plugin);
        if (player.hasMetadata(MetadataUtil.SLEEPVOTE_TASK))
        {
            BukkitTask task = (BukkitTask)player.getMetadata(MetadataUtil.SLEEPVOTE_TASK).get(0).value();
            if (task != null) {
                task.cancel();
            }
            player.removeMetadata(MetadataUtil.SLEEPVOTE_TASK, plugin);
        }
    }

    private void broadcastSleep (Player player, @NotNull World world, boolean overrideAfkCheck)
    {
        List<Player> onlinePlayers = world.getPlayers();

        int playersSleeping = getPlayersSleeping(world);
        int totalValidPlayers = getValidPlayerCount(onlinePlayers, player, overrideAfkCheck);
        int playersNeeded = (int) Math.ceil(totalValidPlayers * Settings.SLEEPVOTE_SLEEP_PERCENTAGE.getValue());

        if (playersSleeping >= playersNeeded) {
            String morning = Lang.SLEEPVOTE_WAKING.get();
            for (Player p : onlinePlayers) {
                exitSleepVote(p);
                if (Settings.SLEEPVOTE_BROADCAST_TO_SLEEPING_PLAYERS_ONLY.getValue() && !p.isSleeping()) {
                    continue;
                }

                if (p.isSleeping()) {
                    p.wakeup(true);
                }
                p.sendMessage(morning);
            }

            // Set the time to morning if it is almost or past nighttime
            // This will allow a window where players can sleep in the storm and it isn't considered night time.
            if (world.getTime() > 11500L) {
                world.setTime(0L);
            }

            // Disable thunder storms since we allowed weather
            if (!Settings.SLEEPVOTE_CHECK_WEATHER.getValue()) {
                if (world.isThundering()) {
                    world.setThundering(false);
                    world.setStorm(false);
                }
            }

            return;
        }

        // Show the sleep message
        String sleep = getSleepMessage(world).get(
                "{1}", player.getDisplayName(),
                "{2}", Integer.toString(playersSleeping),
                "{3}", Integer.toString(playersNeeded)
        );

        for (Player p : world.getPlayers()) {
            p.sendMessage(sleep);
        }
    }

    private int getValidPlayerCount (@NotNull List<Player> players, Player player, boolean overrideAfkCheck)
    {
        int total = 0;
        for (Player p : players)
        {
            // Skip AFK players
            if (essentialsHookLoaded && sleepvoteEssentialsHook.isPlayerAfk(p))
            {
                // Skip this player if afk override is false
                if (!overrideAfkCheck) {
                    continue;
                }

                // Skip this player is he is the calling player
                if (!p.equals(player)) {
                    continue;
                }
            }

            // Skip players with the sleep ignore permission
            if (Settings.SLEEPVOTE_IGNORE_STAFF.getValue() && Permission.SLEEPVOTE_IGNORE.hasPermission(player)) {
                continue;
            }

            total++;
        }
        return total;
    }

    /**
     * Checks to see if the player can sleep vote in this world.
     *
     * @param world
     *      The world to check
     * @return
     *      True if this world can sleep vote.
     */
    private boolean worldIsSleepVoting (World world)
    {
        if (!supportedWorlds.contains(world)) {
            return false;
        }

        for (Player player : world.getPlayers())
        {
            if (player.hasMetadata(MetadataUtil.SLEEPVOTE_WORLD)) {
                return true;
            }
        }

        return false;
    }
}
