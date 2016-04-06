package com.migsi.chunkup.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.bukkit.ChatColor;

public class ChunkDataVector {

	private static Vector<ChunkData> ChunkDataVector = null;

	private static boolean useOwners = true;

	public ChunkDataVector() {
		if (ChunkDataVector == null) {
			ChunkDataVector = new Vector<ChunkData>();
		}
	}

	public static boolean add(ChunkData chdata) {
		if (!contains(chdata)) {
			return ChunkDataVector.add(chdata);
		} else if (!get(chdata).isOwner(chdata.getMainOwner())) {
			return get(chdata).addOwner(chdata.getMainOwner());
		}
		return false;
	}

	public static boolean remove(ChunkData chdata) {
		if (get(chdata) != null) {
			if (!useOwners || get(chdata).isOnlyOwner(chdata.getMainOwner())) {
				get(chdata).removeOwner(chdata.getMainOwner());
				return ChunkDataVector.remove(chdata);
			} else {
				return get(chdata).removeOwner(chdata.getMainOwner());
			}
		}
		return false;
	}

	public static boolean clear(ChunkUpPlayer owner) {
		boolean ret = false;
		// Sinnvoll auch wenn owner nicht aktiv?
		//if (useOwners) {
			int i = 0;
			while (i < ChunkDataVector.size()) {
				if (ChunkDataVector.get(i).isOnlyOwner(owner)) {
					ChunkDataVector.remove(i);
					ret = true;
				} else {
					ret = ChunkDataVector.get(i).removeOwner(owner);
					i++;
				}
				ChunkData.removeFromMap(owner);
			}
		//}
		return ret;
	}

	public static void clearAll() {
		ChunkData.clearOwnerMap();
		ChunkDataVector.clear();
	}

	public static ChunkData get(int index) {
		return ChunkDataVector.get(index);
	}

	public static ChunkData get(ChunkData chdata) {
		int index = index(chdata);
		if (index > -1) {
			return ChunkDataVector.get(index);
		}
		return null;
	}

	public static int size() {
		return ChunkDataVector.size();
	}

	public static boolean contains(ChunkData chdata) {
		return ChunkDataVector.contains(chdata);
	}

	public static int index(ChunkData chdata) {
		return ChunkDataVector.indexOf(chdata);
	}

	public static boolean isUsingOwners() {
		return useOwners;
	}

	public static void setUseOwners(boolean allowOwners) {
		useOwners = allowOwners;
	}

	public static Vector<ChunkData> getChunkDataVector() {
		return ChunkDataVector;
	}

	public static List<String> getOwners() {
		List<String> ret = null;
		if (!ChunkDataVector.isEmpty()) {
			ret = new ArrayList<>();
			for (int i = 0; i < ChunkDataVector.size(); i++) {
				ChunkDataVector.get(i).getOwners();
			}

		}

		return ret;
	}

	public static String list() {
		String ret = null;
		if (ChunkDataVector != null && !ChunkDataVector.isEmpty()) {
			ret = new String();
			int i;
			for (i = 0; i < ChunkDataVector.size(); i++) {
				ret += ChatColor.GREEN + "----------------------------\n" + ChatColor.RESET
						+ ChunkDataVector.get(i).toString() + "\n";
			}
			ret += ChatColor.GREEN + "----------------------------\n" + ChatColor.RESET + ChatColor.BOLD + "Total of "
					+ ChatColor.RESET + ChatColor.GRAY + i + ChatColor.RESET + ChatColor.BOLD + " chunks marked."
					+ ChatColor.RESET;
		}
		return ret;
	}
}
