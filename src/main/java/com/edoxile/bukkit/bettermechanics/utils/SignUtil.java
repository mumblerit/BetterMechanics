package com.edoxile.bukkit.bettermechanics.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.exceptions.NonCardinalDirectionException;

/**
 * Created by IntelliJ IDEA. User: Edoxile
 */
public class SignUtil {
	public static boolean isSign(Block b) {
		return (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN);
	}

	public static Sign getSign(Block b) {
		if (!isSign(b))
			return null;
		BlockState s = b.getState();
		if (s instanceof Sign)
			return (Sign) s;
		else
			return null;
	}

	public static MechanicsType getMechanicsType(String str) {
		if (str.equalsIgnoreCase("[Lift Up]")
				|| str.equalsIgnoreCase("[Lift Down]")
				|| str.equalsIgnoreCase("[Lift]"))
			return MechanicsType.LIFT;

		else if (str.equalsIgnoreCase("[Gate]"))
			return MechanicsType.GATE;
		else if (str.equalsIgnoreCase("[Dgate]"))
			return MechanicsType.SMALL_GATE;
		else if (str.equalsIgnoreCase("[Bridge]")
				|| str.equalsIgnoreCase("[Bridge End]"))
			return MechanicsType.BRIDGE;
		else if (str.equalsIgnoreCase("[sBridge]")
				|| str.equalsIgnoreCase("[sBridge End]"))
			return MechanicsType.SMALL_BRIDGE;
		else if (str.equalsIgnoreCase("[Door]")
				|| str.equalsIgnoreCase("[Door Down]")
				|| str.equalsIgnoreCase("[Door Up]"))
			return MechanicsType.DOOR;
		else if (str.equalsIgnoreCase("[sDoor]")
				|| str.equalsIgnoreCase("[sDoor Down]")
				|| str.equalsIgnoreCase("[sDoor Up]"))
			return MechanicsType.SMALL_DOOR;
		else if (str.equalsIgnoreCase("[X]"))
			return MechanicsType.HIDDEN_SWITCH;
		else
			return null;
	}

	public static MechanicsType getMechanicsType(Sign s) {
		String str = s.getLine(1);
		return getMechanicsType(str);
	}

	public static MechanicsType getActiveMechanicsType(Sign s) {
		String str = s.getLine(1);
		if (s.equals("") || s == null)
			return null;
		else
			return getActiveMechanicsType(str);
	}

	public static MechanicsType getActiveMechanicsType(String str) {
		if (str.equalsIgnoreCase("[Lift Up]")
				|| str.equalsIgnoreCase("[Lift Down]"))
			return MechanicsType.LIFT;
		else if (str.equalsIgnoreCase("[Gate]"))
			return MechanicsType.GATE;
		else if (str.equalsIgnoreCase("[Dgate]"))
			return MechanicsType.SMALL_GATE;
		else if (str.equalsIgnoreCase("[Bridge]"))
			return MechanicsType.BRIDGE;
		else if (str.equalsIgnoreCase("[sBridge]"))
			return MechanicsType.SMALL_BRIDGE;
		else if (str.equalsIgnoreCase("[Door Down]")
				|| str.equalsIgnoreCase("[Door Up]"))
			return MechanicsType.DOOR;
		else if (str.equalsIgnoreCase("[sDoor Down]")
				|| str.equalsIgnoreCase("[sDoor Up]"))
			return MechanicsType.SMALL_DOOR;
		else if (str.equalsIgnoreCase("[sDoor Down]")
				|| str.equalsIgnoreCase("[sDoor Up]"))
			return MechanicsType.DOOR;
		else if (str.equalsIgnoreCase("[X]"))
			return MechanicsType.HIDDEN_SWITCH;
		else
			return null;
	}

	public static BlockFace getBackBlockFace(Sign s)
			throws NonCardinalDirectionException {
		if (s.getType() == Material.SIGN_POST) {
			switch (s.getData().getData()) {
			case 0xC:
				return BlockFace.NORTH;
			case 0x0:
				return BlockFace.EAST;
			case 0x4:
				return BlockFace.SOUTH;
			case 0x8:
				return BlockFace.WEST;
			default:
				throw new NonCardinalDirectionException();
			}
		} else if (s.getType() == Material.WALL_SIGN) {
			switch (s.getData().getData()) {
			case 0x5:
				return BlockFace.NORTH;
			case 0x3:
				return BlockFace.EAST;
			case 0x4:
				return BlockFace.SOUTH;
			case 0x2:
				return BlockFace.WEST;
			default:
				throw new NonCardinalDirectionException();
			}
		} else {
			// This should never happen...
			return null;
		}
	}

	public static BlockFace getBlockFace(Sign s)
			throws NonCardinalDirectionException {
		if (s.getType() == Material.SIGN_POST) {
			switch (s.getData().getData()) {
			case 0x4:
				return BlockFace.NORTH;
			case 0x8:
				return BlockFace.EAST;
			case 0xC:
				return BlockFace.SOUTH;
			case 0x0:
				return BlockFace.WEST;
			default:
				throw new NonCardinalDirectionException();
			}
		} else if (s.getType() == Material.WALL_SIGN) {
			switch (s.getData().getData()) {
			case 0x4:
				return BlockFace.NORTH;
			case 0x2:
				return BlockFace.EAST;
			case 0x5:
				return BlockFace.SOUTH;
			case 0x3:
				return BlockFace.WEST;
			default:
				throw new NonCardinalDirectionException();
			}
		} else {
			// This should never happen...
			return null;
		}
	}
}
