package com.edoxile.bukkit.bettermechanics.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */

import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Store of recipes.
 *
 * @author sk89q
 */
public class CauldronCookbook {
    public CauldronCookbook() {

        try {
            CauldronCookbook recipes = readCauldronRecipes("cauldron-recipes.txt");
            if (recipes.size() != 0) {
                log.info(recipes.size() + " cauldron recipe(s) loaded");
            } else {
                log.warning("cauldron-recipes.txt had no recipes");
            }
        } catch (FileNotFoundException e) {
            log.info("cauldron-recipes.txt not found: " + e.getMessage());
            try {
                log.info("Looked in: " + (new File(".")).getCanonicalPath() + "/plugins/CraftBookMechanisms");
            } catch (IOException ioe) {
            }
        } catch (IOException e) {
            log.warning("cauldron-recipes.txt not loaded: " + e.getMessage());
        }
    }

    private List<Recipe> recipes = new ArrayList<Recipe>();

    static Logger log = Logger.getLogger("Minecraft");

    public void add(Recipe recipe) {
        recipes.add(recipe);
    }

    public Recipe find(Map<Integer, Integer> ingredients) {
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

    private CauldronCookbook readCauldronRecipes(String path)
            throws IOException {
        File file = new File("plugins/BetterMechanics", path);
        FileReader input = null;
        try {
            input = new FileReader(file);
            BufferedReader buff = new BufferedReader(input);
            String line;
            while ((line = buff.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                if (line.charAt(0) == ';' || line.charAt(0) == '#' || line.equals("")) {
                    continue;
                }
                String[] parts = line.split(":");
                if (parts.length < 3) {
                    log.warning("Invalid cauldron recipe line in "
                            + file.getName() + ": '" + line + "'");
                } else {
                    String name = parts[0];
                    List<Integer> ingredients = parseCauldronItems(parts[1]);
                    List<Integer> results = parseCauldronItems(parts[2]);
                    String[] groups = null;
                    if (parts.length >= 4 && parts[3].trim().length() > 0) {
                        groups = parts[3].split(",");
                    }
                    CauldronCookbook.Recipe recipe =
                            new CauldronCookbook.Recipe(name, ingredients, results, groups);
                    add(recipe);
                }
            }
            return this;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private List<Integer> parseCauldronItems(String list) {
        String[] parts = list.split(",");

        List<Integer> out = new ArrayList<Integer>();

        for (String part : parts) {
            int multiplier = 1;

            try {
                if (part.matches("^.*\\*([0-9]+)$")) {
                    int at = part.lastIndexOf("*");
                    multiplier = Integer.parseInt(
                            part.substring(at + 1, part.length()));
                    part = part.substring(0, at);
                }

                try {
                    for (int i = 0; i < multiplier; i++) {
                        out.add(Integer.valueOf(part));
                    }
                } catch (NumberFormatException e) {
                    log.warning("Cauldron: Unknown item " + part);
                }
            } catch (NumberFormatException e) { // Bad multiplier
                log.warning("Cauldron: Bad multiplier in '" + part + "'");
            }
        }
        return out;
    }
    public static final class Recipe {
        private final String name;
        private final Map<Integer, Integer> ingredientLookup = new HashMap<Integer, Integer>();
        private final List<Integer> results;

        public Recipe(String name, List<Integer> ingredients,
                      List<Integer> results, String[] groups) {
            this.name = name;
            this.results = Collections.unmodifiableList(results);
            for (Integer id : ingredients) {
                if (ingredientLookup.containsKey(id)) {
                    ingredientLookup.put(id, ingredientLookup.get(id) + 1);
                } else {
                    ingredientLookup.put(id, 1);
                }
            }
        }

        public String getName() {
            return name;
        }

        public boolean hasAllIngredients(Map<Integer, Integer> check) {
            for (Map.Entry<Integer, Integer> entry : ingredientLookup.entrySet()) {
                int id = entry.getKey();
                if (!check.containsKey(id)) {
                    return false;
                } else if (check.get(id) < entry.getValue()) {
                    return false;
                }
            }
            return true;
        }

        public List<Integer> getResults() {
            return results;
        }
    }
}