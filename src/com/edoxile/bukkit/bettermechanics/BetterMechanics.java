package com.edoxile.bukkit.bettermechanics;

import com.edoxile.bukkit.bettermechanics.Utils.*;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class BetterMechanics extends JavaPlugin {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static EventManager eventManager;
    private static MechanicsConfig configManager;

    public void onDisable() {
        configManager = new MechanicsConfig(this);
        eventManager = new EventManager(this);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onEnable() {
        eventManager = new EventManager(this);
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
