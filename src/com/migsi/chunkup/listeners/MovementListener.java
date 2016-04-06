package com.migsi.chunkup.listeners;

import java.util.Vector;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.migsi.chunkup.ChunkUp;
import com.migsi.chunkup.data.ChunkData;
import com.migsi.chunkup.data.ChunkDataVector;
import com.migsi.chunkup.data.ChunkUpPlayer;

public class MovementListener implements Listener {

	private Vector<ChunkUpPlayer> following = null;

	public MovementListener() {
		following = new Vector<ChunkUpPlayer>();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!following.isEmpty()) {
			ChunkUpPlayer cuplayer = new ChunkUpPlayer(event.getPlayer());
			if (cuplayer != null && !cuplayer.Ignore()) {
				if (automark(cuplayer)) {
					cuplayer.incrementChunkCount();
				}
			}
		}
	}

	public boolean add(ChunkUpPlayer cuplayer) {
		if (!contains(cuplayer)) {
			// if (description != null) {
			return following.add(cuplayer);
			// }
			// return following.add(new ChunkUpPlayer(player, mark));
		}
		return false;
	}

	public boolean remove(ChunkUpPlayer cuplayer) {
		if (cuplayer != null) {
			if (cuplayer.isMarking()) {
				ChunkUp.message(cuplayer.getChunkCount() + " chunks marked by " + cuplayer.getOfflinePlayer().getName());
			} else {
				ChunkUp.message(cuplayer.getChunkCount() + " chunks demarked by " + cuplayer.getOfflinePlayer().getName());
			}
			return following.remove(cuplayer);
		}
		return false;
	}

	/*public ChunkUpPlayer get(Player player) {
		for (int i = 0; i < following.size(); i++) {
			if (following.get(i).equals(player)) {
				return following.get(i);
			}
		}
		return null;
	}*/

	public boolean contains(ChunkUpPlayer player) {
		for (int i = 0; i < following.size(); i++) {
			if (following.get(i).equals(player)) {
				return true;
			}
		}
		return false;
	}

	private boolean automark(ChunkUpPlayer player) {
		if (player.isMarking()) {
			if (ChunkDataVector.add(new ChunkData(player.getOfflinePlayer().getPlayer(), player.getDescription(), player.getRoute()))) {
				ChunkData.addToMap(player);
				return true;
			}
			return false;
		}
		if (ChunkDataVector.remove(new ChunkData(player.getOfflinePlayer().getPlayer(), true))) {
			ChunkData.removeFromMap(player);
			return true;
		}
		return false;
	}
}
