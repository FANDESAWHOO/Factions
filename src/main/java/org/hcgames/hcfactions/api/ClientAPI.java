package org.hcgames.hcfactions.api;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.api.enums.ClientTypes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Useful things for NameTags API
 * That will be implemented soon
 */
public final class ClientAPI { // TODO, WE CAN USE EVENTS AND IS MORE EASY.

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

	public void overrideNametags(Player viewer, Player target, List<String> format) {
		if (isUsingLunar(viewer))
			overrideNametag(target, viewer, format);

	}

	public void overrideNametag(Player target, Player viewer, List<String> tag) {
		Apollo.getPlayerManager()
				.getPlayer(viewer.getUniqueId())
				.ifPresent(apolloTarget -> (Apollo.getModuleManager().getModule(NametagModule.class)).overrideNametag(apolloTarget, target.getUniqueId(), Nametag.builder().lines(tag.stream().map(Component::text).collect(Collectors.toList())).build()));
	}
}
