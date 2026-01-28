package org.hcgames.hcfactions.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public final class Common {

	public static void callEvent(Event event) {
		Bukkit.getPluginManager().callEvent(event);
	}
	
}
