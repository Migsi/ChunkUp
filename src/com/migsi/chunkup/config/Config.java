package com.migsi.chunkup.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.migsi.chunkup.ChunkUp;
import com.migsi.chunkup.commands.Permissions;
import com.migsi.chunkup.data.ChunkData;
import com.migsi.chunkup.data.ChunkDataVector;
import com.migsi.chunkup.data.ChunkUpPlayer;
import com.migsi.chunkup.listeners.ChunkLoader;

public class Config {

	// File configurations
	private static final String CONFIGFILENAME = "config.yml";
	private static final String CHUNKSFILENAME = "chunks.yml";
	private static File ConfigFile = null;
	private static File ChunksFile = null;
	private static FileConfiguration ChunksFileConfig = null;
	private static boolean ConfigLoaded = false;
	private static boolean ChunksLoaded = false;

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
			if (!ChunksLoaded) {
				if (!ChunksFile.exists()) {
					ChunkUp.instance.getLogger().info("Chunks.yml not found, creating a new one...");
					writeChunks();

				} else {
					ChunkUp.instance.getLogger().info("Chunks.yml found, loading...");
					loadChunks();
				}
				ChunksLoaded = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadConfig() {

		if (!ConfigLoaded) {
			ChunkUp.setUseAlternativeChunkLoader(
					ChunkUp.instance.getConfig().getBoolean(ConfigSection.USE_ALTERNATIVE_CHUNKLOADER));
			ChunkUpPlayer.setIgnoreCount(ChunkUp.instance.getConfig().getInt(ConfigSection.IGNORE_INTERVAL));
			ChunkLoader.setRefreshTime(ChunkUp.instance.getConfig().getInt(ConfigSection.REFRESH_IN_TICKS));
			ChunkData.setNextID(ChunkUp.instance.getConfig().getLong(ConfigSection.NEXT_ID));
			ChunkData.setNextRoute(ChunkUp.instance.getConfig().getLong(ConfigSection.NEXT_ROUTE));
			ChunkDataVector.setUseOwners(ChunkUp.instance.getConfig().getBoolean(ConfigSection.OWNERS));
			Permissions.Permissions = ChunkUp.instance.getConfig().getBoolean(ConfigSection.PERMISSIONS);
			Permissions.Op = ChunkUp.instance.getConfig().getBoolean(ConfigSection.OP);

			ChunkUp.instance.getLogger().info("Configuration loaded successfully");

			if (!ChunkUp.getPlugin(ChunkUp.class).getDescription().getVersion()
					.equals(ChunkUp.instance.getConfig().getString(ConfigSection.VERSION))) {

				ChunkUp.instance.getLogger()
						.info("but you are using an old version of the config. I'll create a new one for you!");
				ConfigFile.delete();

				load();
			}
		}
		ConfigLoaded = true;
	}

	public void loadChunks() {
		for (String key : ChunksFileConfig.getKeys(false)) {
			try {
				ChunkDataVector.add(new ChunkData(ChunksFileConfig.getString(key).split(";")));
			} catch (ArrayIndexOutOfBoundsException e) {
				ChunkUp.instance.getLogger().log(Level.SEVERE, "Found invalid key: " + key);
			} catch (NumberFormatException e) {
				ChunkUp.instance.getLogger().log(Level.SEVERE, "Found invalid key: " + key);
			} catch (NullPointerException e) {
				ChunkUp.instance.getLogger().log(Level.SEVERE,
						"Found invalid key: " + key + "\nThis could also be an internal error. Please send the following stacktrace to the developer.");
				e.printStackTrace();
			}
		}

	}

	public void reload() {
		ConfigLoaded = false;
		ChunksLoaded = false;
		load();
	}

	public void save() {
		writeConfig();
		writeChunks();
		ChunkUp.instance.getLogger().info("Saved");
	}

	public void writeConfig() {
		ChunkUp.instance.getLogger().info("Saving config...");
		ChunkUp.instance.getConfig().set(ConfigSection.VERSION,
				ChunkUp.getPlugin(ChunkUp.class).getDescription().getVersion());
		ChunkUp.instance.getConfig().set(ConfigSection.USE_ALTERNATIVE_CHUNKLOADER,
				ChunkUp.isUseAlternativeChunkLoader());
		ChunkUp.instance.getConfig().set(ConfigSection.IGNORE_INTERVAL, ChunkUpPlayer.getIgnoreCount());
		ChunkUp.instance.getConfig().set(ConfigSection.REFRESH_IN_TICKS, ChunkLoader.getRefreshTime());
		ChunkUp.instance.getConfig().set(ConfigSection.NEXT_ID, ChunkData.getNextID());
		ChunkUp.instance.getConfig().set(ConfigSection.NEXT_ROUTE, ChunkData.getNextRouteNoInc());
		ChunkUp.instance.getConfig().set(ConfigSection.OWNERS, ChunkDataVector.isUsingOwners());
		ChunkUp.instance.getConfig().set(ConfigSection.PERMISSIONS, Permissions.Permissions);
		ChunkUp.instance.getConfig().set(ConfigSection.OP, Permissions.Op);
		ChunkUp.instance.saveConfig();
	}

	public void writeChunks() {
		ChunkUp.instance.getLogger().info("Saving chunks...");

		ChunksFileConfig = new YamlConfiguration();

		for (int index = 0; index < ChunkDataVector.getChunkDataVector().size(); index++) {
			ChunksFileConfig.set(Integer.toString(index),
					ChunkDataVector.getChunkDataVector().get(index).toConfString());
		}

		try {
			ChunksFileConfig.save(ChunksFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
