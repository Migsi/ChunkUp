package com.migsi.chunkup.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.migsi.chunkup.ChunkUp;
import com.migsi.chunkup.data.ChunkData;
import com.migsi.chunkup.data.ChunkDataVector;

public class ChunkLoader extends BukkitRunnable implements Listener {

	private static int RefreshTime = 50;

	public ChunkLoader() {
		if (!ChunkUp.isUseAlternativeChunkLoader()) {
			runTaskTimer(ChunkUp.getPlugin(ChunkUp.class), 0, RefreshTime);
		} else {
			loadChunks();
			ChunkUp.getPlugin(ChunkUp.class).getLogger().info("Loaded chunks.");
		}
	}

	@Override
	public void run() {
		loadChunks();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (ChunkDataVector.contains(new ChunkData(event.getChunk()))) {
			event.setCancelled(true);
			if (!event.getChunk().isLoaded()) {
				event.getChunk().load();
			}
		}
	}

	public void loadChunks() {
		ChunkData temp = null;
		for (int i = 0; i < ChunkDataVector.size(); i++) {
			temp = ChunkDataVector.get(i);
			try {
				if (!ChunkUp.getPlugin(ChunkUp.class).getServer().getWorld(temp.getWorld())
						.getChunkAt(temp.getX(), temp.getZ()).isLoaded()) {
					ChunkUp.getPlugin(ChunkUp.class).getServer().getWorld(temp.getWorld())
							.getChunkAt(temp.getX(), temp.getZ()).load();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setRefreshTime(int ticks) {
		if (ticks > -1) {
			RefreshTime = ticks;
		}
	}

	public static int getRefreshTime() {
		return RefreshTime;
	}

}
