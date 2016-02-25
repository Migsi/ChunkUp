package com.migsi.chunkup;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChunkLoader extends BukkitRunnable implements Listener {

	private ChunkDataVector chdvector = null;

	private static int RefreshTime = 50;

	public ChunkLoader(ChunkDataVector chdvector) {
		if (chdvector != null) {
			this.chdvector = chdvector;
		} else {
			this.chdvector = new ChunkDataVector();
		}
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

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (chdvector.contains(new ChunkData(event.getChunk()))) {
			event.setCancelled(true);
			if (!event.getChunk().isLoaded()) {
				event.getChunk().load();
			}
		}
	}

	public void loadChunks() {
		ChunkData temp = null;
		for (int i = 0; i < chdvector.size(); i++) {
			temp = chdvector.get(i);
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

	public boolean add(ChunkData chdata) {
		return chdvector.add(chdata);
	}

	public boolean remove(ChunkData chdata) {
		return chdvector.remove(chdata);
	}

	public boolean clear(String owner) {
		return chdvector.clear(owner);
	}

	public ChunkDataVector getChunkDataVector() {
		return chdvector;
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
