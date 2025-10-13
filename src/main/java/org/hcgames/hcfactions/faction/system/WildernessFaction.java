package org.hcgames.hcfactions.faction.system;

import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.faction.Faction;

import java.util.Map;
import java.util.UUID;

public class WildernessFaction extends Faction implements SystemFaction {

	private final static UUID FACTION_UUID = UUID.fromString("ef1d2c32-a234-4fd8-b116-299221c1ec92");

	public WildernessFaction() {
		super("Wilderness", FACTION_UUID);
	}

	public WildernessFaction(Map<String, Object> map) {
		super(map);
	}

	public WildernessFaction(Document document) {
		super(document);
	}

	public static UUID getUUID() {
		return FACTION_UUID;
	}

	@Override
	public String getFormattedName(CommandSender sender) {
		return Configuration.relationColourWilderness + getName();
	}
}
