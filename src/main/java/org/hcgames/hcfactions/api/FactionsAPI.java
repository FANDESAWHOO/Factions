package org.hcgames.hcfactions.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.structure.Relation;

/**
 * BETA
 *
 */
public final class FactionsAPI {

	public static Faction getFactionAt(Player location) {
		return HCFactions.getInstance().getFactionManager().getFactionAt(location.getLocation());
	}
	public static Faction getFactionAt(Location location) {
		return HCFactions.getInstance().getFactionManager().getFactionAt(location);
	}

	 public static Faction getFaction(String name) {
		 return HCFactions.getInstance().getFactionManager().getFaction(name);
	 }

	 public static boolean isDeathban(Faction faction){
		 return faction.isDeathban();
	 }

	 public static Relation getRelation(Faction faction, Player player){
		return faction.getRelation(player);
	 }

}
