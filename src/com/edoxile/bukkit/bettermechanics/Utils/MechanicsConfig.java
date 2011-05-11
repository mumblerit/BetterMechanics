package com.edoxile.bukkit.bettermechanics.Utils;

import com.edoxile.bukkit.bettermechanics.BetterMechanics;
import com.edoxile.bukkit.bettermechanics.Exceptions.ConfigWriteException;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Edoxile
 */
public class MechanicsConfig {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static BetterMechanics plugin;
    private static Configuration config;

    public final BridgeConfig bridgeConfig;
    public final GateConfig gateConfig;
    public final DoorConfig doorConfig;
    public final LiftConfig liftConfig;
    public final HiddenSwitchConfig hiddenSwitchConfig;
    public final AmmeterConfig ammeterConfig;
    public final CauldronConfig cauldronConfig;
    public final PermissionConfig permissionConfig;

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
        ammeterConfig = new AmmeterConfig();
        cauldronConfig = new CauldronConfig();
        permissionConfig = new PermissionConfig();
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

    public class HiddenSwitchConfig {
        public final boolean enabled;

        public HiddenSwitchConfig() {
            enabled = config.getBoolean("hidden-switch.enabled", true);
        }
    }

    public class CauldronConfig {
        public final boolean enabled;
        public final CauldronCookbook cauldronCookbook;

        public CauldronConfig() {
            enabled = config.getBoolean("cauldron.enabled", true);
            cauldronCookbook = new CauldronCookbook();
        }
    }

    public class AmmeterConfig {
        public final boolean enabled;

        public AmmeterConfig() {
            enabled = config.getBoolean("ammeter.enabled", true);
        }
    }

    public class PermissionConfig {
        public final boolean usePermissions;
        public final boolean useWorldGuard;
        private WorldGuardPlugin worldGuard = null;
        private PermissionHandler permissionHandler = null;

        public PermissionConfig() {
            usePermissions = config.getBoolean("use-permissions", true);
            useWorldGuard = config.getBoolean("use-worldguard", true);
            if (usePermissions) {
                this.setupPermissions();
                log.info("[BetterMechanics] Using Permissions");
            }
            if (useWorldGuard) {
                this.setupWorldGuard();
                log.info("[BetterMechanics] Using WorldGuard");
            }
        }

        private void setupWorldGuard() {
            Plugin wg = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
            if (worldGuard == null) {
                if (plugin != null) {
                    worldGuard = (WorldGuardPlugin) wg;
                }
            }
        }

        private void setupPermissions() {
            Plugin permissionsPlugin = plugin.getServer().getPluginManager().getPlugin("Permissions");

            if (this.permissionHandler == null) {
                if (permissionsPlugin != null) {
                    this.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
                } else {
                    log.info("[BetterMechanics] Permission system not detected, defaulting to OP");
                }
            }
        }

        public boolean checkPermissions(Player player, String permission) {
            if (permissionHandler == null) {
                return true;
            } else {
                return player.isOp() || permissionHandler.permission(player, "bettermechanics." + permission);
            }
        }

        public boolean checkWorldGuard(Player player, Block clickedBlock) {
            if (worldGuard == null) {
                return true;
            } else {
                return player.isOp() || worldGuard.canBuild(player, clickedBlock);
            }
        }

        public boolean check(Player player, String type, Block clickedBlock) {
            boolean allowed = false;
            if (checkPermissions(player, type)) {
                if (checkWorldGuard(player, clickedBlock)) {
                    allowed = true;
                }
            }
            return allowed;
        }
    }

    public PermissionConfig getPermissionConfig() {
        return this.permissionConfig;
    }

    public BridgeConfig getBridgeConfig() {
        return this.bridgeConfig;
    }

    public GateConfig getGateConfig() {
        return this.gateConfig;
    }

    public DoorConfig getDoorConfig() {
        return this.doorConfig;
    }

    public HiddenSwitchConfig getHiddenSwitchConfig() {
        return this.hiddenSwitchConfig;
    }

    public LiftConfig getLiftConfig() {
        return this.liftConfig;
    }

    public AmmeterConfig getAmmeterConfig() {
        return this.ammeterConfig;
    }

    public CauldronConfig getCauldronConfig() {
        return this.cauldronConfig;
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
