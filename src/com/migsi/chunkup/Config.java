package com.migsi.chunkup;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	// Determines which permission system is used
	public static boolean Permissions = true;
	public static boolean Op = false;
	
	// File configurations
	private static final String CONFIGFILENAME = "config.yml";
	private static final String CHUNKSFILENAME = "chunks.yml";
	private static File ConfigFile = null;
	private static File ChunksFile = null;
	private static FileConfiguration ChunksFileConfig = null;

	public Config() {
		load();
	}

	public void load() {
		try {
			if (!ChunkUp.instance.getDataFolder().exists()) {
				ChunkUp.instance.getDataFolder().mkdirs();
			}
			
			ConfigFile = new File(ChunkUp.instance.getDataFolder(), CONFIGFILENAME);
			
			if (!ConfigFile.exists()) {
				ChunkUp.instance.getLogger().info("Config.yml not found, creating a new one...");

				writeConfig();
				
			} else {
				ChunkUp.instance.getLogger().info("Config.yml found, loading...");
				loadConfig();
			}
			
			ChunksFile = new File(ChunkUp.instance.getDataFolder(), CHUNKSFILENAME);
			ChunksFileConfig = YamlConfiguration.loadConfiguration(ChunksFile);
			
			if (!ChunksFile.exists()) {
				ChunkUp.instance.getLogger().info("Chunks.yml not found, creating a new one...");

				writeChunks();
				
			} else {
				ChunkUp.instance.getLogger().info("Chunks.yml found, loading...");
				loadChunks();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void loadConfig() {
			
			ChunkUp.setUseAlternativeChunkLoader(ChunkUp.instance.getConfig().getBoolean(ConfigEnum.USE_ALTERNATIVE_CHUNKLOADER.value()));
			ChunkUpPlayer.setIgnoreCount(ChunkUp.instance.getConfig().getInt(ConfigEnum.IGNORE_INTERVAL.value()));
			ChunkLoader.setRefreshTime(ChunkUp.instance.getConfig().getInt(ConfigEnum.REFRESH_IN_TICKS.value()));
			ChunkData.setNextID(ChunkUp.instance.getConfig().getLong(ConfigEnum.NEXT_ID.value()));
			ChunkData.setNextRoute(ChunkUp.instance.getConfig().getLong(ConfigEnum.NEXT_ROUTE.value()));
			ChunkDataVector.setUseOwners(ChunkUp.instance.getConfig().getBoolean(ConfigEnum.OWNERS.value()));
			Config.Permissions = ChunkUp.instance.getConfig().getBoolean(ConfigEnum.PERMISSIONS.value());
			Config.Op = ChunkUp.instance.getConfig().getBoolean(ConfigEnum.OP.value());
			
			ChunkUp.instance.getLogger().info("Configuration loaded successfully");
			
		if (!ChunkUp.getPlugin(ChunkUp.class).getDescription().getVersion().equals(ChunkUp.instance.getConfig().getString(ConfigEnum.VERSION.value()))) {
			
			ChunkUp.instance.getLogger().info("but you are using an old version of the config. I'll create a new one for you!");
			new File(ChunkUp.instance.getDataFolder(), CONFIGFILENAME).delete();
			
			load();
		}
	}
	
	public void loadChunks() {
		for (String key : ChunksFileConfig.getKeys(false)) {
			try {
				ChunkDataVector.add(new ChunkData(ChunksFileConfig.getString(key).split(";")));
			} catch (ArrayIndexOutOfBoundsException e) {
				ChunkUp.instance.getLogger().log(Level.SEVERE, "Set invalid key: " + key);
			} catch (NumberFormatException e) {
				ChunkUp.instance.getLogger().log(Level.SEVERE, "Set invalid key: " + key);
			} catch (NullPointerException e) {
				ChunkUp.instance.getLogger().log(Level.SEVERE, "Invalid key: " + key + "\nThis could also be an internal error.");
			}
		}
	}
	
	public void save() {
		writeConfig();
		writeChunks();
		ChunkUp.instance.getLogger().info("Saved");
	}

	public void writeConfig() {
		ChunkUp.instance.getLogger().info("Saving config...");
		ChunkUp.instance.getConfig().set(ConfigEnum.VERSION.value(), ChunkUp.getPlugin(ChunkUp.class).getDescription().getVersion());
		ChunkUp.instance.getConfig().set(ConfigEnum.USE_ALTERNATIVE_CHUNKLOADER.value(), ChunkUp.isUseAlternativeChunkLoader());
		ChunkUp.instance.getConfig().set(ConfigEnum.IGNORE_INTERVAL.value(), ChunkUpPlayer.getIgnoreCount());
		ChunkUp.instance.getConfig().set(ConfigEnum.REFRESH_IN_TICKS.value(), ChunkLoader.getRefreshTime());
		ChunkUp.instance.getConfig().set(ConfigEnum.NEXT_ID.value(), ChunkData.getNextID());
		ChunkUp.instance.getConfig().set(ConfigEnum.NEXT_ROUTE.value(), ChunkData.getNextRoute());
		ChunkUp.instance.getConfig().set(ConfigEnum.OWNERS.value(), ChunkDataVector.isUsingOwners());
		ChunkUp.instance.getConfig().set(ConfigEnum.PERMISSIONS.value(), Permissions);
		ChunkUp.instance.getConfig().set(ConfigEnum.OP.value(), Op);
		ChunkUp.instance.saveConfig();
	}
	
	public void writeChunks() {
		ChunkUp.instance.getLogger().info("Saving chunks...");
		for (int index = 0; index < ChunkDataVector.getChunkDataVector().size(); index++) {
			ChunksFileConfig.set(Integer.toString(index), ChunkDataVector.getChunkDataVector().get(index).toConfString());
		}

		try {
			ChunksFileConfig.save(ChunksFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
