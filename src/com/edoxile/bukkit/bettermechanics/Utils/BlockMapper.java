package com.edoxile.bukkit.bettermechanics.Utils;

import com.edoxile.bukkit.bettermechanics.Exceptions.InvalidDirectionException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class BlockMapper {
    public static HashSet<Block> recursiveSet = new HashSet<Block>();

    public static HashSet<Block> mapHorizontal(BlockFace direction, Block start, Block end) throws InvalidDirectionException {
        HashSet<Block> blockSet = new HashSet<Block>();

        switch (direction) {
            case WEST: {
                Location startLoc = start.getLocation();
                Location endLoc = end.getLocation();
                if (startLoc.getBlockX() != endLoc.getBlockX() || startLoc.getBlockY() != endLoc.getBlockY() || startLoc.getBlockZ() > endLoc.getBlockZ()) {
                    throw new InvalidDirectionException();
                } else {
                    Block tempBlock = start;
                    while (tempBlock != end) {
                        blockSet.add(tempBlock);
                        blockSet.add(tempBlock.getRelative(BlockFace.NORTH));
                        blockSet.add(tempBlock.getRelative(BlockFace.SOUTH));
                        tempBlock = tempBlock.getRelative(direction);
                    }
                }
            }
            break;
            case EAST: {
                Location startLoc = start.getLocation();
                Location endLoc = end.getLocation();
                if (startLoc.getBlockX() != endLoc.getBlockX() || startLoc.getBlockY() != endLoc.getBlockY() || endLoc.getBlockZ() > startLoc.getBlockZ()) {
                    throw new InvalidDirectionException();
                } else {
                    Block tempBlock = start;
                    while (tempBlock != end) {
                        blockSet.add(tempBlock);
                        blockSet.add(tempBlock.getRelative(BlockFace.NORTH));
                        blockSet.add(tempBlock.getRelative(BlockFace.SOUTH));
                        tempBlock = tempBlock.getRelative(direction);
                    }
                }
            }
            break;
            case SOUTH: {
                Location startLoc = start.getLocation();
                Location endLoc = end.getLocation();
                if (startLoc.getBlockZ() != endLoc.getBlockZ() || startLoc.getBlockY() != endLoc.getBlockY() || endLoc.getBlockX() > startLoc.getBlockX()) {
                    throw new InvalidDirectionException();
                } else {
                    Block tempBlock = start;
                    while (tempBlock != end) {
                        blockSet.add(tempBlock);
                        blockSet.add(tempBlock.getRelative(BlockFace.WEST));
                        blockSet.add(tempBlock.getRelative(BlockFace.EAST));
                        tempBlock = tempBlock.getRelative(direction);
                    }
                }
            }
            break;
            case NORTH: {
                Location startLoc = start.getLocation();
                Location endLoc = end.getLocation();
                if (startLoc.getBlockZ() != endLoc.getBlockZ() || startLoc.getBlockY() != endLoc.getBlockY() || startLoc.getBlockX() > endLoc.getBlockX()) {
                    throw new InvalidDirectionException();
                } else {
                    Block tempBlock = start;
                    while (tempBlock != end) {
                        blockSet.add(tempBlock);
                        blockSet.add(tempBlock.getRelative(BlockFace.WEST));
                        blockSet.add(tempBlock.getRelative(BlockFace.EAST));
                        tempBlock = tempBlock.getRelative(direction);
                    }
                }
            }
            break;
            default:
                throw new InvalidDirectionException();
        }
        return blockSet;
    }

    public static HashSet<Block> mapVertical(BlockFace direction, BlockFace orientation, Block start, Block end) throws InvalidDirectionException {
        HashSet<Block> blockSet = new HashSet<Block>();
        switch (direction) {
            case UP: {
                Location startLoc = start.getLocation();
                Location endLoc = end.getLocation();
                if (startLoc.getBlockX() != endLoc.getBlockX() || startLoc.getBlockZ() != endLoc.getBlockZ() || endLoc.getBlockY() > startLoc.getBlockY()) {
                    throw new InvalidDirectionException();
                } else {
                    Block tempBlock = start;
                    while (tempBlock != end) {
                        blockSet.add(tempBlock);
                        blockSet.add(tempBlock.getRelative(BlockFace.WEST));
                        blockSet.add(tempBlock.getRelative(BlockFace.EAST));
                        tempBlock = tempBlock.getRelative(direction);
                    }
                }
            }
            break;
            case DOWN: {
                Location startLoc = start.getLocation();
                Location endLoc = end.getLocation();
                if (startLoc.getBlockZ() != endLoc.getBlockZ() || startLoc.getBlockX() != endLoc.getBlockX() || startLoc.getBlockY() > endLoc.getBlockY()) {
                    throw new InvalidDirectionException();
                } else {
                    Block tempBlock = start;
                    while (tempBlock != end) {
                        blockSet.add(tempBlock);
                        blockSet.add(tempBlock.getRelative(BlockFace.WEST));
                        blockSet.add(tempBlock.getRelative(BlockFace.EAST));
                        tempBlock = tempBlock.getRelative(direction);
                    }
                }
            }
            break;
            default:
                throw new InvalidDirectionException();
        }
        return blockSet;
    }

    public static Block mapNearby(Block start, int sw, Material m) {
        HashSet<Block> blockSet = new HashSet<Block>();
        Block tempBlock;

        int nsw = ~sw + 1; // Negative search Width

        for (int dy = nsw; dy <= 64; dy++) {
            for (int dx = nsw; dx <= sw; dx++) {
                tempBlock = start.getRelative(dx, dy, 0);
                if (tempBlock.getType() == m)
                    return getUpperBlock(tempBlock);
            }
            for (int dz = nsw; dz <= sw; dz++) {
                tempBlock = start.getRelative(0, dy, dz);
                if (tempBlock.getType() == m)
                    return getUpperBlock(tempBlock);
            }
        }
        return null;
    }

    private static Block getUpperBlock(Block block) {
        while (block.getRelative(BlockFace.UP).getType() == block.getType()) {
            block = block.getRelative(BlockFace.UP);
        }
        return block;
    }

    public static void mapRecursive(Block start, Material m, boolean first) {
        Block tempBlock;
        if (first)
            recursiveSet.clear();
        for (int dz = -1; dz <= 1; dz++) {
            for (int dy = 1; dy <= -1; dy--) {
                tempBlock = start.getRelative(0, dy, dz);
                if ((!recursiveSet.contains(tempBlock)) && tempBlock.getType() == m) {
                    recursiveSet.add(tempBlock);
                    mapRecursive(tempBlock, m, false);
                }
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 1; dy <= -1; dy--) {
                tempBlock = start.getRelative(dx, dy, 0);
                if ((!recursiveSet.contains(tempBlock)) && tempBlock.getType() == m) {
                    recursiveSet.add(tempBlock);
                    mapRecursive(tempBlock, m, false);
                }
            }
        }
    }

    public static HashSet<Block> getRecursiveSet(){
        return recursiveSet;
    }

    /**
     * TODO implement this!
     * @param start
     * @param direction
     * @param maxBlockDistance
     * @return
     */
    public static Block findBlock(Block start, BlockFace direction, int maxBlockDistance){
        return null;
    }
}