package com.edoxile.bukkit.bettermechanics.Utils;

import com.edoxile.bukkit.bettermechanics.Exceptions.ConfigLoadException;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class EventManager{
    private JavaPlugin plugin;
    private MechanicsConfig config;
    private MechanicsPlayerListener playerListener = new MechanicsPlayerListener();
    private MechanicsBlockListener blockListener = new MechanicsBlockListener();

    public EventManager(JavaPlugin p, MechanicsConfig c){
        plugin = p;
        config = c;
    }

    public class MechanicsPlayerListener extends PlayerListener{
        public void onPlayerInteract(PlayerInteractEvent event){

        }
    }

    public class MechanicsBlockListener extends BlockListener{
        public void onSignChange(SignChangeEvent event){

        }

        public void onBlockRedstoneChange(BlockRedstoneEvent event){

        }
    }

    public void registerEvents() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Event.Priority.Normal, plugin);
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Event.Priority.Normal, plugin);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, plugin);
    }
}
