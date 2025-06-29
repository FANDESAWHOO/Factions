package org.hcgames.hcfactions.focus;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.hcgames.hcfactions.event.playerfaction.PlayerFactionFocusEvent;
import org.hcgames.hcfactions.event.playerfaction.PlayerFactionUnfocusEvent;
import org.hcgames.hcfactions.faction.PlayerFaction;

/**
 * Will be used soon
 * For now is only to remember this
 */
public class FocusListener implements Listener {

	@EventHandler
	public void onFocus(PlayerFactionFocusEvent event){
		PlayerFaction faction = event.getFaction();
		FocusTarget focused = event.getTarget();
	}

	@EventHandler
	public void onUnFocus(PlayerFactionUnfocusEvent event){
		PlayerFaction faction = event.getFaction();
		FocusTarget focused = event.getTarget();
	}

}
