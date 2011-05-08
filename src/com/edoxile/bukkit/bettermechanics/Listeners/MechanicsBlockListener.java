package com.edoxile.bukkit.bettermechanics.Listeners;

import com.edoxile.bukkit.bettermechanics.Mechanics.Bridge;
import com.edoxile.bukkit.bettermechanics.Mechanics.Door;
import com.edoxile.bukkit.bettermechanics.Mechanics.Gate;
import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.Utils.MechanicsConfig;
import com.edoxile.bukkit.bettermechanics.Utils.SignUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */

public class MechanicsBlockListener extends BlockListener {
    private static final Logger log = Logger.getLogger("Minecraft");
    private MechanicsConfig config;

    public MechanicsBlockListener(MechanicsConfig c) {
        config = c;
    }

    public void onSignChange(SignChangeEvent event) {
        MechanicsType type = SignUtil.getMechanicsType(event.getLine(1));
        if (type != null) {
            String name = type.name();
            name = name.replace("_", " ").toLowerCase();
            event.getPlayer().sendMessage(ChatColor.AQUA + "You created a " + name + "!");
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
                            }
                        }
                    }
                }
                if (sign == null)
                    return;
            }
        }
        switch (SignUtil.getActiveMechanicsType(sign)) {
            case BRIDGE: {
                Bridge bridge = new Bridge(config, sign, null);
                try {
                    if (!bridge.map())
                        return;
                    if (event.getNewCurrent() > 0) {
                        bridge.toggleOpen();
                    } else {
                        bridge.toggleClosed();
                    }
                } catch (Exception e) {
                }
            }
            break;
            case GATE: {
                Gate gate = new Gate(config, sign, null);
                try {
                    if (!gate.map())
                        return;
                    if (event.getNewCurrent() > 0) {
                        gate.toggleOpen();
                    } else {
                        gate.toggleClosed();
                    }
                } catch (Exception e) {
                }
            }
            break;
            case DOOR: {
                Door door = new Door(config, sign, null);
                try {
                    if (!door.map())
                        return;
                    if (event.getNewCurrent() > 0) {
                        door.toggleOpen();
                    } else {
                        door.toggleClosed();
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