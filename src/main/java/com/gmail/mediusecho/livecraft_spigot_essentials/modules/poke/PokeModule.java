package com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke;

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.Permission;
import com.gmail.mediusecho.livecraft_spigot_essentials.config.CustomConfig;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.poke.commands.PokeCommand;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PokeModule extends Module {

    private final Map<UUID, PokeData> pokeDataMap;

    private Sound sound;
    private float volume;
    private float pitch;
    private Particle particle;
    private int cooldownTime;

    public PokeModule(LivecraftSpigotEssentials plugin)
    {
        super(plugin, Settings.POKE_ENABLED);

        pokeDataMap = new HashMap<>();
        plugin.getMainCommand().registerCommand(new PokeCommand(this));
    }

    @Override
    protected void onReload()
    {
        sound = Settings.POKE_SOUND.getValue();
        volume = Settings.POKE_SOUND_VOLUME.getValue();
        pitch = Settings.POKE_SOUND_PITCH.getValue();
        particle = Settings.POKE_PARTICLE.getValue();
        cooldownTime = Settings.POKE_COMMAND_COOLDOWN.getValue();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract (@NotNull PlayerInteractAtEntityEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (!Permission.POKE.hasPermission(player)) {
            return;
        }

        Entity entity = event.getRightClicked();
        if (!(entity instanceof Player)) {
            return;
        }

        if (entity.hasMetadata("NPC")) {
            return;
        }

        Player pokee = (Player)entity;
        if (isIgnoringPlayer(pokee.getUniqueId(), player)) {
            return;
        }

        poke(pokee);
    }

    /**
     * Returns the {@link PokeData} associated with this UUID.
     *
     * @param id
     *      The unique id to check
     * @return
     *      PokeData
     */
    public PokeData getPokeData (UUID id)
    {
        if (pokeDataMap.containsKey(id)) {
            return pokeDataMap.get(id);
        }

        PokeData pokeData = new PokeData(plugin, id);
        pokeDataMap.put(id, pokeData);

        CustomConfig playerConfig = plugin.getPlayerConfig(id);
        if (playerConfig == null) {
            return pokeData;
        }

        List<String> ignoredIds = playerConfig.getConfig().getStringList(Settings.POKE_PLAYER_IGNORED_LIST_POINTER.getPath());
        for (String s : ignoredIds)
        {
            UUID uuid = UUID.fromString(s);
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                pokeData.ignorePlayer(player);
            }
        }

        return pokeData;
    }

    /**
     * Pokes this player
     *
     * @param player
     *      The player to poke
     */
    public void poke (@NotNull Player player)
    {
        Location location = player.getLocation();
        player.playSound(location, sound, volume, pitch);

        for (int i = 0; i < 5; i++)
        {
            double x = getRandomOffset();
            double y = getRandomOffset() + 0.5;
            double z = getRandomOffset();

            player.getWorld().spawnParticle(particle, location.clone().add(x, y, z), 1);
        }
    }

    public boolean canPoke (UUID id)
    {
        PokeData pokeData = getPokeData(id);
        long lastPokeTime = pokeData.getLastPokeTime();

        if ((System.currentTimeMillis() - lastPokeTime) > cooldownTime)
        {
            pokeData.setLastPokeTime(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    /**
     * Checks to see if a player can be poked
     *
     * @param id The {@link UUID} of the player poking
     * @param player The player being poked
     * @return Returns true if the player can be poked by this player
     */
    public boolean isIgnoringPlayer (UUID id, Player player) {
        return getPokeData(id).isIgnoringPlayer(player);
    }

    /**
     * Get a random offset value
     *
     * @return
     *      Returns a random double from -0.5 to 0.5
     */
    private double getRandomOffset() {
        return Math.random() - 0.5;
    }
}
