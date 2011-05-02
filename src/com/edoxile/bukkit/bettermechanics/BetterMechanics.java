package com.edoxile.bukkit.bettermechanics;

import com.edoxile.bukkit.bettermechanics.Exceptions.ConfigWriteException;
import com.edoxile.bukkit.bettermechanics.Utils.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class BetterMechanics extends JavaPlugin {
    public static final Logger log = Logger.getLogger("Minecraft");
    private EventManager eventManager;
    private MechanicsConfig configManager;
    private File configFile;

    public void onDisable() {
    }

    public void onEnable() {
        try {
            configFile = this.getFile();
            configManager = new MechanicsConfig(this);
            eventManager = new EventManager(this, configManager);
            eventManager.registerEvents();
        } catch (ConfigWriteException ex) {
            log.severe("[BetterMechanics] Couldn't create config file.");
            this.setEnabled(false);
        }
    }

    public File getConfigFile(){
        return configFile;
    }
}
