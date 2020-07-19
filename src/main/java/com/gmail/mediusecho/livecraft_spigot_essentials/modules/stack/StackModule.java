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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.stack;

import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.Permission;
import com.gmail.mediusecho.livecraft_spigot_essentials.config.CustomConfig;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.stack.commands.StackCommand;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.MetadataUtil;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;

public class StackModule extends Module {

    private final List<EntityDamageEvent.DamageCause> damageCauses;

    private List<EntityType> whitelistedEntities;
    private List<World> whitelistedWorlds;
    private Map<UUID, Long> lastInteractTime;
    private Map<UUID, Boolean> playerStackingOptStatus;

    private double interactCooldown;
    private double standingVelocityMultiplier;
    private double runningVelocityMultiplier;
    private double minimumWalkSpeed;

    private boolean cancelFallDamage;
    private boolean cancelSuffocationDamage;
    private boolean showPickupAndDropMessages;
    private boolean applySlowdown;

    public StackModule(LivecraftSpigotEssentials plugin)
    {
        super(plugin, Settings.STACK_ENABLED);
        plugin.getMainCommand().registerCommand(new StackCommand());

        damageCauses = new ArrayList<>(Arrays.asList(EntityDamageEvent.DamageCause.FALL, EntityDamageEvent.DamageCause.SUFFOCATION));
        whitelistedEntities = new ArrayList<>();
        whitelistedWorlds = new ArrayList<>();
        lastInteractTime = new HashMap<>();
        playerStackingOptStatus = new HashMap<>();
    }

    @Override
    protected void onReload()
    {
        interactCooldown = Settings.STACK_COOLDOWN.getValue();
        standingVelocityMultiplier = Settings.STACK_STANDING_MULTIPLIER.getValue();
        runningVelocityMultiplier = Settings.STACK_RUNNING_MULTIPLIER.getValue();
        minimumWalkSpeed = Settings.STACK_MIMINUM_WALK_SPEED.getValue();

        cancelFallDamage = Settings.STACK_CANCEL_FALL_DAMAGE.getValue();
        cancelSuffocationDamage = Settings.STACK_CANCEL_SUFFOCATION_DAMAGE.getValue();
        showPickupAndDropMessages = Settings.STACK_SHOW_PICKUP_AND_DROP_MESSAGES.getValue();
        applySlowdown = Settings.STACK_PLAYER_SLOWDOWN.getValue();

        FileConfiguration config = plugin.getConfig();

        whitelistedEntities.clear();
        for (String entity : config.getStringList(Settings.STACK_WHITELISTED_ENTITIES_POINTER.getPath()))
        {
            try {
                EntityType type = EntityType.valueOf(entity);
                whitelistedEntities.add(type);
            } catch (Exception ignored) {}
        }

        whitelistedWorlds.clear();
        for (String worldName : config.getStringList(Settings.STACK_WHITELISTED_WORLDS_POINTER.getPath()))
        {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                whitelistedWorlds.add(world);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract (@NotNull PlayerInteractEntityEvent event)
    {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (!whitelistedWorlds.contains(world)) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        if (!canInteractWithEntity(player)) {
            return;
        }

        int playerStackLimit = StringUtil.getIntegerValueForPermission(player,
                Permission.STACK_ENTITY_LIMIT.getPermission());

        if (getEntityStackedCount(player) >= playerStackLimit)
        {
            if (playerStackLimit != 0) {
                player.sendMessage(Lang.STACK_LIMIT_REACHED.get());
            }
            return;
        }

        final Location projectedLocation = getProjectedLocation(player);
        List<Entity> entities = player.getNearbyEntities(2, 2, 2);

        // Sort entities by distance so we're right clicking on the nearest non-stacked entity
        entities.sort(Comparator.comparingInt(o -> (int) o.getLocation().distanceSquared(projectedLocation)));

        for (Entity entity : entities)
        {
            if (!canStackEntity(player, entity)) {
                continue;
            }

            // Skip players that don't want to be stacked
            if ((entities instanceof Player) && !playerHasOptedIn((Player)entities)) {
                continue;
            }

            addEntityToStack(player, entity);
            event.setCancelled(true);
            break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity (@NotNull EntityDamageByEntityEvent event)
    {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }

        Player player = (Player)damager;
        Entity entity = removeEntityFromStack(player);

        if (entity != null)
        {
            double multiplier = player.isSprinting() ? runningVelocityMultiplier : standingVelocityMultiplier;
            entity.setVelocity(player.getEyeLocation().getDirection().multiply(multiplier));

            // Only apply this metadata if we think the entity will take fall damage
            if (cancelFallDamage) {
                entity.setMetadata(MetadataUtil.STACKING_ENTITY_THROWN, new FixedMetadataValue(plugin, ""));
            }

            if (showPickupAndDropMessages) {
                player.sendMessage(getEntityStackMessage(entity, false, multiplier > 0));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage (@NotNull EntityDamageEvent event)
    {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (!damageCauses.contains(cause)) {
            return;
        }

        Entity entity = event.getEntity();
        if (cause.equals(EntityDamageEvent.DamageCause.FALL))
        {
            if (!entity.hasMetadata(MetadataUtil.STACKING_ENTITY_THROWN)) {
                return;
            }

            event.setCancelled(true);
            entity.removeMetadata(MetadataUtil.STACKING_ENTITY_THROWN, plugin);
            return;
        }

        if (cause.equals(EntityDamageEvent.DamageCause.SUFFOCATION) && cancelSuffocationDamage)
        {
            if (!entity.hasMetadata(MetadataUtil.STACKING_ENTITY_OWNER)) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDismount (@NotNull EntityDismountEvent event)
    {
        Entity entity = event.getEntity();
        if (!entity.hasMetadata(MetadataUtil.STACKING_ENTITY_OWNER)) {
            return;
        }

        MetadataValue data = entity.getMetadata(MetadataUtil.STACKING_ENTITY_OWNER).get(0);
        if (data.value() instanceof Player)
        {
            Player owner = (Player)data.value();
            if (owner == null) {
                return;
            }

            entity.removeMetadata(MetadataUtil.STACKING_ENTITY_OWNER, plugin);
            if (applySlowdown) {
                recalculateWeightConstraints(owner, -1);
            }
        }
    }

    /**
     * Get how many entities the player currently has stacked.
     *
     * @param player
     *      The player to check.
     * @return
     *      Returns the amount of entities currently stacked.
     */
    public int getEntityStackedCount (Player player) {
        return getAllEntitiesInSTack(player).size();
    }

    /**
     * Checks to see if enough time has passed in
     * order for this player to be able to interact.
     *
     * @param player
     *      The player to check.
     * @return
     *      Returns true if the player can interact again.
     */
    public boolean canInteractWithEntity (@NotNull Player player)
    {
        UUID id = player.getUniqueId();
        if (!lastInteractTime.containsKey(id))
        {
            lastInteractTime.put(id, System.currentTimeMillis());
            return true;
        }

        long start = lastInteractTime.get(id);
        long end = System.currentTimeMillis();
        float seconds = (end - start) / 1000f;

        if (seconds > interactCooldown)
        {
            lastInteractTime.put(id, System.currentTimeMillis());
            return true;
        }

        return false;
    }

    /**
     * Checks to see if the player has opted into player stacking.
     *
     * @param player
     *      The player to check.
     * @return
     *      True if the player has opted in.
     */
    public boolean playerHasOptedIn (@NotNull Player player)
    {
        UUID id = player.getUniqueId();
        if (playerStackingOptStatus.containsKey(id)) {
            return playerStackingOptStatus.get(id);
        }

        CustomConfig playerConfig = plugin.getPlayerConfig(id);
        if (playerConfig == null) {
            return true;
        }

        boolean status = playerConfig.getConfig().getBoolean("modules.stacking.opt-in");
        playerStackingOptStatus.put(id, status);
        return status;
    }

    /**
     * Set this players stack opt status.
     *
     * @param player
     *      The player we're toggling for.
     * @param status
     *      The players stack opted status.
     */
    public void togglePlayerOptedStatus (@NotNull Player player, boolean status)
    {
        UUID id = player.getUniqueId();
        playerStackingOptStatus.put(id, status);

        CustomConfig playerConfig = plugin.getPlayerConfig(id);
        if (playerConfig == null) {
            playerConfig = plugin.getNewPlayerConfig(id);
        }

        playerConfig.set("modules.stacking.opt-in", status);
        playerConfig.save();
    }

    /**
     * Returns a Location in the direction the player is looking.
     *
     * @param player
     *      The player to check
     * @return
     *      Location the player is looking at
     */
    public Location getProjectedLocation (@NotNull Player player)
    {
        Location eyeLocation = player.getEyeLocation();
        Vector projectedDirection = eyeLocation.getDirection().multiply(2);
        return eyeLocation.add(projectedDirection);
    }

    /**
     * Applies a weight constraint to the player based
     * on how many entities they have stacked
     *
     * @param player
     *      The player to recalculate
     */
    public void recalculateWeightConstraints (Player player, int modifier)
    {
        int playerStackLimit = StringUtil.getIntegerValueForPermission(player,
                Permission.STACK_ENTITY_LIMIT.getPermission());

        if (playerStackLimit == 0) {
            return;
        }

        int currentStackSize = Math.max(0, getEntityStackedCount(player) + modifier);
        float walkSpeed = calculateWalkSpeed(currentStackSize, playerStackLimit);
        player.setWalkSpeed(walkSpeed);
    }

    /**
     * Adds an Entity to the stack.
     *
     * @param player
     *      The player we're adding this entity to
     * @param entity
     *      The Entity being added
     */
    private void addEntityToStack (@NotNull Player player, Entity entity)
    {
        Entity origin = player.getPassengers().size() == 0 ? player : getLastEntityInStack(player);
        if (origin == null) {
            return;
        }

        origin.addPassenger(entity);
        if (!player.hasMetadata(MetadataUtil.STACKING_ENTITY)) {
            player.setMetadata(MetadataUtil.STACKING_ENTITY, new FixedMetadataValue(plugin, "true"));
        }

        entity.setMetadata(MetadataUtil.STACKING_ENTITY_OWNER, new FixedMetadataValue(plugin, player));
        if (applySlowdown) {
            recalculateWeightConstraints(player, 0);
        }

        if (showPickupAndDropMessages) {
            player.sendMessage(getEntityStackMessage(entity, true, false));
        }
    }

    /**
     * Returns the Entity at the top of the stack, or null if nothing is found.
     *
     * @param player
     *      The player we're removing entities from.
     * @return
     *      The top most entity in the stack.
     */
    @Nullable
    private Entity removeEntityFromStack (Player player)
    {
        Entity entity = getLastEntityInStack(player);
        if (entity == null || entity.getVehicle() == null) {
            return null;
        }

        entity.getVehicle().eject();
        if (player.getPassengers().size() == 0) {
            player.removeMetadata(MetadataUtil.STACKING_ENTITY, plugin);
        }
        return entity;
    }

    /**
     * Returns the top most Entity in the players passenger stack.
     *
     * @param player
     *      Player to get the top most Entity from.
     * @return
     *      The top most Entity if one is found, or null.
     */
    @Nullable
    private Entity getLastEntityInStack (Player player)
    {
        List<Entity> passengers = getAllEntitiesInSTack(player);
        if (passengers.size() == 0) {
            return null;
        }
        return passengers.get(passengers.size() - 1);
    }

    /**
     * Get a list of all entities stacked on top of this player.
     *
     * @param player
     *      The player to check.
     * @return
     *      A list of Entities stacked on top of this player.
     */
    @NotNull
    private List<Entity> getAllEntitiesInSTack (@NotNull Player player)
    {
        List<Entity> passengers = new ArrayList<>();
        if (player.getPassengers().size() == 0) {
            return passengers;
        }

        Entity nextPassenger = player.getPassengers().get(0);
        passengers.add(nextPassenger);

        while (nextPassenger.getPassengers().size() > 0)
        {
            nextPassenger = nextPassenger.getPassengers().get(0);
            passengers.add(nextPassenger);
        }
        return passengers;
    }

    /**
     * Checks to see if this Entity can be stacked
     *
     * @param player
     *      The player to check.
     * @param entity
     *      Entity to check.
     * @return
     *      True if this Entity can be stacked.
     */
    private boolean canStackEntity (Player player, @NotNull Entity entity)
    {
        if (entity.hasMetadata("NPC")) {
            return false;
        }

        if (getAllEntitiesInSTack(player).contains(entity)) {
            return false;
        }

        return whitelistedEntities.contains(entity.getType());
    }

    /**
     * Calculates the players walk speed based on current stack
     * amount and overall stack limit.
     *
     * @param amount
     *      The current amount of stacked entities
     * @param max
     *      The maximum limit of entities a player can stack
     * @return
     *      The calculated walk speed based on the given parameters
     */
    private float calculateWalkSpeed (int amount, int max)
    {
        double modifier = 0.2D * ((double) amount / (double) max);
        return (float) Math.max(0.2 - modifier, minimumWalkSpeed);
    }

    private String getEntityStackMessage (Entity entity, boolean pickingUp, boolean yeeted)
    {
        if (entity instanceof Player)
        {
            Player player = (Player)entity;
            if (pickingUp) {
                return Lang.STACK_PICKUP_PLAYER.get("{1}", player.getDisplayName());
            }

            else
            {
                if (yeeted) {
                    return Lang.STACK_PLAYER_YEETED.get("{1}", player.getDisplayName());
                }
                return Lang.STACK_DROP_PLAYER.get("{1}", player.getDisplayName());
            }
        }

        String entityName = StringUtil.capitalizeFirstLetter(entity.getName());
        if (pickingUp) {
            return Lang.STACK_PICKUP_ENTITY.get("{1}", entityName);
        }

        else
        {
            if (yeeted) {
                return Lang.STACK_ENTITY_YEETED.get("{1}", entityName);
            }
            return Lang.STACK_DROP_ENTITY.get("{1}", entityName);
        }
    }
}
