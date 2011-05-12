package com.edoxile.bukkit.bettermechanics.Mechanics;

import com.edoxile.bukkit.bettermechanics.Exceptions.*;
import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.Utils.BlockMapper;
import com.edoxile.bukkit.bettermechanics.Utils.BlockbagUtil;
import com.edoxile.bukkit.bettermechanics.Utils.MechanicsConfig;
import com.edoxile.bukkit.bettermechanics.Utils.SignUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.Set;
import java.util.logging.Logger;

/**
* Created by IntelliJ IDEA.
* User: Edoxile
*/
public class Door {
    private static final Logger log = Logger.getLogger("Minecraft");
    private Sign sign;
    private Player player;
    private Chest chest;
    private MechanicsConfig.DoorConfig config;
    private Set<Block> blockSet;
    private MaterialData doorMaterial;

    public Door(MechanicsConfig c, Sign s, Player p) {
        sign = s;
        player = p;
        config = c.getDoorConfig();
    }

    public boolean map() throws InvalidMaterialException, BlockNotFoundException, NonCardinalDirectionException, ChestNotFoundException {
        if(!config.enabled)
            return false;
        BlockFace direction;
        BlockFace orientation = SignUtil.getBlockFace(sign);
        if (sign.getLine(1).equalsIgnoreCase("[Door Down]")) {
            direction = BlockFace.DOWN;
        } else {
            direction = BlockFace.UP;
        }
        if (config.canUseBlock(sign.getBlock().getRelative(direction).getType())) {
            doorMaterial = new MaterialData(sign.getBlock().getRelative(direction).getType(), sign.getBlock().getRelative(direction).getData());
        } else {
            throw new InvalidMaterialException();
        }
        Sign endSign = BlockMapper.findMechanicsSign(sign.getBlock(), direction, MechanicsType.DOOR, config.maxHeight);
        Block startBlock = sign.getBlock().getRelative(direction).getRelative(direction);
        Block endBlock = null;
        switch (direction) {
            case UP:
                endBlock = endSign.getBlock().getRelative(BlockFace.DOWN);
                break;
            case DOWN:
                endBlock = endSign.getBlock().getRelative(BlockFace.UP);
                break;
        }
        try {
            blockSet = BlockMapper.mapVertical(direction, orientation, startBlock, endBlock);
            if (!blockSet.isEmpty()) {
                Block chestBlock = BlockMapper.mapCuboidRegion(sign.getBlock(), 3, Material.CHEST);
                if (chestBlock == null) {
                    //Check other sign
                    chestBlock = BlockMapper.mapCuboidRegion(endSign.getBlock(), 3, Material.CHEST);
                    if (chestBlock == null) {
                        throw new ChestNotFoundException();
                    }
                }
                chest = BlockbagUtil.getChest(chestBlock);
                if (chest == null) {
                    throw new ChestNotFoundException();
                }
                return true;
            } else {
                log.info("[BetterMechanics] Empty blockSet?");
                return false;
            }
        } catch (InvalidDirectionException e) {
            log.info("[BetterMechanics] Our mapper is acting weird!");
            return false;
        }
    }

    public void toggleOpen() {
        int amount = 0;
        try {
            for (Block b : blockSet) {
                if (b.getType() == doorMaterial.getItemType()) {
                    b.setType(Material.AIR);
                    amount++;
                }
            }
            BlockbagUtil.safeAddItems(chest, doorMaterial.toItemStack(amount));
            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "Bridge opened!");
            }
        } catch (OutOfSpaceException ex) {
            for (Block b : blockSet) {
                if (b.getType() == Material.AIR) {
                    b.setType(doorMaterial.getItemType());
                    b.setData(doorMaterial.getData());
                    amount--;
                    if (amount == 0) {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "Not enough space in chest!");
                        }
                        return;
                    }
                }
            }
        }
    }

    public void toggleClosed() {
        int amount = 0;
        try {
            for (Block b : blockSet) {
                if (canPassThrough(b.getType())) {
                    b.setType(doorMaterial.getItemType());
                    b.setData(doorMaterial.getData());
                    amount++;
                }
            }
            BlockbagUtil.safeRemoveItems(chest, doorMaterial.toItemStack(amount));
            if (player != null) {
                player.sendMessage(ChatColor.GOLD + "Bridge closed!");
            }
        } catch (OutOfMaterialException ex) {
            for (Block b : blockSet) {
                if (b.getType() == doorMaterial.getItemType()) {
                    b.setType(Material.AIR);
                    amount--;
                    if (amount == 0) {
                        if (player != null) {
                            player.sendMessage(ChatColor.RED + "Not enough items in chest! Still need: " + Integer.toString(ex.getAmount()) + " of type: " + doorMaterial.getItemType().name());
                        }
                        return;
                    }
                }
            }
        }
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

    public boolean isClosed() {
        for (Block b : blockSet) {
            if(b.getType() == doorMaterial.getItemType() || canPassThrough(b.getType())){
                return (!canPassThrough(b.getType()));
            }
        }
        return false;
    }
}