package org.hcgames.hcfactions.event.faction;

import lombok.NonNull;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.hcgames.hcfactions.event.FactionEvent;
import org.hcgames.hcfactions.faction.Faction;

public class FactionRaidableEvent extends FactionEvent<Faction> implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	public FactionRaidableEvent(@NonNull Faction faction, boolean async) {
		super(faction, async);
		cancelled = false;
	}

	public FactionRaidableEvent(@NonNull Faction faction) {
		this(faction,false);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
