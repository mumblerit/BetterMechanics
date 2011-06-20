package com.edoxile.bukkit.bettermechanics.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */

import com.edoxile.bukkit.bettermechanics.BetterMechanics;
import com.edoxile.bukkit.bettermechanics.exceptions.KeyNotFoundException;
import org.bukkit.util.config.Configuration;

import java.util.logging.Logger;

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
        if(recipeNames == null){
            log.warning("[BetterMechanics] Error loading cauldron recipes: no recipes found! (you probably messed up the yml format somewhere)");
            return;
        }
        for (String name : recipeNames) {
            MaterialMap ingredients = new MaterialMap();
            MaterialMap results = new MaterialMap();
            try {
                List<List<Integer>> list = (List<List<Integer>>) config.getProperty("cauldron.recipes." + name + ".ingredients");
                for (List<Integer> l : list) {
                    ingredients.put(l.get(0), l.get(1));
                }
                list = (List<List<Integer>>) config.getProperty("cauldron.recipes." + name + ".results");
                for (List<Integer> l : list) {
                    results.put(l.get(0), l.get(1));
                }
            }catch(Throwable e){
                recipes.clear();
                log.warning("[BetterMechanics] Error loading cauldron recipes: " + e.getCause()  + " (you probably messed up the yml format somewhere)");
                return;
            }

            add(new Recipe(name, ingredients, results));
        }
        log.info("[BetterMechanics] Cauldron loaded " + size() + " recipes.");
    }

    public void add(Recipe recipe) {
        recipes.add(recipe);
    }

    public Recipe find(MaterialMap ingredients) {
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
        private final MaterialMap ingredients;
        private final MaterialMap results;

        public Recipe(String name, MaterialMap ingredients, MaterialMap results) {
            this.name = name;
            this.ingredients = ingredients;
            this.results = results;
        }

        public String getName() {
            return name;
        }

        public boolean hasAllIngredients(MaterialMap check) {
            MaterialMapIterator iterator = ingredients.iterator();
            do {
                iterator.next();
                try {
                    if (check.get(iterator.key()) < iterator.value()) {
                        return false;
                    }
                } catch (KeyNotFoundException e) {
                    return false;
                }
            } while (iterator.hasNext());
            return true;
        }

        public MaterialMap getResults() {
            return results;
        }

        public MaterialMap getIngredients() {
            return ingredients;
        }
    }
}