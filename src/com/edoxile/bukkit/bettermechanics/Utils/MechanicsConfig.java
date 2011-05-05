package com.edoxile.bukkit.bettermechanics.Utils;

import com.edoxile.bukkit.bettermechanics.BetterMechanics;
import com.edoxile.bukkit.bettermechanics.Exceptions.ConfigWriteException;
import org.bukkit.Material;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class MechanicsConfig {
    private static BetterMechanics plugin;
    private static Configuration config;

    public final BridgeConfig bridgeConfig;
    public final GateConfig gateConfig;
    public final DoorConfig doorConfig;
    public final LiftConfig liftConfig;
    public final HiddenSwitchConfig hiddenSwitchConfig;
    public final LightSwitchConfig lightSwitchConfig;
    public final AmmeterConfig ammeterConfig;

    public MechanicsConfig(BetterMechanics p) throws ConfigWriteException {
        plugin = p;
        config = plugin.getConfiguration();
        if (config == null) {
            createConfig();
        } else {
            createConfig();
        }

        bridgeConfig = new BridgeConfig();
        gateConfig = new GateConfig();
        doorConfig = new DoorConfig();
        liftConfig = new LiftConfig();
        hiddenSwitchConfig = new HiddenSwitchConfig();
        lightSwitchConfig = new LightSwitchConfig();
        ammeterConfig = new AmmeterConfig();

    }

    public static class BridgeConfig {
        public final boolean enabled;
        public final Set<Material> materials;
        public final int maxLength;

        public BridgeConfig() {
            enabled = config.getBoolean("bridge.enabled", true);
            maxLength = config.getInt("bridge.max-length", 32);
            List<Integer> list = config.getIntList("bridge.allowed-materials", Arrays.asList(3, 4, 5, 22, 35, 41, 42, 45, 47, 57, 87, 88, 89, 91));
            Set<Material> hashSet = new HashSet<Material>();
            for (int m : list)
                hashSet.add(Material.getMaterial(m));
            materials = Collections.unmodifiableSet(hashSet);
        }

        public boolean canUseBlock(Material b) {
            return materials.contains(b);
        }
    }

    public class GateConfig {
        public final boolean enabled;
        public final int maxLength;
        public final int maxWidth;
        public final int maxHeight;

        public GateConfig() {
            enabled = config.getBoolean("door.enabled", true);
            maxHeight = config.getInt("door.max-height", 32);
            maxLength = config.getInt("door.max-length", 32);
            maxWidth = config.getInt("door.max-width", 3);
        }
    }

    public class DoorConfig {
        public final boolean enabled;
        public final int maxHeight;
        public final Set<Material> materials;

        public DoorConfig() {
            enabled = config.getBoolean("door.enabled", true);
            maxHeight = config.getInt("door.max-height", 32);
            List<Integer> list = config.getIntList("door.allowed-materials", Arrays.asList(3, 4, 5, 22, 35, 41, 42, 45, 47, 57, 87, 88, 89, 91));
            Set<Material> hashSet = new HashSet<Material>();
            for (int m : list)
                hashSet.add(Material.getMaterial(m));
            materials = Collections.unmodifiableSet(hashSet);
        }

        public boolean canUseBlock(Material b) {
            return materials.contains(b);
        }
    }

    public class LiftConfig {
        public final boolean enabled;
        public final int maxSearchHeight;

        public LiftConfig() {
            enabled = config.getBoolean("lift.enabled", true);
            maxSearchHeight = config.getInt("lift.max-search-height", 32);
        }
    }

    public class LightSwitchConfig {
        public final boolean enabled;

        public LightSwitchConfig() {
            enabled = config.getBoolean("light-switch.enabled", true);
        }
    }

    public class HiddenSwitchConfig {
        public final boolean enabled;

        public HiddenSwitchConfig() {
            enabled = config.getBoolean("hidden-switch.enabled", true);
        }
    }

    public class AmmeterConfig {
        public final boolean enabled;

        public AmmeterConfig() {
            enabled = config.getBoolean("ammeter.enabled", true);
        }
    }

    public BridgeConfig getBridgeConfig(){
        return this.bridgeConfig;
    }

    public GateConfig getGateConfig(){
        return this.gateConfig;
    }

    private void createConfig() throws ConfigWriteException {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.canRead()) {
            try {
                configFile.getParentFile().mkdirs();
                JarFile jar = new JarFile(plugin.getConfigFile());
                JarEntry entry = jar.getJarEntry("config.yml");
                InputStream is = jar.getInputStream(entry);
                FileOutputStream os = new FileOutputStream(configFile);
                byte[] buf = new byte[(int) entry.getSize()];
                is.read(buf, 0, (int) entry.getSize());
                os.write(buf);
                os.close();
                plugin.getConfiguration().load();
            } catch (Exception e) {
                throw new ConfigWriteException();
            }
        }
    }
}
