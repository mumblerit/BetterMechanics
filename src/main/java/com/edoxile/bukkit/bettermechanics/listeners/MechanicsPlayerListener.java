package com.edoxile.bukkit.bettermechanics.listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.exceptions.BlockNotFoundException;
import com.edoxile.bukkit.bettermechanics.exceptions.ChestNotFoundException;
import com.edoxile.bukkit.bettermechanics.exceptions.InvalidMaterialException;
import com.edoxile.bukkit.bettermechanics.exceptions.NonCardinalDirectionException;
import com.edoxile.bukkit.bettermechanics.exceptions.OutOfBoundsException;
import com.edoxile.bukkit.bettermechanics.mechanics.Bridge;
import com.edoxile.bukkit.bettermechanics.mechanics.Cauldron;
import com.edoxile.bukkit.bettermechanics.mechanics.Door;
import com.edoxile.bukkit.bettermechanics.mechanics.Gate;
import com.edoxile.bukkit.bettermechanics.mechanics.HiddenSwitch;
import com.edoxile.bukkit.bettermechanics.mechanics.Lift;
import com.edoxile.bukkit.bettermechanics.utils.MechanicsConfig;
import com.edoxile.bukkit.bettermechanics.utils.SignUtil;

/**
 * Created by IntelliJ IDEA. User: Edoxile
 */

public class MechanicsPlayerListener extends PlayerListener {
	private static final Logger log = Logger.getLogger("Minecraft");
	private MechanicsConfig config;;

	public MechanicsPlayerListener(MechanicsConfig c) {
		config = c;
	}

	public void setConfig(MechanicsConfig c) {
		config = c;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (SignUtil.isSign(event.getClickedBlock())) {
				Sign sign = SignUtil.getSign(event.getClickedBlock());
				if (sign != null) {
					if (SignUtil.getActiveMechanicsType(sign) != null) {
						switch (SignUtil.getActiveMechanicsType(sign)) {
						case BRIDGE:
						case SMALL_BRIDGE:

							Bridge bridge = new Bridge(config, sign,
									event.getPlayer());
							try {
								if (!bridge.map())
									return;
								if (bridge.isClosed()) {
									bridge.toggleOpen();
								} else {
									bridge.toggleClosed();
								}
							} catch (InvalidMaterialException e) {
								event.getPlayer()
										.sendMessage(
												ChatColor.RED
														+ "Bridge not made of an allowed Material!");
							} catch (BlockNotFoundException e) {
								event.getPlayer()
										.sendMessage(
												ChatColor.RED
														+ "Bridge is too long or sign on the other side was not found!");
							} catch (ChestNotFoundException e) {
								event.getPlayer().sendMessage(
										ChatColor.RED
												+ "No chest found near signs!");
							} catch (NonCardinalDirectionException e) {
								event.getPlayer()
										.sendMessage(
												ChatColor.RED
														+ "Sign is not in a cardinal direction!");
							}

							break;
						case GATE:
						case SMALL_GATE:

							Gate gate = new Gate(config, sign,
									event.getPlayer());
							try {
								if (!gate.map())
									return;
								if (gate.isClosed()) {
									gate.toggleOpen();
								} else {
									gate.toggleClosed();
								}
							} catch (ChestNotFoundException e) {
								event.getPlayer().sendMessage(
										ChatColor.RED
												+ "No chest found near signs!");
							} catch (NonCardinalDirectionException e) {
								event.getPlayer()
										.sendMessage(
												ChatColor.RED
														+ "Sign is not in a cardinal direction!");
							} catch (OutOfBoundsException e) {
								event.getPlayer().sendMessage(
										ChatColor.RED
												+ "Gate too long or too wide!");
							} catch (BlockNotFoundException e) {
								event.getPlayer()
										.sendMessage(
												ChatColor.RED
														+ "No fences were found close to bridge!");
							}

							break;
						case DOOR:
						case SMALL_DOOR:

							Door door = new Door(config, sign,
									event.getPlayer());
							try {
								if (!door.map())
									return;
								if (door.isClosed()) {
									door.toggleOpen();
								} else {
									door.toggleClosed();
								}
							} catch (InvalidMaterialException e) {
								event.getPlayer()
										.sendMessage(
												ChatColor.RED
														+ "Door not made of an allowed Material!");
							} catch (BlockNotFoundException e) {
								event.getPlayer()
										.sendMessage(
												ChatColor.RED
														+ "Door is too long or sign on the other side was not found!");
							} catch (ChestNotFoundException e) {
								event.getPlayer().sendMessage(
										ChatColor.RED
												+ "No chest found near signs!");
							} catch (NonCardinalDirectionException e) {
								event.getPlayer()
										.sendMessage(
												ChatColor.RED
														+ "Sign is not in a cardinal direction!");
							}
							break;
						case LIFT:

							Lift lift = new Lift(config, sign,
									event.getPlayer());
							try {
								if (!lift.map()) {
									
									event.getPlayer()
									.sendMessage(
											ChatColor.RED
													+ "Somethings Wrong in lift.map()!--!");
									return;
								}
								lift.movePlayer();
							} catch (BlockNotFoundException e) {
								event.getPlayer()
										.sendMessage(
												ChatColor.RED
														+ "Lift is too high or signs are not aligned!");
							}
							break;

						}
					}
				}
			} 
			 else {
				if (!event.getPlayer().getItemInHand().getType().isBlock()
						|| event.getPlayer().getItemInHand().getType() == Material.AIR) {
					Cauldron cauldron = Cauldron.preCauldron(
							event.getClickedBlock(), config, event.getPlayer());
					if (cauldron != null) {

						cauldron.performCauldron();
					} else {
						return;
					}
				}
			}
			if (isRedstoneBlock(event.getClickedBlock().getTypeId()))
				return;

			BlockFace[] toCheck = { BlockFace.WEST, BlockFace.EAST,
					BlockFace.SOUTH, BlockFace.NORTH, BlockFace.DOWN,
					BlockFace.UP };
			for (BlockFace b : toCheck) {
				if (SignUtil.isSign(event.getClickedBlock().getRelative(b))) {
					Sign sign = SignUtil.getSign(event.getClickedBlock()
							.getRelative(b));
					if (SignUtil.getMechanicsType(sign) == MechanicsType.HIDDEN_SWITCH) {

						HiddenSwitch hiddenSwitch = new HiddenSwitch(config,
								sign, event.getPlayer());
						if (hiddenSwitch.map())
							hiddenSwitch.toggleLevers();

					}
				}
			}
		}
	}

	/**
	 * Returns true if a block uses redstone in some way. Shamelessly stolen
	 * from sk89q's craftbook
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isRedstoneBlock(int id) {
		return id == Material.LEVER.getId()
				|| id == Material.STONE_PLATE.getId()
				|| id == Material.WOOD_PLATE.getId()
				|| id == Material.REDSTONE_TORCH_ON.getId()
				|| id == Material.REDSTONE_TORCH_OFF.getId()
				|| id == Material.STONE_BUTTON.getId()
				|| id == Material.REDSTONE_WIRE.getId()
				|| id == Material.WOODEN_DOOR.getId()
				|| id == Material.IRON_DOOR.getId();
	}
}
