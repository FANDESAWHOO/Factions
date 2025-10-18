package org.hcgames.hcfactions.manager;

import com.google.common.collect.ImmutableList;
import org.hcgames.hcfactions.faction.system.SystemFaction;

import java.util.ArrayList;
import java.util.List;

public class SystemFactions {

	private List<Class<? extends SystemFaction>> factions = new ArrayList<>();

	SystemFactions() {
	}

	public void registerSystemFaction(Class<? extends SystemFaction> clazz) {
		factions.add(clazz);
	}

	public List<Class<? extends SystemFaction>> getSystemFactions() {
		return ImmutableList.copyOf(factions);
	}
}
