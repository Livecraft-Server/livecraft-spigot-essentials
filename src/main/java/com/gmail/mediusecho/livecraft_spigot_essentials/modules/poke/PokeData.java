package com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.config.CustomConfig;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PokeData {

    private final LivecraftSpigotEssentials plugin;
    private final UUID playerId;

    private List<Player> ignoredPlayers;
    private long lastPokeTime = 0L;

    public PokeData (final LivecraftSpigotEssentials plugin, final UUID playerId)
    {
        this.plugin = plugin;
        this.playerId = playerId;
        this.ignoredPlayers = new ArrayList<>();
    }

    /**
     * Checks to see if this player is being ignored
     *
     * @param player
     *      The player to check
     * @return
     *      True if the player is ignored
     */
    public boolean isIgnoringPlayer (Player player) {
        return ignoredPlayers.contains(player);
    }

    /**
     * Adds this player to the ignored players list
     *
     * @param player
     *      The player to ignore
     * @return
     *      True if the player was ignored, false if the player
     *      is already ignored.
     */
    public boolean ignorePlayer (Player player)
    {
        if (isIgnoringPlayer(player)) {
            return false;
        }

        ignoredPlayers.add(player);
        saveIgnoredPlayers();
        return true;
    }

    /**
     * Removes this player from the ignored players list
     *
     * @param player
     *      The player to stop ignoring
     * @return
     *      True if the player was removed from the ignored list.
     *      False if the player was never ignored.
     */
    public boolean stopIgnoringPlayer (Player player)
    {
        if (!isIgnoringPlayer(player)) {
            return false;
        }

        ignoredPlayers.remove(player);
        saveIgnoredPlayers();
        return true;
    }

    public List<Player> getIgnoredPlayers () {
        return ignoredPlayers;
    }

    /**
     * Sets the time passed since this player has poked another player.
     *
     * @param time
     *      The current system time that the poke took place.
     */
    public void setLastPokeTime (long time) {
        lastPokeTime = time;
    }

    /**
     * Gets the time passed since this player has poked another player
     *
     * @return
     *      Long
     */
    public long getLastPokeTime () {
        return lastPokeTime;
    }

    private void saveIgnoredPlayers ()
    {
        CustomConfig playerConfig = plugin.getPlayerConfig(playerId);

        // Create a new config if there are ignored players to save
        if (playerConfig == null && ignoredPlayers.size() > 0) {
            playerConfig = plugin.getNewPlayerConfig(playerId);
        }

        // Cancel if the config is still null
        if (playerConfig == null) {
            return;
        }

        List<String> ignoredIds = new ArrayList<>();
        for (Player player : ignoredPlayers) {
            ignoredIds.add(player.getUniqueId().toString());
        }

        playerConfig.set(Settings.POKE_PLAYER_IGNORED_LIST_POINTER.getPath(), ignoredIds);
        playerConfig.save();
    }
}
