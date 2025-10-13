package org.hcgames.hcfactions.event.playerfaction;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.hcgames.hcfactions.structure.Raidable;

@Getter
public class FactionDtrChangeEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final DtrUpdateCause cause;
	private final Raidable raidable;
	private final double originalDtr;
	private boolean cancelled;
	@Setter
	private double newDtr;

	public FactionDtrChangeEvent(@NonNull DtrUpdateCause cause, @NonNull Raidable raidable, @NonNull double originalDtr, @NonNull double newDtr) {
		this.cause = cause;
		this.raidable = raidable;
		this.originalDtr = originalDtr;
		this.newDtr = newDtr;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled || (Math.abs(newDtr - originalDtr) == 0);
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public enum DtrUpdateCause {
		REGENERATION, MEMBER_DEATH
	}
}
