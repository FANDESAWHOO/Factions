package org.hcgames.hcfactions.api;

import com.lunarclient.apollo.Apollo;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.api.enums.ClientTypes;

/**
 * Useful things for NameTags API
 * That will be implemented soon
 */
public final class ClientAPI {

	public static ClientTypes getClientType(Player player){
		if(isUsingLunar(player))
			return ClientTypes.LUNAR;
		return ClientTypes.DEFAULT;
	}

	/**
	 * Using Apollo API, Maybe we need
	 * To Create another method to
	 * Lunar Old API!
	 * @param player
	 * @return
	 */
	private static boolean isUsingLunar(Player player){
		return Apollo.getPlayerManager().hasSupport(player.getUniqueId());
	}

	/**
	 * I don't know if we gonna use this
	 * Maybe we gonna remove Badlion and
	 * CheatBreaker in ClientsTypes!
	 * @param player
	 * @return
	 */
	private static boolean isUsingBadlion(Player player){
		return false;
	}
}
