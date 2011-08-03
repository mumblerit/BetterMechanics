package com.edoxile.bukkit.bettermechanics.utils;

import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.exceptions.BlockNotFoundException;
import com.edoxile.bukkit.bettermechanics.exceptions.InvalidDirectionException;
import com.edoxile.bukkit.bettermechanics.exceptions.OutOfBoundsException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class BlockMapper {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static HashSet<Block> recursiveSet = new HashSet<Block>();

    public static HashSet<Block> mapHorizontal(BlockFace direction, Block start, Block end, boolean small) throws InvalidDirectionException {
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
                        if (!small) {
                            blockSet.add(tempBlock.getRelative(BlockFace.NORTH));
                            blockSet.add(tempBlock.getRelative(BlockFace.SOUTH));
                        }
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
                        if (!small) {
                            blockSet.add(tempBlock.getRelative(BlockFace.NORTH));
                            blockSet.add(tempBlock.getRelative(BlockFace.SOUTH));
                        }
                        tempBlock = tempBlock.getRelative(direction);
                    }
                }
            }
            break;
            case SOUTH: {
                Location startLoc = start.getLocation();
                Location endLoc = end.getLocation();
                if (startLoc.getBlockZ() != endLoc.getBlockZ() || startLoc.getBlockY() != endLoc.getBlockY() || startLoc.getBlockX() > endLoc.getBlockX()) {
                    throw new InvalidDirectionException();
                } else {
                    Block tempBlock = start;
                    while (tempBlock != end) {
                        blockSet.add(tempBlock);
                        if (!small) {
                            blockSet.add(tempBlock.getRelative(BlockFace.WEST));
                            blockSet.add(tempBlock.getRelative(BlockFace.EAST));
                        }
                        tempBlock = tempBlock.getRelative(direction);
                    }
                }
            }
            break;
            case NORTH: {
                Location startLoc = start.getLocation();
                Location endLoc = end.getLocation();
                if (startLoc.getBlockZ() != endLoc.getBlockZ() || startLoc.getBlockY() != endLoc.getBlockY() || endLoc.getBlockX() > startLoc.getBlockX()) {
                    throw new InvalidDirectionException();
                } else {
                    Block tempBlock = start;
                    while (tempBlock != end) {
                        blockSet.add(tempBlock);
                        if (!small) {
                            blockSet.add(tempBlock.getRelative(BlockFace.WEST));
                            blockSet.add(tempBlock.getRelative(BlockFace.EAST));
                        }
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

    public static HashSet<Block> mapVertical(BlockFace direction, BlockFace orientation, Block start, Block end, boolean small) throws InvalidDirectionException {
        HashSet<Block> blockSet = new HashSet<Block>();
        switch (direction) {
            case UP: {
                Location startLoc = start.getLocation();
                Location endLoc = end.getLocation();
                if (startLoc.getBlockX() != endLoc.getBlockX() || startLoc.getBlockZ() != endLoc.getBlockZ()) {
                    throw new InvalidDirectionException();
                } else {
                    Block tempBlock = start;
                    while (tempBlock != end) {
                        blockSet.add(tempBlock);
                        if (!small) {
                            switch (orientation) {
                                case NORTH:
                                case SOUTH: {
                                    blockSet.add(tempBlock.getRelative(BlockFace.WEST));
                                    blockSet.add(tempBlock.getRelative(BlockFace.EAST));
                                }
                                break;
                                case EAST:
                                case WEST: {
                                    blockSet.add(tempBlock.getRelative(BlockFace.NORTH));
                                    blockSet.add(tempBlock.getRelative(BlockFace.SOUTH));
                                }
                                break;
                            }
                        }
                        tempBlock = tempBlock.getRelative(direction);
                    }
                }
            }
            break;
            case DOWN: {
                Location startLoc = start.getLocation();
                Location endLoc = end.getLocation();
                if (startLoc.getBlockZ() != endLoc.getBlockZ() || startLoc.getBlockX() != endLoc.getBlockX()) {
                    throw new InvalidDirectionException();
                } else {
                    Block tempBlock = start;
                    while (tempBlock != end) {
                        blockSet.add(tempBlock);
                        switch (orientation) {
                            case NORTH:
                            case SOUTH: {
                                blockSet.add(tempBlock.getRelative(BlockFace.WEST));
                                blockSet.add(tempBlock.getRelative(BlockFace.EAST));
                            }
                            break;
                            case EAST:
                            case WEST: {
                                blockSet.add(tempBlock.getRelative(BlockFace.NORTH));
                                blockSet.add(tempBlock.getRelative(BlockFace.SOUTH));
                            }
                            break;
                        }
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

    public static Block mapColumn(Block start, int sw, int h, Material m) {
        Block tempBlock;

        int nsw = ~sw + 1; // Negative search Width
        int nh = ~h + 1;
        for (int dy = nh; dy <= h; dy++) {
            for (int dx = nsw; dx <= sw; dx++) {
                tempBlock = start.getRelative(dx, dy, 0);
                if (tempBlock.getType() == m) {
                    return getUpperBlock(tempBlock);
                }
            }
            for (int dz = nsw; dz <= sw; dz++) {
                tempBlock = start.getRelative(0, dy, dz);
                if (tempBlock.getType() == m) {
                    return getUpperBlock(tempBlock);
                }
            }
        }
        return null;
    }

    public static Block mapCuboidRegion(Block start, int sw, Material m) {
        for (int dy = 0; dy <= sw; dy++) {
            for (int dx = 0; dx <= sw; dx++) {
                for (int dz = 0; dz <= sw; dz++) {
                    HashSet<Block> blockSet = new HashSet<Block>();
                    blockSet.add(start.getRelative(dx, dy, dz));
                    blockSet.add(start.getRelative(-dx, dy, dz));
                    blockSet.add(start.getRelative(dx, dy, -dz));
                    blockSet.add(start.getRelative(-dx, dy, -dz));
                    blockSet.add(start.getRelative(dx, -dy, dz));
                    blockSet.add(start.getRelative(-dx, -dy, dz));
                    blockSet.add(start.getRelative(dx, -dy, -dz));
                    blockSet.add(start.getRelative(-dx, -dy, -dz));
                    for (Block b : blockSet) {
                        if (b.getType() == m) {
                            return b;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static HashSet<Block> mapAllInCuboidRegion(Block start, int sw, Material m) {
        Block tempBlock;
        HashSet<Block> blockSet = new HashSet<Block>();
        int nsw = ~sw + 1;
        for (int dx = nsw; dx <= sw; dx++) {
            for (int dy = nsw; dy <= sw; dy++) {
                for (int dz = nsw; dz <= sw; dz++) {
                    tempBlock = start.getRelative(dx, dy, dz);
                    if (tempBlock.getType() == m) {
                        blockSet.add(tempBlock);
                    }
                }
            }
        }
        return blockSet;
    }

    private static Block getUpperBlock(Block block) {
        while (block.getRelative(BlockFace.UP).getType() == block.getType()) {
            block = block.getRelative(BlockFace.UP);
        }
        return block;
    }

    public static HashSet<Block> mapFlatRegion(Block start, Material m, int w, int l) throws OutOfBoundsException {
        Block tempBlock = start;
        int west = 0, east = 0, south = 0, north = 0, width, length;
        while (checkInColumn(tempBlock.getRelative(BlockFace.WEST), m, 1) != null) {
            tempBlock = tempBlock.getRelative(BlockFace.WEST);
            west++;
        }
        tempBlock = start;
        while (checkInColumn(tempBlock.getRelative(BlockFace.EAST), m, 1) != null) {
            tempBlock = tempBlock.getRelative(BlockFace.EAST);
            east++;
        }
        tempBlock = start;
        while (checkInColumn(tempBlock.getRelative(BlockFace.NORTH), m, 1) != null) {
            tempBlock = tempBlock.getRelative(BlockFace.NORTH);
            north++;
        }
        tempBlock = start;
        while (checkInColumn(tempBlock.getRelative(BlockFace.SOUTH), m, 1) != null) {
            tempBlock = tempBlock.getRelative(BlockFace.SOUTH);
            south++;
        }
        if ((north + south) > (east + west)) {
            width = (east + west);
            length = (north + south);
        } else {
            length = (east + west);
            width = (north + south);
        }
        if (width > w || length > l) {
            throw new OutOfBoundsException();
        }
        start = start.getRelative((~north + 1), 0, (~east + 1));
        HashSet<Block> blockSet = new HashSet<Block>();
        for (int dx = 0; dx <= (north + south); dx++) {
            for (int dz = 0; dz <= (east + west); dz++) {
                tempBlock = checkInColumn(start.getRelative(dx, 0, dz), m, 1);
                if (tempBlock != null) {
                    blockSet.add(getUpperBlock(tempBlock));
                }
            }
        }
        return blockSet;
    }

    private static Block checkInColumn(Block start, Material m, int h) {
        int nh = ~h + 1;
        for (int dy = nh; dy <= h; dy++) {
            if (start.getRelative(0, dy, 0).getType() == m) {
                return start.getRelative(0, dy, 0);
            }
        }
        return null;
    }

    public static Sign findMechanicsSign(Block block, BlockFace direction, MechanicsType type, int maxBlockDistance) throws BlockNotFoundException {
        for (int d = 0; d < maxBlockDistance; d++) {
            block = block.getRelative(direction);
            if (SignUtil.isSign(block)) {
                Sign s = SignUtil.getSign(block);
                if (s != null) {
                    if (SignUtil.getMechanicsType(s) == type) {
                        return s;
                    }
                }
            }
        }
        throw new BlockNotFoundException();
    }
}