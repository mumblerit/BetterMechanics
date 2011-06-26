package com.edoxile.bukkit.bettermechanics.listeners;

import com.edoxile.bukkit.bettermechanics.mechanics.Bridge;
import com.edoxile.bukkit.bettermechanics.mechanics.Door;
import com.edoxile.bukkit.bettermechanics.mechanics.Gate;
import com.edoxile.bukkit.bettermechanics.utils.MechanicsConfig;
import com.edoxile.bukkit.bettermechanics.utils.SignUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */

public class MechanicsBlockListener extends BlockListener {
    private MechanicsConfig config;
    private MechanicsConfig.PermissionConfig permissions;

    public MechanicsBlockListener(MechanicsConfig c) {
        config = c;
        permissions = c.getPermissionConfig();
    }

    public void onSignChange(SignChangeEvent event) {
        String str = event.getLine(1);
        if (SignUtil.getMechanicsType(str) == null) {
            return;
        } else {
            if (!permissions.check(event.getPlayer(), SignUtil.getActiveMechanicsType(str).name().toLowerCase() + ".create", event.getBlock(), false)) {
                event.setCancelled(true);
                System.out.println("No permissions!");
                return;
            } else {
                System.out.println("You have permissions to do this!");
            }
        }

        if (str.equalsIgnoreCase("[lift up]")) {
            event.setLine(1, "[Lift Up]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a lift!");
        } else if (str.equalsIgnoreCase("[Lift Down]")) {
            event.setLine(1, "[Lift Down]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a lift!");
        } else if (str.equalsIgnoreCase("[Lift]")) {
            event.setLine(1, "[Lift]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a lift!");
        } else if (str.equalsIgnoreCase("[gate]")) {
            event.setLine(1, "[Gate]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a gate!");
        } else if (str.equalsIgnoreCase("[dgate]")) {
            event.setLine(1, "[Dgate]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a small gate!");
        } else if (str.equalsIgnoreCase("[bridge]")) {
            event.setLine(1, "[Bridge]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a bridge!");
        } else if (str.equalsIgnoreCase("[bridge end]")) {
            event.setLine(1, "[Bridge End]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a bridge!");
        } else if (str.equalsIgnoreCase("[sbridge]")) {
            event.setLine(1, "[sBridge]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a bridge!");
        } else if (str.equalsIgnoreCase("[sbridge end]")) {
            event.setLine(1, "[sBridge End]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a bridge!");
        } else if (str.equalsIgnoreCase("[door up]")) {
            event.setLine(1, "[Door Up]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a door!");
        } else if (str.equalsIgnoreCase("[door down]")) {
            event.setLine(1, "[Door Down]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a door!");
        } else if (str.equalsIgnoreCase("[door]")) {
            event.setLine(1, "[Door]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a door!");
        } else if (str.equalsIgnoreCase("[sdoor]")) {
            event.setLine(1, "[sDoor]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a small door!");
        } else if (str.equalsIgnoreCase("[sdoor up]")) {
            event.setLine(1, "[sDoor Up]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a small door!");
        } else if (str.equalsIgnoreCase("[sdoor down]")) {
            event.setLine(1, "[sDoor Down]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a small door!");
        } else if (str.equalsIgnoreCase("[x]")) {
            event.setLine(1, "[X]");
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a hidden switch!");
        } else {
            return;
        }
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockAgainst();
        if (SignUtil.isSign(block)) {
            Sign sign = SignUtil.getSign(block);
            if (SignUtil.getMechanicsType(sign) != null) {
                event.setCancelled(true);
            }
        }
    }

    public void onBlockRedstoneChange(BlockRedstoneEvent event) {
        if ((event.getNewCurrent() == event.getOldCurrent()) || (event.getNewCurrent() > 0 && event.getOldCurrent() > 0))
            return;
        Block block = event.getBlock();
        Sign sign = null;
        for (int dx = -1; dx <= 1; dx++) {
            if (SignUtil.isSign(block.getRelative(dx, 0, 0))) {
                sign = SignUtil.getSign(block.getRelative(dx, 0, 0));
                if (sign != null) {
                    if (SignUtil.getActiveMechanicsType(sign) != null) {
                        break;
                    } else {
                        sign = null;
                    }
                }
            }
        }
        if (sign == null) {
            for (int dy = -1; dy <= 1; dy++) {
                if (SignUtil.isSign(block.getRelative(0, dy, 0))) {
                    sign = SignUtil.getSign(block.getRelative(0, dy, 0));
                    if (sign != null) {
                        if (SignUtil.getActiveMechanicsType(sign) != null) {
                            break;
                        } else {
                            sign = null;
                        }
                    }
                }
            }
            if (sign == null) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (SignUtil.isSign(block.getRelative(0, 0, dz))) {
                        sign = SignUtil.getSign(block.getRelative(0, 0, dz));
                        if (sign != null) {
                            if (SignUtil.getActiveMechanicsType(sign) != null) {
                                break;
                            } else {
                                sign = null;
                            }
                        }
                    }
                }
                if (sign == null)
                    return;
            }
        }
        switch (SignUtil.getActiveMechanicsType(sign)) {
            case SMALL_BRIDGE:
            case BRIDGE: {
                Bridge bridge = new Bridge(config, sign, null);
                try {
                    if (!bridge.map())
                        return;
                    if (event.getNewCurrent() > 0) {
                        bridge.toggleClosed();
                    } else {
                        bridge.toggleOpen();
                    }
                } catch (Exception e) {
                }
            }
            break;
            case SMALL_GATE:
            case GATE: {
                Gate gate = new Gate(config, sign, null);
                try {
                    if (!gate.map())
                        return;
                    if (event.getNewCurrent() > 0) {
                        gate.toggleClosed();
                    } else {
                        gate.toggleOpen();
                    }
                } catch (Exception e) {
                }
            }
            break;
            case SMALL_DOOR:
            case DOOR: {
                Door door = new Door(config, sign, null);
                try {
                    if (!door.map())
                        return;
                    if (event.getNewCurrent() > 0) {
                        door.toggleClosed();
                    } else {
                        door.toggleOpen();
                    }
                } catch (Exception e) {
                }
            }
            break;
            default:
                return;
        }
    }
}