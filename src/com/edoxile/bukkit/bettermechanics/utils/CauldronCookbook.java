package com.edoxile.bukkit.bettermechanics.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */

import com.edoxile.bukkit.bettermechanics.BetterMechanics;
import org.bukkit.util.config.Configuration;

import java.util.logging.Logger;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Store of recipes.
 *
 * @author sk89q, Edoxile
 */
public class CauldronCookbook {
    private List<Recipe> recipes = new ArrayList<Recipe>();
    private static final Logger log = Logger.getLogger("Minecraft");
    private BetterMechanics instance;
    private Configuration config;

    public CauldronCookbook(BetterMechanics plugin) {
        instance = plugin;
        config = instance.getConfiguration();
        List<String> recipeNames = config.getKeys("cauldron.recipes");
        for (String name : recipeNames) {
            IntIntMap ingredients = new IntIntMap();
            IntIntMap results = new IntIntMap();

            List<List<Integer>> list = (List<List<Integer>>) config.getProperty("cauldron.recipes." + name + ".ingredients");
            for (List<Integer> l : list) {
                ingredients.put(l.get(0), l.get(1));
            }
            list = (List<List<Integer>>) config.getProperty("cauldron.recipes." + name + ".results");
            for (List<Integer> l : list) {
                results.put(l.get(0), l.get(1));
            }

            add(new Recipe(name, ingredients, results));
        }
        log.info("[BetterMechanics] Cauldron loaded " + size() + " recipes.");
    }

    public void add(Recipe recipe) {
        recipes.add(recipe);
    }

    public Recipe find(IntIntMap ingredients) {
        for (Recipe recipe : recipes) {
            if (recipe.hasAllIngredients(ingredients)) {
                return recipe;
            }
        }
        return null;
    }

    public int size() {
        return recipes.size();
    }

    public static final class Recipe {
        private final String name;
        private final IntIntMap ingredients;
        private final IntIntMap results;

        public Recipe(String name, IntIntMap ingredients, IntIntMap results) {
            this.name = name;
            this.ingredients = ingredients;
            this.results = results;
        }

        public String getName() {
            return name;
        }

        public boolean hasAllIngredients(IntIntMap check) {
            return ingredients.equals(check);
        }

        public IntIntMap getResults() {
            return results;
        }
    }
}