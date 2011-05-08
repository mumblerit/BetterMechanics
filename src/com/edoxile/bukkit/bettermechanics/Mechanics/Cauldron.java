package com.edoxile.bukkit.bettermechanics.Mechanics;

import com.edoxile.bukkit.bettermechanics.Utils.CauldronCookbook;
import com.edoxile.bukkit.bettermechanics.Utils.MechanicsConfig;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 * Author: sk89q for the most part.
 */
public class Cauldron {
    public static Cauldron preCauldron(Block clickedBlock, MechanicsConfig c, Player p) {
        if(!c.getCauldronConfig().enabled)
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
            Block referencePoint = clickedBlock.getRelative(dx,dy,dz);
            for (int ndy = 0; ndy <= 3; ndy++) {
                for (int ndx = 0; ndx <= 1; ndx++) {
                    for (int ndz = 0; ndz <= 1; ndz++) {
                        if(referencePoint.getRelative(ndx, ndy, ndz).getType() != Material.AIR){
                            blockSet.add(referencePoint.getRelative(ndx,ndy,ndz));
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

        for (Block b : contents) {
            if (!map.containsKey(b.getTypeId())) {
                map.put(b.getTypeId(), 1);
            } else {
                map.put(b.getTypeId(), map.get(b.getTypeId()) + 1);
            }
        }

        // Find the recipe
        CauldronCookbook.Recipe recipe = recipes.find(map);

        if (recipe != null) {

            player.sendMessage(ChatColor.GOLD + "In a poof of smoke, you've made " + recipe.getName() + ".");

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
            player.sendMessage(ChatColor.RED + "Hmm, this doesn't make anything...");
            return false;
        }
    }
}
