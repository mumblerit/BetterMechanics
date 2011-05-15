package com.edoxile.bukkit.bettermechanics.Listeners;

import com.edoxile.bukkit.bettermechanics.Exceptions.*;
import com.edoxile.bukkit.bettermechanics.Mechanics.*;
import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.Utils.MechanicsConfig;
import com.edoxile.bukkit.bettermechanics.Utils.SignUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */

public class MechanicsPlayerListener extends PlayerListener {
    private static final Logger log = Logger.getLogger("Minecraft");
    private MechanicsConfig config;
    private MechanicsConfig.PermissionConfig permissions;

    public MechanicsPlayerListener(MechanicsConfig c) {
        config = c;
        permissions = c.getPermissionConfig();
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        Pen.clear(event.getPlayer());
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (SignUtil.isSign(event.getClickedBlock())) {
                Sign sign = SignUtil.getSign(event.getClickedBlock());
                if (sign != null) {
                    if (SignUtil.getActiveMechanicsType(sign) != null) {
                        if (!permissions.check(event.getPlayer(), SignUtil.getActiveMechanicsType(sign).name().toLowerCase(), event.getClickedBlock())) {
                            return;
                        }
                        switch (SignUtil.getActiveMechanicsType(sign)) {
                            case BRIDGE: {
                                Bridge bridge = new Bridge(config, sign, event.getPlayer());
                                try {
                                    if (!bridge.map())
                                        return;
                                    if (bridge.isClosed()) {
                                        bridge.toggleOpen();
                                    } else {
                                        bridge.toggleClosed();
                                    }
                                } catch (InvalidMaterialException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "Bridge not made of an allowed Material!");
                                } catch (BlockNotFoundException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "Bridge is too long or sign on the other side was not found!");
                                } catch (ChestNotFoundException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "No chest found near signs!");
                                } catch (NonCardinalDirectionException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "Sign is not in a cardinal direction!");
                                }
                            }
                            break;
                            case GATE:
                            case SMALL_GATE: {
                                Gate gate = new Gate(config, sign, event.getPlayer());
                                try {
                                    if (!gate.map())
                                        return;
                                    if (gate.isClosed()) {
                                        gate.toggleOpen();
                                    } else {
                                        gate.toggleClosed();
                                    }
                                } catch (ChestNotFoundException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "No chest found near signs!");
                                } catch (NonCardinalDirectionException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "Sign is not in a cardinal direction!");
                                } catch (OutOfBoundsException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "Gate too long or too wide!");
                                } catch (BlockNotFoundException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "No fences were found close to bridge!");
                                }
                            }
                            break;
                            case DOOR: {
                                Door door = new Door(config, sign, event.getPlayer());
                                try {
                                    if (!door.map())
                                        return;
                                    if (door.isClosed()) {
                                        door.toggleOpen();
                                    } else {
                                        door.toggleClosed();
                                    }
                                } catch (InvalidMaterialException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "Door not made of an allowed Material!");
                                } catch (BlockNotFoundException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "Door is too long or sign on the other side was not found!");
                                } catch (ChestNotFoundException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "No chest found near signs!");
                                } catch (NonCardinalDirectionException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "Sign is not in a cardinal direction!");
                                }
                            }
                            break;
                            case LIFT: {
                                Lift lift = new Lift(config, sign, event.getPlayer());
                                try {
                                    if (!lift.map()) {
                                        return;
                                    }
                                    lift.movePlayer();
                                } catch (BlockNotFoundException e) {
                                    event.getPlayer().sendMessage(ChatColor.RED + "Lift is too high or signs are not aligned!");
                                }
                            }
                        }
                    } else if (event.getPlayer().getItemInHand().getType() == config.getPenConfig().penMaterial) {
                        if (permissions.check(event.getPlayer(), "pen", event.getClickedBlock()) && permissions.checkZones(event.getPlayer(), event.getClickedBlock())) {
                            String[] text = Pen.getLines(event.getPlayer());
                            if (text != null) {
                                SignChangeEvent evt = new SignChangeEvent(sign.getBlock(), event.getPlayer(), text);
                                event.getPlayer().getServer().getPluginManager().callEvent(evt);
                                if (!evt.isCancelled()) {
                                    for (int i = 0; i < text.length; i++) {
                                        sign.setLine(i, text[i]);
                                    }
                                    sign.update(true);
                                    event.getPlayer().sendMessage(ChatColor.GOLD + "You edited the sign! If you don't see the changes, place a torch near it.");
                                }
                            } else {
                                text = sign.getLines();
                                Pen.setText(event.getPlayer(), text);
                                event.getPlayer().sendMessage(ChatColor.GOLD + "Loaded sign text in memory.");
                            }
                        }
                    }
                }
            } else if (event.getClickedBlock().getType() == Material.REDSTONE_WIRE && event.getPlayer().getItemInHand().getType() == Material.COAL) {
                if (!permissions.check(event.getPlayer(), "ammeter", event.getClickedBlock())) {
                    return;
                }
                Ammeter ammeter = new Ammeter(config, event.getClickedBlock(), event.getPlayer());
                ammeter.measure();
            } else {
                if (!event.getPlayer().getItemInHand().getType().isBlock() || event.getPlayer().getItemInHand().getType() == Material.AIR) {
                    Cauldron cauldron = Cauldron.preCauldron(event.getClickedBlock(), config, event.getPlayer());
                    if (cauldron != null) {
                        if (permissions.check(event.getPlayer(), "cauldron", event.getClickedBlock())) {
                            cauldron.performCauldron();
                        } else {
                            return;
                        }
                    }
                }
                if (isRedstoneBlock(event.getClickedBlock().getTypeId()))
                    return;

                BlockFace[] toCheck = {BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.DOWN, BlockFace.UP};
                for (BlockFace b : toCheck) {
                    if (SignUtil.isSign(event.getClickedBlock().getRelative(b))) {
                        Sign sign = SignUtil.getSign(event.getClickedBlock().getRelative(b));
                        if (SignUtil.getMechanicsType(sign) == MechanicsType.HIDDEN_SWITCH) {
                            if (permissions.check(event.getPlayer(), "hidden_switch", event.getClickedBlock())) {
                                HiddenSwitch hiddenSwitch = new HiddenSwitch(config, sign, event.getPlayer());
                                if (hiddenSwitch.map())
                                    hiddenSwitch.toggleLevers();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns true if a block uses redstone in some way.
     * Shamelessly stolen from sk89q's craftbook
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
