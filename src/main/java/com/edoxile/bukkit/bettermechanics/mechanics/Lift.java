package com.edoxile.bukkit.bettermechanics.mechanics;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.exceptions.BlockNotFoundException;
import com.edoxile.bukkit.bettermechanics.utils.BlockMapper;
import com.edoxile.bukkit.bettermechanics.utils.MechanicsConfig;

/**
 * Created by IntelliJ IDEA. User: Edoxile
 */
public class Lift {
	private static final Logger log = Logger.getLogger("Minecraft");
	private Sign sign;
	private Player player;
	private MechanicsConfig.LiftConfig config;
	private static List<Integer> nonSolidBlockList = Arrays.asList(0, 6, 8, 9,
			10, 11, 27, 28, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66,
			68, 69, 70, 72, 75, 76, 78, 83, 90, 93, 94);
	private Location destination;
	private String floorName;
	private BlockFace direction;

	public Lift(MechanicsConfig c, Sign s, Player p) {
		sign = s;
		player = p;
		config = c.getLiftConfig();
	}

	public boolean map() throws BlockNotFoundException {
		if (!config.enabled)
			return false;
		Sign endSign;
		if (sign.getLine(1).equalsIgnoreCase("[Lift Down]")) {
			direction = BlockFace.DOWN;
		} else {
			direction = BlockFace.UP;
		}
		endSign = BlockMapper.findMechanicsSign(sign.getBlock(), direction,
				MechanicsType.LIFT, config.maxSearchHeight);
		int x = player.getLocation().getBlockX();
		int z = player.getLocation().getBlockZ();
		int y = endSign.getBlock().getLocation().getBlockY();
		Block dest1 = sign.getWorld().getBlockAt(x, y, z);
		Block dest2 = dest1.getRelative(BlockFace.UP);
		Block dest3 = dest1.getRelative(BlockFace.DOWN);
		floorName = endSign.getLine(0);
		if (canPassThrough(dest1.getType()) && canPassThrough(dest2.getType())) {
			if (canPassThrough(dest3.getType())
					&& canPassThrough(dest3.getRelative(BlockFace.DOWN)
							.getType())) {
				player.sendMessage(ChatColor.RED
						+ "You have no place to stand on!");
				return false;
			}
			destination = player.getLocation();
			destination.setY(dest1.getY());
			return true;
		} else if (canPassThrough(dest1.getType())
				&& canPassThrough(dest3.getType())) {
			if (canPassThrough(dest3.getRelative(BlockFace.DOWN).getType())
					&& canPassThrough(dest3.getRelative(BlockFace.DOWN)
							.getRelative(BlockFace.DOWN).getType())) {
				player.sendMessage(ChatColor.RED
						+ "You have no place to stand on!");
				return false;
			}
			destination = player.getLocation();
			destination.setY(dest3.getY());
			return true;
		} else {
			player.sendMessage(ChatColor.RED + "You would be obscured!");
			return false;
		}
	}

	public boolean movePlayer() {
		if (player.teleport(destination)) {
			if (floorName.equals("")) {
				player.sendMessage(ChatColor.GOLD + "You went "
						+ direction.name().toLowerCase() + " a floor!");
			} else {
				player.sendMessage(ChatColor.GOLD + "You went to floor: "
						+ floorName);
			}
			return true;
		} else {
			player.sendMessage(ChatColor.RED
					+ "Something went wrong teleporting you!");
			log.warning("Couldn't teleport player '" + player.getName()
					+ "' via lift.");
			return false;
		}
	}

	private boolean canPassThrough(Material m) {
		return nonSolidBlockList.contains(m.getId());
	}
}
