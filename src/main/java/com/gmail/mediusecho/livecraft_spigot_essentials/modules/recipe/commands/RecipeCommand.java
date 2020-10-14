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

package com.gmail.mediusecho.livecraft_spigot_essentials.modules.recipe.commands;

import com.gmail.mediusecho.fusion.api.BukkitCommandSender;
import com.gmail.mediusecho.fusion.api.CommandListener;
import com.gmail.mediusecho.fusion.api.annotations.*;
import com.gmail.mediusecho.livecraft_spigot_essentials.Lang;
import com.gmail.mediusecho.livecraft_spigot_essentials.modules.recipe.RecipeModule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


@Command(argument = "recipe", contexts = "@craftable")
@Usage("modules.recipe.messages.usage")
@Syntax("recipe <item>")
public class RecipeCommand extends CommandListener {

    @Inject private RecipeModule recipeModule;

    @Default
    @Permission("lce.command.modules.recipe")
    @Contract("player_only")
    public void showRecipe (@NotNull BukkitCommandSender sender, Material material)
    {
        if (material == null)
        {
            sender.sendMessage(Lang.RECIPE_NO_RECIPE.get("{1}", sender.getCommandArgument(1)));
            return;
        }

        ItemStack item = new ItemStack(material);
        recipeModule.showRecipes(sender.getPlayer(), item);
    }
}
