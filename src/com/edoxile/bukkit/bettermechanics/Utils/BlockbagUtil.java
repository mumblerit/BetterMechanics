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
    private static Logger log = Logger.getLogger("Minecraft");

    public static boolean safeRemoveItems(Chest chest, ItemStack itemStack) throws OutOfMaterialException {
        if (itemStack.getData() != null) {
            ItemStack[] stacks = chest.getInventory().getContents();
            ItemStack tempStack;
            for (int i = 0; i < stacks.length; i++) {
                tempStack = stacks[i];
                if (tempStack == null)
                    continue;
                if (tempStack.getType() == itemStack.getType() && tempStack.getData().getData() == itemStack.getData().getData()) {
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
            }
            if (itemStack.getAmount() > 0) {
                throw new OutOfMaterialException(itemStack.getAmount());
            } else {
                chest.getInventory().setContents(stacks);
                return true;
            }
        } else {
            int a = itemStack.getAmount();
            HashMap<Integer, ItemStack> hashMap = chest.getInventory().removeItem(itemStack);
            if (!hashMap.isEmpty()) {
                int amount = 0;
                for (ItemStack i : hashMap.values()) {
                    amount += i.getAmount();
                }
                if ((a - amount) > 0) {
                    itemStack.setAmount(a - amount);
                    hashMap = chest.getInventory().addItem(itemStack);
                }
                throw new OutOfMaterialException(amount);
            } else {
                return true;
            }
        }
    }

    public static boolean safeAddItems(Chest chest, ItemStack itemStack) throws OutOfSpaceException {
        HashMap<Integer, ItemStack> hashMap = chest.getInventory().addItem(itemStack);
        if (!hashMap.isEmpty()) {
            for (ItemStack i : hashMap.values()) {
                try {
                    i.setAmount(itemStack.getAmount() - i.getAmount());
                    safeRemoveItems(chest, i);
                } catch (OutOfMaterialException ex) {
                }
            }
            throw new OutOfSpaceException();
        } else {
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
