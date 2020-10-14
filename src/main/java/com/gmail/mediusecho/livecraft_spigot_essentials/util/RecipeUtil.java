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

package com.gmail.mediusecho.livecraft_spigot_essentials.util;

import com.gmail.mediusecho.livecraft_spigot_essentials.modules.recipe.preview.*;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.Map;

public class RecipeUtil {

    public static Map<Integer, Integer[]> shapedRecipeMap = new HashMap<>();

    static
    {
        addShapedRecipe(1, 1, 5);
        addShapedRecipe(1, 2, 5, 8);
        addShapedRecipe(1, 3, 2, 5, 8);

        addShapedRecipe(2, 1, 4, 5);
        addShapedRecipe(2, 2, 1, 2, 4, 5);
        addShapedRecipe(2, 3, 1, 2, 4, 5, 7, 8);

        addShapedRecipe(3, 1, 4, 5, 6);
        addShapedRecipe(3, 2, 4, 5, 6, 7, 8, 9);
        addShapedRecipe(3, 3, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    public static Integer[] getRecipeShapeSlot (int a, int b) {
        return shapedRecipeMap.get(intHash(a, b));
    }

    private static int intHash (int a, int b)
    {
        int hash = 23;
        hash = hash * 31 + a;
        hash = hash * 31 + b;
        return hash;
    }

    private static void addShapedRecipe (int a, int b, Integer... slots) {
        shapedRecipeMap.put(intHash(a, b), slots);
    }

    public static RecipePreview getPreview (Recipe recipe)
    {
        if (recipe instanceof ShapelessRecipe) {
            return new WorkbenchPreview(recipe);
        }

        if (recipe instanceof ShapedRecipe) {
            return new WorkbenchPreview(recipe);
        }

        // The stonecutter inventory doesn't show items for some reason
//        if (recipe instanceof StonecuttingRecipe) {
//            return new StonecutterPreview((StonecuttingRecipe)recipe);
//        }

        if (recipe instanceof SmokingRecipe) {
            return new SmokerPreview((SmokingRecipe)recipe);
        }

        // Can't change the title of smoker so this just shows the same view as the smoker recipe
//        if (recipe instanceof CampfireRecipe) {
//            return new CampfirePreview((CampfireRecipe)recipe);
//        }

        if (recipe instanceof BlastingRecipe) {
            return new BlastFurnacePreview((BlastingRecipe)recipe);
        }

        if (recipe instanceof FurnaceRecipe) {
            return new FurnacePreview((FurnaceRecipe)recipe);
        }

        return null;
    }
}
