package com.edoxile.bukkit.bettermechanics;

import com.edoxile.bukkit.bettermechanics.Exceptions.ConfigWriteException;
import com.edoxile.bukkit.bettermechanics.Listeners.*;
import com.edoxile.bukkit.bettermechanics.Utils.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class BetterMechanics extends JavaPlugin {
    public static final Logger log = Logger.getLogger("Minecraft");
    private MechanicsPlayerListener playerListener;
    private MechanicsBlockListener blockListener;
    private MechanicsConfig configManager;
    private File configFile;

    public void onDisable() {
        log.severe("[BetterMechanics] disabled.");
    }

    public void onEnable() {
        try {
            configFile = this.getFile();
            configManager = new MechanicsConfig(this);
            blockListener = new MechanicsBlockListener(configManager);
            playerListener = new MechanicsPlayerListener(configManager);
            registerEvents();
            log.severe("[BetterMechanics] Loading completed.");
        } catch (ConfigWriteException ex) {
            log.severe("[BetterMechanics] Couldn't create config file.");
            this.setEnabled(false);
        }
    }

    public File getConfigFile() {
        return configFile;
    }

    public void registerEvents() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
    }
}
