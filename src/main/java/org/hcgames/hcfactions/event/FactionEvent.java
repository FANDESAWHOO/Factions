package org.hcgames.hcfactions.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.event.Event;
import org.hcgames.hcfactions.faction.Faction;

@Getter
@Setter
public abstract class FactionEvent<T extends Faction> extends Event {

	private final T faction;

	public FactionEvent(@NonNull T faction) {
		this.faction = faction;
	}

	public FactionEvent(@NonNull T faction, boolean async) {
		super(async);
		this.faction = faction;
	}

}
