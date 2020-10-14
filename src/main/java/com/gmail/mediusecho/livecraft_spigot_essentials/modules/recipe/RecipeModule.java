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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.recipe;

import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.Module;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.recipe.commands.RecipeCommand;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.recipe.preview.RecipePreview;
import com.gmail.mediusecho.livecraft_spigot_essentials.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecipeModule extends Module {

    private Map<UUID, PreviewContainer> previewMap;

    public RecipeModule(LivecraftSpigotEssentials plugin)
    {
        super(plugin, Settings.RECIPE_ENABLED);
        plugin.getMainCommand().registerCommand(new RecipeCommand());

        previewMap = new HashMap<>();
    }

    @Override
    protected void onReload() { }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick (@NotNull InventoryClickEvent event)
    {
        plugin.log(event.getRawSlot());
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player)event.getWhoClicked();
        if (previewMap.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose (@NotNull InventoryCloseEvent event)
    {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player)event.getPlayer();
        UUID id = player.getUniqueId();

        if (previewMap.containsKey(id))
        {
            PreviewContainer previewContainer = previewMap.get(id);
            if (previewContainer.onClose()) {
                previewMap.remove(id);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen (@NotNull InventoryOpenEvent event)
    {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player)event.getPlayer();
        UUID id = player.getUniqueId();

        if (previewMap.containsKey(id)) {
            previewMap.get(id).onOpen();
        }
    }

    public void showRecipes (Player player, ItemStack item)
    {
        if (!this.isEnabled) {
            return;
        }

        List<Recipe> itemRecipes = Bukkit.getRecipesFor(item);
        if (itemRecipes.isEmpty())
        {
            player.sendMessage(Lang.RECIPE_NO_RECIPE.get("{1}", item.getType().toString().toLowerCase()));
            return;
        }

        PreviewContainer previewContainer = new PreviewContainer(plugin, player, itemRecipes);
        previewMap.put(player.getUniqueId(), previewContainer);

        previewContainer.showRecipes();
    }
}
