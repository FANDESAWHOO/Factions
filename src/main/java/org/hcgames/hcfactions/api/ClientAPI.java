package org.hcgames.hcfactions.api;

import com.google.common.collect.Lists;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.recipients.Recipients;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.api.enums.ClientTypes;

/**
 * Useful things for NameTags API
 * That will be implemented soon
 */
public final class ClientAPI {

	public static ClientTypes getClientType(Player player) {
		if (isUsingLunar(player))
			return ClientTypes.LUNAR;
		return ClientTypes.DEFAULT;
	}

	/**
	 * Using Apollo API, Maybe we need
	 * To Create another method to
	 * Lunar Old API!
	 *
	 * @param player
	 * @return
	 */
	private static boolean isUsingLunar(Player player) {
		return Apollo.getPlayerManager().hasSupport(player.getUniqueId());
	}

	/**
	 * I don't know if we gonna use this
	 * Maybe we gonna remove Badlion and
	 * CheatBreaker in ClientsTypes!
	 *
	 * @param player
	 * @return
	 */
	private static boolean isUsingBadlion(Player player) {
		return false;
	}

	public void overrideNametag(Player player, Component component) {
		if (isUsingLunar(player))
			overrideNametag(player, component);

	}

	public void overrideLunarTag(Player target, Component text) {
		Apollo.getModuleManager().getModule(NametagModule.class).overrideNametag(Recipients.ofEveryone(), target.getUniqueId(), Nametag.builder()
				.lines(Lists.newArrayList(text))
				.build()
		);
	}
}
