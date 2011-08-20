package com.edoxile.bukkit.bettermechanics.mechanics;

import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.exceptions.*;
import com.edoxile.bukkit.bettermechanics.utils.BlockMapper;
import com.edoxile.bukkit.bettermechanics.utils.BlockbagUtil;
import com.edoxile.bukkit.bettermechanics.utils.MechanicsConfig;
import com.edoxile.bukkit.bettermechanics.utils.SignUtil;
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
 * Created by IntelliJ IDEA. User: Edoxile
 */
public class Bridge {
	private Logger log = Logger.getLogger("Minecraft");
	private Sign sign;
	private Player player;
	private Chest chest;
	private MechanicsConfig.BridgeConfig config;
	private Set<Block> blockSet;
	private MaterialData bridgeMaterial;

	public Bridge(MechanicsConfig c, Sign s, Player p) {
		sign = s;
		player = p;
		config = c.getBridgeConfig();
	}

	public boolean map() throws NonCardinalDirectionException,
			BlockNotFoundException, InvalidMaterialException,
			ChestNotFoundException {
		if (!config.enabled)
			return false;
		BlockFace bf;
		if (config.canUseBlock(sign.getBlock().getRelative(BlockFace.UP)
				.getType())) {
			bf = BlockFace.UP;
			bridgeMaterial = new MaterialData(sign.getBlock()
					.getRelative(BlockFace.UP).getType(), sign.getBlock()
					.getRelative(BlockFace.UP).getData());
		} else if (config.canUseBlock(sign.getBlock()
				.getRelative(BlockFace.DOWN).getType())) {
			bf = BlockFace.DOWN;
			bridgeMaterial = new MaterialData(sign.getBlock()
					.getRelative(BlockFace.DOWN).getType(), sign.getBlock()
					.getRelative(BlockFace.DOWN).getData());
		} else {
			throw new InvalidMaterialException();
		}

		MechanicsType bridgeType = SignUtil.getMechanicsType(sign);

		Sign endSign = BlockMapper.findMechanicsSign(sign.getBlock(),
				SignUtil.getBackBlockFace(sign), bridgeType, config.maxLength);
		Block startBlock = sign.getBlock()
				.getRelative(SignUtil.getBackBlockFace(sign)).getRelative(bf);
		Block endBlock = endSign.getBlock().getRelative(bf);
		try {
			blockSet = BlockMapper.mapHorizontal(
					SignUtil.getBackBlockFace(sign), startBlock, endBlock,
					bridgeType == MechanicsType.SMALL_BRIDGE);
			if (!blockSet.isEmpty()) {
				Block chestBlock = BlockMapper.mapCuboidRegion(sign.getBlock(),
						3, Material.CHEST);
				if (chestBlock == null) {
					// Check other sign
					chestBlock = BlockMapper.mapCuboidRegion(
							endSign.getBlock(), 3, Material.CHEST);
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
		} catch (InvalidDirectionException ex) {
			log.info("[BetterMechanics] Our mapper is acting weird!");
			return false;
		}
	}

	public void toggleOpen() {
		int amount = 0;
		try {
			for (Block b : blockSet) {
				if (b.getType() == bridgeMaterial.getItemType()) {
					b.setType(Material.AIR);
					amount++;
				}
			}
			BlockbagUtil
					.safeAddItems(chest, bridgeMaterial.toItemStack(amount));
			if (player != null) {
				player.sendMessage(ChatColor.GOLD + "Bridge opened!");
			}
		} catch (OutOfSpaceException ex) {
			for (Block b : blockSet) {
				if (b.getType() == Material.AIR) {
					b.setType(bridgeMaterial.getItemType());
					b.setData(bridgeMaterial.getData());
					amount--;
					if (amount == 0) {
						if (player != null) {
							player.sendMessage(ChatColor.RED
									+ "Not enough space in chest!");
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
					b.setType(bridgeMaterial.getItemType());
					b.setData(bridgeMaterial.getData());
					amount++;
				}
			}
			BlockbagUtil.safeRemoveItems(chest,
					bridgeMaterial.toItemStack(amount));
			if (player != null) {
				player.sendMessage(ChatColor.GOLD + "Bridge closed!");
			}
		} catch (OutOfMaterialException ex) {
			for (Block b : blockSet) {
				if (b.getType() == bridgeMaterial.getItemType()) {
					b.setType(Material.AIR);
					amount--;
					if (amount == 0) {
						if (player != null) {
							player.sendMessage(ChatColor.RED
									+ "Not enough items in chest! Still need: "
									+ Integer.toString(ex.getAmount())
									+ " of type: "
									+ bridgeMaterial.getItemType().name());
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
			if (b.getType() == bridgeMaterial.getItemType()
					|| canPassThrough(b.getType())) {
				return (!canPassThrough(b.getType()));
			}
		}
		return false;
	}
}
