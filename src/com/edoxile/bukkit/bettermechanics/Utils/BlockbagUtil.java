package com.edoxile.bukkit.bettermechanics.Utils;

import com.edoxile.bukkit.bettermechanics.Exceptions.OutOfMaterialException;
import com.edoxile.bukkit.bettermechanics.Exceptions.OutOfSpaceException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class BlockbagUtil {
    private static final Logger log = Logger.getLogger("Minecraft");

    public static boolean safeRemoveItems(Chest chest, ItemStack itemStack) throws OutOfMaterialException {
        boolean checkData = true;
        if (itemStack.getData() == null) {
            checkData = false;
        }
        ItemStack[] stacks = chest.getInventory().getContents();
        ItemStack tempStack;
        for (int i = 0; i < stacks.length; i++) {
            tempStack = stacks[i];
            if (tempStack == null)
                continue;
            if (checkData) {
                if (tempStack.getData() == null) {
                    continue;
                }
                if (tempStack.getType() != itemStack.getType() || tempStack.getData().getData() != itemStack.getData().getData()) {
                    continue;
                }
            } else {
                if (tempStack.getType() != itemStack.getType()) {
                    continue;
                }
            }
            if (tempStack.getAmount() > itemStack.getAmount()) {
                tempStack.setAmount(tempStack.getAmount() - itemStack.getAmount());
                itemStack.setAmount(0);
                stacks[i] = tempStack;
                break;
            } else if (tempStack.getAmount() < itemStack.getAmount()) {
                stacks[i] = null;
                itemStack.setAmount(itemStack.getAmount() - tempStack.getAmount());
                continue;
            } else {
                stacks[i] = null;
                itemStack.setAmount(0);
                break;
            }
        }
        if (itemStack.getAmount() > 0) {
            log.warning("[BetterMechanics] Not enough items in chest, no changes were made.");
            log.warning("[BetterMechanics] Chest location: " + chest.getBlock().getLocation().toString() + ".");
            throw new OutOfMaterialException(itemStack.getAmount());
        } else {
            chest.getInventory().setContents(stacks);
            return true;
        }
    }

    public static boolean safeAddItems(Chest chest, ItemStack itemStack) throws OutOfSpaceException {
        int maxStackSize = itemStack.getMaxStackSize();
        int amount = itemStack.getAmount();
        ItemStack[] stacks = chest.getInventory().getContents();
        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i] == null) {
                if (amount > maxStackSize) {
                    if (itemStack.getData() == null) {
                        stacks[i] = new ItemStack(itemStack.getType(), maxStackSize);
                    } else {
                        stacks[i] = itemStack.getData().toItemStack(maxStackSize);
                    }
                    amount -= maxStackSize;
                } else {
                    if (itemStack.getData() == null) {
                        stacks[i] = new ItemStack(itemStack.getType(), amount);
                    } else {
                        stacks[i] = itemStack.getData().toItemStack(amount);
                    }
                    amount = 0;
                }
            } else {
                if (!stacks[i].equals(itemStack)) {
                    continue;
                } else {
                    if (amount > maxStackSize) {
                        if (itemStack.getData() == null && stacks[i].getData() == null) {
                            stacks[i].setAmount(maxStackSize);
                        } else {
                            if (itemStack.getDurability() != stacks[i].getDurability()) {
                                continue;
                            }
                            stacks[i] = itemStack.getData().toItemStack(maxStackSize);
                        }
                        amount -= maxStackSize;
                    } else {
                        if (itemStack.getData() == null && stacks[i].getData() == null) {
                            stacks[i].setAmount(stacks[i].getAmount() + amount);
                        } else {
                            if (itemStack.getDurability() != stacks[i].getDurability()) {
                                continue;
                            }
                            stacks[i].setAmount(stacks[i].getAmount() + amount);
                        }
                        amount = 0;
                    }
                }
            }
        }
        if (amount > 0) {
            log.warning("[BetterMechanics] Not enough space in chest, no changes were made.");
            log.warning("[BetterMechanics] Chest location: " + chest.getBlock().getLocation().toString() + ".");
            throw new OutOfSpaceException();
        } else {
            chest.getInventory().setContents(stacks);
            return true;
        }
    }

    public static Chest getChest(Block block) {
        if (block.getType() == Material.CHEST) {
            BlockState s = block.getState();
            if (s instanceof Chest) {
                return (Chest) s;
            }
        }
        return null;
    }
}
