package org.hcgames.hcfactions.api;

import lombok.Setter;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.enums.EconomyTypes;
import org.hcgames.hcfactions.api.events.EconomyEvent;
import org.hcgames.hcfactions.api.interfaces.IEconomy;
import org.mineacademy.fo.Common;

/**
 * STABLE VERSION
 * CHANGED REFLECTION METHOD
 * TO IEconomy INTERFACE
 * :)
 */
public final class EconomyAPI {
	public static final String ECONOMY_SYMBOL = "$";
	private static final boolean API = Configuration.api;
	private static final boolean customEvents = Configuration.customEvents;
	@Setter
	private static IEconomy economy = null; // YOU MUST OVERRIDE THIS TO MAKE WORK

	public static void subtractBalance(Player id, int amount) {
		if (!API) {
			if (customEvents) {
				Common.callEvent(new EconomyEvent(false, id, amount, EconomyTypes.subtract));
				return;
			}
			if (economy != null) economy.subtractBalance(id.getUniqueId(), amount);
		} else HCFactions.getInstance().getEcon().withdrawPlayer(id, amount);
	}

	public static void addBalance(Player id, int amount) {
		if (!API) {
			if (customEvents) {
				Common.callEvent(new EconomyEvent(false, id, amount, EconomyTypes.deposit));
				return;
			}
			if (economy != null) economy.addBalance(id.getUniqueId(), amount);
		} else
			HCFactions.getInstance().getEcon().depositPlayer(id, amount);
	}

	public static Double getBalance(Player player) {
		if (!API) {
			if (customEvents) Common.callEvent(new EconomyEvent(false, player, 0, EconomyTypes.balance));
			if (economy != null) return economy.getBalance(player.getUniqueId());
		}
		return HCFactions.getInstance().getEcon().getBalance(player);
	}

}
