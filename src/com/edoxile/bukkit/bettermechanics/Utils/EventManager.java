package com.edoxile.bukkit.bettermechanics.Utils;

import com.edoxile.bukkit.bettermechanics.Mechanics.*;
import com.edoxile.bukkit.bettermechanics.MechanicsType;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class EventManager {
    private JavaPlugin plugin;
    private MechanicsConfig config;
    private MechanicsPlayerListener playerListener = new MechanicsPlayerListener();
    private MechanicsBlockListener blockListener = new MechanicsBlockListener();

    public EventManager(JavaPlugin p, MechanicsConfig c) {
        plugin = p;
        config = c;
    }

    public class MechanicsPlayerListener extends PlayerListener {
        public void onPlayerInteract(PlayerInteractEvent event) {

        }
    }

    public class MechanicsBlockListener extends BlockListener {
        public void onSignChange(SignChangeEvent event) {
            if (SignUtil.isSign(event.getBlock())) {
                Sign s = SignUtil.getSign(event.getBlock());
                if (s != null) {
                    MechanicsType type = SignUtil.getMechanicsType(s);
                    if (type != null) {
                        String name = type.name();
                        name = name.replace("_", " ").toLowerCase();
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "You created a " + name + "!");
                    }
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
                    Bridge bridge = new Bridge(sign, null);
                    try {
                        bridge.map();
                        if (event.getNewCurrent() > 0) {
                            bridge.toggleClosed();
                        } else {
                            bridge.toggleOpen();
                        }
                    } catch (Exception e) {

                    }
                }
                break;
                case DOOR: {
                    Door door = new Door(sign, null);
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
                case GATE: {
                    Gate gate = new Gate(sign, null);
                    try {
                        gate.map();
                        if (event.getNewCurrent() > 0) {
                            gate.toggleClosed();
                        } else {
                            gate.toggleOpen();
                        }
                    } catch (Exception e) {

                    }
                }
                break;
                case LIGHT_SWITCH: {
                    LightSwitch lightSwitch = new LightSwitch(sign, null);
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
                break;
                default:
                    return;
            }
        }
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Event.Priority.Normal, plugin);
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Event.Priority.Normal, plugin);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, plugin);
    }
}
