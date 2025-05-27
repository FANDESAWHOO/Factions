package org.hcgames.hcfactions.api;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.event.timer.HomeEvent;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.mineacademy.fo.Common;

/**
 * This is to
 * Check if we need to
 * Use our Timers API
 * Or call Event to let
 * Other Plugins works.
 */
public final class TimerAPI {

	private static final boolean useEvents = true;
	private static final HCFactions plugin = HCFactions.getInstance();
    // SOON, ONLY NEED THIS RN FOR MAKE HOME COMMAND WORKS.
	public static void callHome(Player player, PlayerFaction faction){
      if (useEvents)
		  Common.callEvent(new HomeEvent(false,player,faction));
	//  else new TimerTemplate(plugin).startTeleport(player); SOON.
	}

}
