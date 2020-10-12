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
}
