package com.edoxile.bukkit.bettermechanics.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.util.config.Configuration;

import com.edoxile.bukkit.bettermechanics.BetterMechanics;
import com.edoxile.bukkit.bettermechanics.exceptions.ConfigWriteException;

/**
 * Created by IntelliJ IDEA. User: Edoxile
 */
public class MechanicsConfig {
	private static final Logger log = Logger.getLogger("Minecraft");
	private static BetterMechanics plugin;
	private static Configuration config;

	public BridgeConfig bridgeConfig;
	public GateConfig gateConfig;
	public DoorConfig doorConfig;
	public LiftConfig liftConfig;
	public HiddenSwitchConfig hiddenSwitchConfig;
	public CauldronConfig cauldronConfig;

	public MechanicsConfig(BetterMechanics p) throws ConfigWriteException {
		plugin = p;
		config = plugin.getConfiguration();
		config.load();
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
		cauldronConfig = new CauldronConfig();
	}

	public void reloadCauldronConfig() {
		cauldronConfig = new CauldronConfig();
	}

	public static class BridgeConfig {
		public final boolean enabled;
		public final Set<Material> materials;
		public final int maxLength;

		public BridgeConfig() {
			enabled = config.getBoolean("bridge.enabled", true);
			maxLength = config.getInt("bridge.max-length", 32);
			List<Integer> list = config.getIntList("bridge.allowed-materials",
					Arrays.asList(3, 4, 5, 22, 35, 41, 42, 45, 47, 57, 87, 88,
							89, 91));
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
			List<Integer> list = config.getIntList("door.allowed-materials",
					Arrays.asList(3, 4, 5, 22, 35, 41, 42, 45, 47, 57, 87, 88,
							89, 91));
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
			if (config.getBoolean("cauldron.enabled", true)) {
				cauldronCookbook = new CauldronCookbook(plugin);
				if (cauldronCookbook.size() > 0) {
					enabled = true;
				} else {
					log.warning("[BetterMechanics] Disabled cauldron because there were no recipes found in the config.");
					enabled = false;
				}
			} else {
				cauldronCookbook = null;
				enabled = false;
			}
		}
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
