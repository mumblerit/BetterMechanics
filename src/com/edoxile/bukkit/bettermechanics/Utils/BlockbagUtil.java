package com.edoxile.bukkit.bettermechanics.Utils;

import com.edoxile.bukkit.bettermechanics.Exceptions.OutOfMaterialException;
import com.edoxile.bukkit.bettermechanics.Exceptions.OutOfSpaceException;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 * TODO: implement this!
 */
public class BlockbagUtil {
    public static boolean safeRemoveItems(Chest chest, ItemStack itemStack) throws OutOfMaterialException {

        return true;
    }

    public static boolean safeAddItems(Chest chest, ItemStack itemStack) throws OutOfSpaceException {

        return true;
    }
}
