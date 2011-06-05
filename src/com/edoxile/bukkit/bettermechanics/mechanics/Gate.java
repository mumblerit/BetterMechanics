package com.edoxile.bukkit.bettermechanics.mechanics;

import com.edoxile.bukkit.bettermechanics.exceptions.*;
import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class Gate {
    private static final Logger log = Logger.getLogger("Minecraft");
    private Sign sign;
    private Player player;
    private MechanicsConfig.GateConfig config;
    private boolean smallGate;
    private Set<Block> blockSet;
    private Chest chest;

    public Gate(MechanicsConfig c, Sign s, Player p) {
        sign = s;
        player = p;
        config = c.getGateConfig();
    }

    public boolean map() throws NonCardinalDirectionException, ChestNotFoundException, OutOfBoundsException, BlockNotFoundException {
        if(!config.enabled)
            return false;
        Block chestBlock = BlockMapper.mapCuboidRegion(sign.getBlock(), 3, Material.CHEST);
        if (chestBlock == null) {
            throw new ChestNotFoundException();
        } else {
            chest = BlockbagUtil.getChest(chestBlock);
            if (chest == null) {
                throw new ChestNotFoundException();
            }
        }
        smallGate = (SignUtil.getMechanicsType(sign) == MechanicsType.SMALL_GATE);
        int sw = (smallGate ? 1 : (3 + 1));
        Block tempBlock = sign.getBlock().getRelative(SignUtil.getBackBlockFace(sign));
        tempBlock = BlockMapper.mapColumn(tempBlock, sw, 32, Material.FENCE);
        if (tempBlock == null) {
            throw new BlockNotFoundException();
        }
        blockSet = BlockMapper.mapFlatRegion(tempBlock, Material.FENCE, config.maxWidth, config.maxLength);
        if (blockSet.isEmpty()) {
            log.info("blockset empty?");
            return false;
        } else {
            return true;
        }
    }

    public void toggleOpen() {
        int amount = 0;
        Block tempBlock;
        try {
            for (Block b : blockSet) {
                tempBlock = b.getRelative(BlockFace.DOWN);
                while (tempBlock.getType() == Material.FENCE) {
                    tempBlock.setType(Material.AIR);
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    amount++;
                }
            }
            BlockbagUtil.safeAddItems(chest, new ItemStack(Material.FENCE, amount));
            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "Gate opened!");
            }
        } catch (OutOfSpaceException ex) {
            for (Block b : blockSet) {
                tempBlock = b.getRelative(BlockFace.DOWN);
                while (tempBlock.getType() == Material.AIR && amount > 0) {
                    tempBlock.setType(Material.FENCE);
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    amount--;
                }
                if(amount == 0)
                    break;
            }
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Not enough space in chest!");
            }
        }
    }

    public void toggleClosed() {
        int amount = 0;
        Block tempBlock;
        try {
            for (Block b : blockSet) {
                tempBlock = b.getRelative(BlockFace.DOWN);
                while (canPassThrough(tempBlock.getType())) {
                    tempBlock.setType(Material.FENCE);
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    amount++;
                }
            }
            BlockbagUtil.safeRemoveItems(chest, new ItemStack(Material.FENCE, amount));
            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "Gate closed!");
            }
        } catch (OutOfMaterialException ex) {
            for (Block b : blockSet) {
                tempBlock = b.getRelative(BlockFace.DOWN);
                while (tempBlock.getType() == Material.FENCE && amount > 0) {
                    tempBlock.setType(Material.AIR);
                    tempBlock = tempBlock.getRelative(BlockFace.DOWN);
                    amount--;
                }
                if(amount == 0)
                    break;
            }
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Not enough items in chest! Still need: " + Integer.toString(ex.getAmount()) + " of type: fence");
            }
        }
    }

    public boolean isClosed() {
        for (Block b : blockSet) {
            return b.getRelative(BlockFace.DOWN).getType() == Material.FENCE;
        }
        return false;
    }

    private boolean canPassThrough(Material m) {
        switch (m) {
            case AIR:
            case WATER:
            case STATIONARY_WATER:
            case LAVA:
            case STATIONARY_LAVA:
            case SNOW:
                return true;
            default:
                return false;
        }
    }
}
