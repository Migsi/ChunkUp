package com.migsi.chunkup;

import java.util.Vector;

public class ChunkDataVector {
	private static boolean useOwners = true;

	private Vector<ChunkData> chdvector = null;

	public ChunkDataVector() {
		chdvector = new Vector<ChunkData>();
	}

	public void orderByWorld() {
		// TODO
	}

	public void orderByRoute() {
		// TODO
	}

	public boolean add(ChunkData chdata) {
		if (!contains(chdata)) {
			return chdvector.add(chdata);
		} else if (!get(chdata).isOwner(chdata.getMainOwner())) {
			return get(chdata).addOwner(chdata.getMainOwner());
		}
		return false;
	}

	public boolean remove(ChunkData chdata) {
		if (get(chdata) != null) {
			if (!useOwners || get(chdata).isOnlyOwner(chdata.getMainOwner())) {
				return chdvector.remove(chdata);
			} else {
				return get(chdata).removeOwner(chdata.getMainOwner());
			}
		}
		return false;
	}

	public boolean clear(String owner) {
		boolean ret = false;
		if (useOwners && !owner.toLowerCase().equals("all")) {
			int i = 0;
			while (i < chdvector.size()) {
				if (chdvector.get(i).isOnlyOwner(owner)) {
					chdvector.remove(i);
					ret = true;
				} else {
					if (chdvector.get(i).removeOwner(owner)) {
						ret = true;
					}
					i++;
				}
			}
		} else {
			chdvector.clear();
			ret = true;
		}
		return ret;
	}

	public ChunkData get(int index) {
		return chdvector.get(index);
	}

	public ChunkData get(ChunkData chdata) {
		int index = index(chdata);
		if (index > -1) {
			return chdvector.get(index);
		}
		return null;
	}

	public int size() {
		return chdvector.size();
	}

	public boolean contains(ChunkData chdata) {
		return chdvector.contains(chdata);
	}

	public int index(ChunkData chdata) {
		return chdvector.indexOf(chdata);
	}

	public String list() {
		String ret = null;
		if (chdvector.size() != 0) {
			ret = new String();
			for (int i = 0; i < chdvector.size(); i++) {
				ret += chdvector.get(i).toString() + "\n";
			}
			ret.substring(1, ret.length() - 1);
		}
		return ret;
	}

	public static boolean isUsingOwners() {
		return useOwners;
	}

	public static void setUseOwners(boolean allowOwners) {
		useOwners = allowOwners;
	}
}
