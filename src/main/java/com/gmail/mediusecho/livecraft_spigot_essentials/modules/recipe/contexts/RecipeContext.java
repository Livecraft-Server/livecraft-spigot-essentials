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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.recipe.contexts;

import com.gmail.mediusecho.fusion.api.commands.ContextProvider;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecipeContext implements ContextProvider {

    private List<String> recipes;

    public RecipeContext ()
    {
        recipes = new ArrayList<>();

        Iterator<Recipe> recipeIterator = Bukkit.getServer().recipeIterator();
        while (recipeIterator.hasNext())
        {
            Recipe recipe = recipeIterator.next();
            if (recipe instanceof ShapelessRecipe) {
                recipes.add(recipe.getResult().getType().toString());
            }

            if (recipe instanceof ShapedRecipe) {
                recipes.add(recipe.getResult().getType().toString());
            }
        }
    }

    @Override
    public List<String> getContext() {
        return recipes;
    }
}
