package com.edoxile.bukkit.bettermechanics.mechanics;

import com.edoxile.bukkit.bettermechanics.utils.CauldronCookbook;
import com.edoxile.bukkit.bettermechanics.utils.MechanicsConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.swing.plaf.TreeUI;
import java.util.*;

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
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> noGlassMap = new HashMap<Integer, Integer>();

        for (Block b : contents) {
            if (b.getTypeId() == Material.GLASS.getId()) {
                if (!map.containsKey(b.getTypeId())) {
                    map.put(b.getTypeId(), 1);
                } else {
                    map.put(b.getTypeId(), map.get(b.getTypeId()) + 1);
                }
            } else {
                if (!map.containsKey(b.getTypeId())) {
                    noGlassMap.put(b.getTypeId(), 1);
                    map.put(b.getTypeId(), 1);
                } else {
                    noGlassMap.put(b.getTypeId(), noGlassMap.get(b.getTypeId()) + 1);
                    map.put(b.getTypeId(), map.get(b.getTypeId()) + 1);
                }
            }
        }

        // Find the recipe
        CauldronCookbook.Recipe recipe = recipes.find(map);

        if (recipe != null) {

            player.sendMessage(ChatColor.GOLD + "In a proof of smoke, you've made " + recipe.getName() + ".");

            for (Block b : contents) {
                if (isDependant(b.getTypeId())) {
                    b.setType(Material.AIR);
                }
            }
            for (Block b : contents) {
                b.setType(Material.AIR);
            }

            // Give results
            for (Integer id : recipe.getResults()) {
                HashMap<Integer, ItemStack> inventoryMap = player.getInventory().addItem(new ItemStack(id, 1));
                for (Map.Entry<Integer, ItemStack> i : inventoryMap.entrySet()) {
                    player.getLocation().getWorld().dropItem(player.getLocation(), i.getValue());
                }
            }
            player.updateInventory();
            return true;
        } else {
            //Check if glassless recipe exists:
            recipe = recipes.find(noGlassMap);
            if (recipe != null) {

                player.sendMessage(ChatColor.GOLD + "In a proof of smoke, you've made " + recipe.getName() + ".");

                for (Block b : contents) {
                    if (b.getTypeId() != Material.GLASS.getId() && isDependant(b.getTypeId())) {
                        b.setType(Material.AIR);
                    }
                }
                for (Block b : contents) {
                    if (b.getTypeId() != Material.GLASS.getId())
                        b.setType(Material.AIR);
                }

                // Give results
                for (Integer id : recipe.getResults()) {
                    HashMap<Integer, ItemStack> inventoryMap = player.getInventory().addItem(new ItemStack(id, 1));
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

    private boolean isDependant(int itemId) {
        switch (itemId) {
            case 6:
            case 31:
            case 32:
            case 37:
            case 38:
            case 39:
            case 40:
            case 50:
            case 51:
            case 55:
            case 59:
            case 69:
            case 70:
            case 71:
            case 72:
            case 75:
            case 76:
            case 78:
            case 81:
            case 83:
            case 93:
            case 94:
            case 96:
                return true;
            default:
                return false;
        }
    }
}
