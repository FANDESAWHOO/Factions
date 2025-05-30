package org.hcgames.hcfactions.api;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.enums.EconomyTypes;
import org.hcgames.hcfactions.api.events.EconomyEvent;
import org.mineacademy.fo.Common;

/**
 * BETA VERSION
 * MAYBE WE NEED TO USE
 * A INTERFACE CLASS
 * AND CHANGE ECONOMYOVERRIDE
 * TO A EconomyManaer INTERFACE
 */
public class EconomyAPI {
	public static String ECONOMY_SYMBOL = "$";
	public static boolean customEvents = !Configuration.api;
	public static Class economyOverride = null; // YOU MUST OVERRIDE THIS TO MAKE WORK
	
	public static void subtractBalance(Player id, int amount){
       if (customEvents)
		   Common.callEvent(new EconomyEvent(false ,id ,amount, EconomyTypes.subtract));
	   else HCFactions.getInstance().getEcon().withdrawPlayer(id, amount);
	}

	public static void addBalance(Player id, int amount) {
		if (customEvents)
			Common.callEvent(new EconomyEvent(false ,id ,amount, EconomyTypes.deposit));
		else HCFactions.getInstance().getEcon().depositPlayer(id, amount);
	}
	
	public static Double getBalance(Player player){
		if (customEvents) {
			if (economyOverride == null) throw new IllegalStateException("No custom economy provider set.");

			try {
				return (Double) economyOverride
						.getClass()
						.getMethod("getBalance", Player.class)
						.invoke(economyOverride, player);

			} catch (Exception e) {
				throw new RuntimeException("Failed to invoke custom getBalance", e);
			}
		} else return HCFactions.getInstance().getEcon().getBalance(player);
	}

}
