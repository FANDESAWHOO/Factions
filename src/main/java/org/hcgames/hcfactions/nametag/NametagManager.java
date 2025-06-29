package org.hcgames.hcfactions.nametag;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.nametag.adapter.FactionsAdapter;
import org.hcgames.hcfactions.nametag.listener.NametagListener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class NametagManager {
	private final Map<UUID, Nametag> nametags;
	private final NametagAdapter adapter;

	public NametagManager() {
		nametags = new ConcurrentHashMap<>();
		adapter = new FactionsAdapter();

		Bukkit.getPluginManager().registerEvents(new NametagListener(), HCFactions.getInstance());
	}

	public void handleUpdate(Player viewer, Player target) {
		if (viewer == null || target == null) return; // Possibly?
		String prefix = getAdapter().getAndUpdate(viewer, target);
		updateLunarTags(viewer, target, prefix);
	}

	/**
	 * SOON
	 *
	 * @param viewer
	 * @param target
	 * @param prefix
	 */
	public void updateLunarTags(Player viewer, Player target, String prefix) {

	}

}
