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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.recipe.preview;

import com.gmail.mediusecho.livecraft_spigot_essentials.util.RecipeUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class WorkbenchPreview extends RecipePreview {

    public WorkbenchPreview(Recipe recipe)
    {
        inventory = Bukkit.createInventory(null, InventoryType.WORKBENCH);

        if (recipe instanceof ShapelessRecipe)
        {
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe)recipe;
            List<ItemStack> ingredients = shapelessRecipe.getIngredientList();

            inventory.setItem(0, shapelessRecipe.getResult());
            for (int i = 0; i < ingredients.size(); i++) {
                inventory.setItem(i + 1, ingredients.get(i));
            }
        }

        else if (recipe instanceof ShapedRecipe)
        {
            ShapedRecipe shapedRecipe = (ShapedRecipe)recipe;
            String[] shape = shapedRecipe.getShape();
            Integer[] slots = RecipeUtil.getRecipeShapeSlot(shape[0].length(), shape.length);

            if (slots != null)
            {
                List<ItemStack> ingredients = new ArrayList<>(shapedRecipe.getIngredientMap().values());

                inventory.setItem(0, shapedRecipe.getResult());
                for (int i = 0; i < ingredients.size(); i++) {
                    inventory.setItem(slots[i], ingredients.get(i));
                }
            }
        }
    }


}
