package org.hcgames.hcfactions.nametag.task;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;

import java.util.ConcurrentModificationException;

@SuppressWarnings("ALL")
public class NametagTask implements Runnable {


	@Override
	public void run() {
		try {

			for (Player viewer : Bukkit.getOnlinePlayers()) {
				// Tracked players doesn't contain the viewer
				HCFactions.getInstance().getNametagManager().handleUpdate(viewer, viewer);

				// When in staff tracked players are empty because you are hidden
				for (Player staff : viewer.spigot().getHiddenPlayers()) {
					if (staff == viewer) continue;
					HCFactions.getInstance().getNametagManager().handleUpdate(staff, viewer);
				}

				/*for (Player target : version.getTrackedPlayers(viewer)) {
					if (viewer == target) continue;
					getManager().handleUpdate(viewer, target);
				}*/
			}

		} catch (Exception e) {
			if (e instanceof ConcurrentModificationException) return;
			e.printStackTrace();
		}
	}
}