package com.edoxile.bukkit.bettermechanics.Listeners;

import com.edoxile.bukkit.bettermechanics.Exceptions.*;
import com.edoxile.bukkit.bettermechanics.Mechanics.Bridge;
import com.edoxile.bukkit.bettermechanics.Mechanics.Gate;
import com.edoxile.bukkit.bettermechanics.Utils.MechanicsConfig;
import com.edoxile.bukkit.bettermechanics.Utils.SignUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */

public class MechanicsPlayerListener extends PlayerListener {
    private static Logger log = Logger.getLogger("Minecraft");
    private MechanicsConfig config;

    public MechanicsPlayerListener(MechanicsConfig c){
        config = c;
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (SignUtil.isSign(event.getClickedBlock())) {
                Sign sign = SignUtil.getSign(event.getClickedBlock());
                if (sign != null) {
                    if (SignUtil.getActiveMechanicsType(sign) != null) {
                        switch (SignUtil.getActiveMechanicsType(sign)) {
                            case BRIDGE: {
                                Bridge bridge = new Bridge(config, sign, event.getPlayer());
                                try {
                                    if(!bridge.map())
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
                                    if(!gate.map())
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
                                } catch (OutOfBoundsException e){
                                    event.getPlayer().sendMessage(ChatColor.RED + "Gate too long or too wide!");
                                } catch (BlockNotFoundException e){
                                    event.getPlayer().sendMessage(ChatColor.RED + "No fences were found close to bridge!");
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
