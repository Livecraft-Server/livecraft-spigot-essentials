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

import com.gmail.mediusecho.livecraft_spigot_essentials.LivecraftSpigotEssentials;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.recipe.preview.RecipePreview;
import com.gmail.mediusecho.livecraft_spigot_essentials.util.RecipeUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PreviewContainer extends BukkitRunnable {

    private final LivecraftSpigotEssentials plugin;
    private final Player player;

    private List<RecipePreview> previewList;
    private int previewIndex = 0;
    private boolean canClose = true;
    private boolean canCancel = false;

    public PreviewContainer (LivecraftSpigotEssentials plugin, Player player, @NotNull List<Recipe> recipes)
    {
        this.plugin = plugin;
        this.player = player;
        previewList = new ArrayList<>();

        for (Recipe recipe : recipes)
        {
            RecipePreview preview = RecipeUtil.getPreview(recipe);
            if (preview != null) {
                previewList.add(preview);
            }
        }
    }

    @Override
    public void run()
    {
        showNextPreview();
    }

    public void showRecipes ()
    {
        if (previewList.size() > 1)
        {
            canCancel = true;
            runTaskTimer(plugin, 0L, 40L);
        }

        else {
            showNextPreview();
        }
    }

    public boolean onClose ()
    {
        if (canClose)
        {
            if (canCancel) {
                this.cancel();
            }
            return true;
        }
        return false;
    }

    public void onOpen () {
        canClose = true;
    }

    private void showNextPreview ()
    {
        RecipePreview preview = previewList.get(previewIndex++);
        if (previewIndex >= previewList.size()) {
            previewIndex = 0;
        }

        canClose = false;
        preview.open(player);
    }
}
