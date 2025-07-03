package org.hcgames.hcfactions.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.hcgames.hcfactions.api.enums.TimerTypes;
import org.hcgames.hcfactions.faction.PlayerFaction;

@AllArgsConstructor
@Getter
@Setter
public class FactionTimerEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final Player player;
	private final PlayerFaction faction;
	private final TimerTypes timer;
	private boolean cancelled;

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
