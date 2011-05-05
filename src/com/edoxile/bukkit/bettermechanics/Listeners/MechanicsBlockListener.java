package com.edoxile.bukkit.bettermechanics.Listeners;

import com.edoxile.bukkit.bettermechanics.Exceptions.BlockNotFoundException;
import com.edoxile.bukkit.bettermechanics.Exceptions.ChestNotFoundException;
import com.edoxile.bukkit.bettermechanics.Exceptions.NonCardinalDirectionException;
import com.edoxile.bukkit.bettermechanics.Exceptions.OutOfBoundsException;
import com.edoxile.bukkit.bettermechanics.Mechanics.Bridge;
import com.edoxile.bukkit.bettermechanics.Mechanics.Gate;
import com.edoxile.bukkit.bettermechanics.MechanicsType;
import com.edoxile.bukkit.bettermechanics.Utils.MechanicsConfig;
import com.edoxile.bukkit.bettermechanics.Utils.SignUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
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
                    if (gate.isClosed()) {
                        gate.toggleOpen();
                    } else {
                        gate.toggleClosed();
                    }
                } catch (Exception e) {
                }
            }
            break;

            /*case DOOR: {
                Door door = new Door(config, sign, null);
                try {
                    door.map();
                    if (event.getNewCurrent() > 0) {
                        door.toggleClosed();
                    } else {
                        door.toggleOpen();
                    }
                } catch (Exception e) {

                }
            }
            break;
            case LIGHT_SWITCH: {
                LightSwitch lightSwitch = new LightSwitch(config, sign, null);
                try {
                    lightSwitch.map();
                    if (event.getNewCurrent() > 0) {
                        lightSwitch.toggleOn();
                    } else {
                        lightSwitch.toggleOff();
                    }
                } catch (Exception e) {

                }
            }
            break;*/
            default:
                return;
        }
    }
}