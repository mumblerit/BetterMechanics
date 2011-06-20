package com.edoxile.bukkit.bettermechanics.mechanics;

import com.edoxile.bukkit.bettermechanics.exceptions.KeyNotFoundException;
import com.edoxile.bukkit.bettermechanics.utils.CauldronCookbook;
import com.edoxile.bukkit.bettermechanics.utils.IntIntMap;
import com.edoxile.bukkit.bettermechanics.utils.IntIntMapIterator;
import com.edoxile.bukkit.bettermechanics.utils.MechanicsConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 * Author: sk89q for the most part.
 */
public class Cauldron {
    public static Cauldron preCauldron(Block clickedBlock, MechanicsConfig c, Player p) {
        if (!c.getCauldronConfig().enabled)
            return null;
        Block tempBlock;
        int dx, dy, dz;
        //First see if there is lava below the clicked block
        for (dy = -1; dy >= -3; dy--) {
            tempBlock = clickedBlock.getRelative(0, dy, 0);
            if (tempBlock.getType() == Material.LAVA || tempBlock.getType() == Material.STATIONARY_LAVA)
                break;
        }
        if (dy > -4) {
            //Search for cauldron walls
            if (clickedBlock.getRelative(BlockFace.WEST).getType() == Material.STONE && clickedBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getType() == Material.STONE) {
                dz = -1;
            } else if (clickedBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getType() == Material.STONE && clickedBlock.getRelative(BlockFace.EAST).getType() == Material.STONE) {
                dz = 0;
            } else {
                return null;
            }
            if (clickedBlock.getRelative(BlockFace.NORTH).getType() == Material.STONE && clickedBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getType() == Material.STONE) {
                dx = 0;
            } else if (clickedBlock.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getType() == Material.STONE && clickedBlock.getRelative(BlockFace.SOUTH).getType() == Material.STONE) {
                dx = -1;
            } else {
                return null;
            }

            //We don't want the lava in our recipe
            dy++;
            HashSet<Block> blockSet = new HashSet<Block>();
            Block referencePoint = clickedBlock.getRelative(dx, dy, dz);
            for (int ndy = 0; ndy <= 3; ndy++) {
                for (int ndx = 0; ndx <= 1; ndx++) {
                    for (int ndz = 0; ndz <= 1; ndz++) {
                        if (referencePoint.getRelative(ndx, ndy, ndz).getType() != Material.AIR) {
                            blockSet.add(referencePoint.getRelative(ndx, ndy, ndz));
                        }
                    }
                }
            }
            return new Cauldron(c, p, blockSet);
        }
        return null;
    }

    private Player player;
    private HashSet<Block> contents = new HashSet<Block>();
    private CauldronCookbook recipes;

    public Cauldron(MechanicsConfig c, Player p, HashSet<Block> con) {
        player = p;
        contents = con;
        recipes = c.getCauldronConfig().cauldronCookbook;
    }

    public boolean performCauldron() {
        IntIntMap map = new IntIntMap();

        for (Block b : contents) {
            map.add(b.getTypeId(), 1);
        }

        // Find the recipe
        CauldronCookbook.Recipe recipe = recipes.find(map);

        if (recipe != null) {
            player.sendMessage(ChatColor.GOLD + "In a poof of smoke, you've made " + recipe.getName() + ".");

            IntIntMap ingredients = recipe.getIngredients().clone();

            for (Block b : contents) {
                try {
                    if (ingredients.get(b.getTypeId()) > 0) {
                        ingredients.remove(b.getTypeId(), 1);
                        b.setType(Material.AIR);
                    }
                } catch (KeyNotFoundException e) {
                    continue;
                }
            }

            // Give results
            IntIntMapIterator iterator = recipe.getResults().iterator();
            while (iterator.hasNext()) {
                iterator.next();
                HashMap<Integer, ItemStack> inventoryMap = player.getInventory().addItem(new ItemStack(iterator.key(), iterator.value()));
                for (Map.Entry<Integer, ItemStack> i : inventoryMap.entrySet()) {
                    player.getLocation().getWorld().dropItem(player.getLocation(), i.getValue());
                }
            }
            player.updateInventory();
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "Hmm, this doesn't make anything...");
            return false;
        }
    }
}
