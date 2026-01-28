package org.hcgames.hcfactions.faction.system;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.util.Mongoable;

public class FactionSystem extends ClaimableFaction implements ConfigurationSerializable, Mongoable {
	
	private final static UUID FACTION_UUID = UUID.randomUUID();
    private ChatColor color = ChatColor.BLUE; // for default.
    
	public FactionSystem(String name) {
		super(name, FACTION_UUID);
		displayName = color + name;
	}


	public static UUID getUUID() {
		return FACTION_UUID;
	}
}
