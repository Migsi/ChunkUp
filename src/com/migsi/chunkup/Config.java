package com.migsi.chunkup;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Config {
	// JSON converter
	private Gson gson = null;
	// ChunkUp instance
	private ChunkUp chunkUp = null;
	// Temporary ChunkDataVector
	private ChunkDataVector temp = null;

	// Determines which permission system gets used
	public static boolean Permissions = false;
	public static boolean Op = true;
	// Determines all configuration fields
	private static final String[] ConfigFields = {"version", "use-alternative-chunkloader", "ignore-interval", "refresh-in-ticks",
			"next-id", "next-route", "owners", "permissions", "op", "chunks" };

	public Config(ChunkUp chunkUp) {
		gson = new Gson();
		this.chunkUp = chunkUp;
	}

	public ChunkDataVector loadConfig() {
		try {
			ChunkUp.setUseAlternativeChunkLoader(chunkUp.getConfig().getBoolean(ConfigFields[1]));
			ChunkUpPlayer.setIgnoreCount(chunkUp.getConfig().getInt(ConfigFields[2]));
			ChunkLoader.setRefreshTime(chunkUp.getConfig().getInt(ConfigFields[3]));
			ChunkData.setNextID(chunkUp.getConfig().getLong(ConfigFields[4]));
			ChunkData.setNextRoute(chunkUp.getConfig().getLong(ConfigFields[5]));
			ChunkDataVector.setUseOwners(chunkUp.getConfig().getBoolean(ConfigFields[6]));
			Config.Permissions = chunkUp.getConfig().getBoolean(ConfigFields[7]);
			Config.Op = chunkUp.getConfig().getBoolean(ConfigFields[8]);
			temp = gson.fromJson(chunkUp.getConfig().getString(ConfigFields[ConfigFields.length - 1]),
					ChunkDataVector.class);
			chunkUp.getLogger().info("Configuration loaded successfully");
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		if (!ChunkUp.getPlugin(ChunkUp.class).getDescription().getVersion().equals(chunkUp.getConfig().getString(ConfigFields[0]))) {
			chunkUp.getLogger().info("but you are using an old version of the config. I'll create a new one for you!");
			new File(chunkUp.getDataFolder(), "config.yml").delete();
			readConfig();
		}
		return temp;
	}

	public ChunkDataVector readConfig() {
		try {
			if (!chunkUp.getDataFolder().exists()) {
				chunkUp.getDataFolder().mkdirs();
			}
			File file = new File(chunkUp.getDataFolder(), "config.yml");
			if (!file.exists()) {
				chunkUp.getLogger().info("Config.yml not found, creating a new one...");
				chunkUp.getConfig().options()
						.header("#IF YOU DON'T KNOW WHAT YOU'RE DOING, DON'T EDIT THIS FILE BY HAND");
				//chunkUp.getConfig().addDefault(ConfigFields[0], ChunkUp.getPlugin(ChunkUp.class).getDescription().getVersion());
				chunkUp.getConfig().addDefault(ConfigFields[1], true);
				chunkUp.getConfig().addDefault(ConfigFields[2], 10);
				chunkUp.getConfig().addDefault(ConfigFields[3], 50);
				chunkUp.getConfig().addDefault(ConfigFields[4], 0);
				chunkUp.getConfig().addDefault(ConfigFields[5], 0);
				chunkUp.getConfig().addDefault(ConfigFields[6], true);
				chunkUp.getConfig().addDefault(ConfigFields[7], true);
				chunkUp.getConfig().addDefault(ConfigFields[8], false);
				chunkUp.getConfig().addDefault(ConfigFields[ConfigFields.length - 1], "'{\"chdvector\":[]}'");
				chunkUp.getConfig().options().copyDefaults(true);
				chunkUp.saveConfig();
			} else {
				chunkUp.getLogger().info("Config.yml found, loading...");
				loadConfig();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	public void writeConfig(ChunkDataVector chdvector) {
		chunkUp.getLogger().info("Saving config...");
		chunkUp.getConfig().set(ConfigFields[0], ChunkUp.getPlugin(ChunkUp.class).getDescription().getVersion());
		chunkUp.getConfig().set(ConfigFields[1], ChunkUp.isUseAlternativeChunkLoader());
		chunkUp.getConfig().set(ConfigFields[2], ChunkUpPlayer.getIgnoreCount());
		chunkUp.getConfig().set(ConfigFields[3], ChunkLoader.getRefreshTime());
		chunkUp.getConfig().set(ConfigFields[4], ChunkData.getNextID());
		chunkUp.getConfig().set(ConfigFields[5], ChunkData.getNextRoute());
		chunkUp.getConfig().set(ConfigFields[6], ChunkDataVector.isUsingOwners());
		chunkUp.getConfig().set(ConfigFields[7], Permissions);
		chunkUp.getConfig().set(ConfigFields[8], Op);
		chunkUp.getConfig().set(ConfigFields[ConfigFields.length - 1], gson.toJson(chdvector, ChunkDataVector.class));
		chunkUp.saveConfig();
		chunkUp.getLogger().info("Saved");
	}

	public void resetTemp() {
		temp = null;
	}

	public static String[] getConfigFields() {
		return ConfigFields;
	}
}
