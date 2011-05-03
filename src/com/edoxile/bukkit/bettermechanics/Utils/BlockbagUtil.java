package com.edoxile.bukkit.bettermechanics.Utils;

import com.edoxile.bukkit.bettermechanics.Exceptions.OutOfMaterialException;
import com.edoxile.bukkit.bettermechanics.Exceptions.OutOfSpaceException;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class BlockbagUtil {
    private static Logger log = Logger.getLogger("Minecraft");

    public static boolean safeRemoveItems(Chest chest, ItemStack itemStack) throws OutOfMaterialException {
        if (itemStack.getData() != null) {
            List<ItemStack> stacks = Arrays.asList(chest.getInventory().getContents());
            ItemStack tempStack;
            for (int i = 0; i < stacks.size(); i++) {
                tempStack = stacks.get(i);
                if (tempStack == null)
                    continue;
                if (tempStack.getType() == itemStack.getType() && tempStack.getData().getData() == itemStack.getData().getData()) {
                    if (tempStack.getAmount() > itemStack.getAmount()) {
                        tempStack.setAmount(tempStack.getAmount() - itemStack.getAmount());
                        itemStack.setAmount(0);
                        stacks.set(i, tempStack);
                        break;
                    } else if (tempStack.getAmount() < itemStack.getAmount()) {
                        stacks.remove(i);
                        itemStack.setAmount(itemStack.getAmount() - tempStack.getAmount());
                        continue;
                    } else {
                        stacks.remove(i);
                        itemStack.setAmount(0);
                        break;
                    }
                }
            }
            if (itemStack.getAmount() > 0) {
                throw new OutOfMaterialException(itemStack.getAmount());
            } else {
                chest.getInventory().setContents((ItemStack[]) stacks.toArray());
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
                    log.warning("[BetterMechanics] Our chests are acting weird! This isn't supposed to happen!");
                }
            }
            throw new OutOfSpaceException();
        } else {
            return true;
        }
    }
}
