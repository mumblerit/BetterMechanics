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
    public static boolean safeRemoveItems(Chest chest, ItemStack itemStack) throws OutOfMaterialException {
        boolean checkData = true;
        if (itemStack.getData() != null) {
            checkData = false;
        }
        ItemStack[] stacks = chest.getInventory().getContents();
        ItemStack tempStack;
        for (int i = 0; i < stacks.length; i++) {
            tempStack = stacks[i];
            if (tempStack == null)
                continue;
            if (checkData) {
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
            throw new OutOfMaterialException(itemStack.getAmount());
        } else {
            chest.getInventory().setContents(stacks);
            return true;
        }
    }

    public static boolean safeAddItems(Chest chest, ItemStack itemStack) throws OutOfSpaceException {
        int maxStackSize = itemStack.getMaxStackSize();
        ItemStack[] stacks = chest.getInventory().getContents();
        ItemStack tempStack;
        for (int i = 0; i < stacks.length; i++) {
            tempStack = stacks[i];
            if (tempStack != null)
                continue;
            if (itemStack.getAmount() > maxStackSize) {
                stacks[i] = itemStack.getData().toItemStack(maxStackSize);
                itemStack.setAmount(itemStack.getAmount() - maxStackSize);
                continue;
            } else {
                stacks[i] = itemStack;
                itemStack.setAmount(0);
                break;
            }
        }
        if (itemStack.getAmount() > 0) {
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
