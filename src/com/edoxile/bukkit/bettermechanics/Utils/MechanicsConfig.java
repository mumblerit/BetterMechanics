package com.edoxile.bukkit.bettermechanics.Utils;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class MechanicsConfig {
    private static JavaPlugin plugin;
    private static Configuration config;

    public MechanicsConfig(JavaPlugin p){
        plugin = p;
        config = plugin.getConfiguration();
    }

    public static class BridgeConfig{
        public static boolean enabled;
        public static List<Material> materials;
        public static int maxLength;
    }

    public static class GateConfig{
        public static boolean enabled;
        public static int maxLength;
        public static int maxWidth;
        public static int maxHeight;
    }

    public static class DoorConfig{
        public static boolean enabled;
        public static int maxLength;
        public static int maxHeight;
        public static List<Material> materials;
    }

    public static class LiftConfig{
        public static boolean enabled;
        public static int maxSearchHeight;
    }

    public static class LightSwitchConfig{
        public static boolean enabled;
    }

    public static class HiddenSwitchConfig{
        public static boolean enabled;
    }

    public static class Ammeter{
        public static boolean enabled;
    }

    public boolean loadConfig() throws Throwable{
        if(config == null){
            createBlankConfig();
            return false;
        }else{
            return true;
        }
    }

    private void createBlankConfig() throws IOException {

    }
}
