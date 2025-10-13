package org.hcgames.hcfactions.faction.system;

import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.faction.Faction;

import java.util.Map;
import java.util.UUID;

public class WarzoneFaction extends Faction implements SystemFaction {

	private final static UUID FACTION_UUID = UUID.fromString("f067e071-86d0-41c7-8c4b-f1a1cf15867e");

	public WarzoneFaction() {
		super("Warzone", FACTION_UUID);
	}

	public WarzoneFaction(Map<String, Object> map) {
		super(map);
	}

	public WarzoneFaction(Document document) {
		super(document);
	}

	public static UUID getUUID() {
		return FACTION_UUID;
	}

	@Override
	public String getFormattedName(CommandSender sender) {
		return Configuration.relationColourWarzone + getName();
	}
}
