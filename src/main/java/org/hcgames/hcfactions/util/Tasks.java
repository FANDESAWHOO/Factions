package org.hcgames.hcfactions.util;

import org.bukkit.Bukkit;
import org.hcgames.hcfactions.HCFactions;

public class Tasks {

	public static void execute(HCFactions manager, Runnable runnable) {
		Bukkit.getServer().getScheduler().runTask(manager, runnable);
	}

}
