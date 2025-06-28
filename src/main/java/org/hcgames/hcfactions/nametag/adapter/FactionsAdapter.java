package org.hcgames.hcfactions.nametag.adapter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.FactionsAPI;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.nametag.NameVisibility;
import org.hcgames.hcfactions.nametag.Nametag;
import org.hcgames.hcfactions.nametag.NametagAdapter;


public class FactionsAdapter implements NametagAdapter {
	@Override
	public String getAndUpdate(Player player, Player target) {
		PlayerFaction pt = FactionsAPI.getPlayerFaction(player);
		return "";
	}

	private String createTeam(Player player, Player target, String name, String prefix, String color) {
		return createTeam(player, target, name, prefix, color, NameVisibility.ALWAYS);
	}

	// yes it needs color because 1.16 is stupid
	private String createTeam(Player player, Player target, String name, String prefix, String color, NameVisibility visibility) {
		Nametag nametag = HCFactions.getInstance().getNametagManager().getNametags().get(player.getUniqueId());
		String formattedPrefix = ChatColor.GOLD.toString();
	/*	String formattedPrefix = (prefix.isEmpty() ? "" : prefix
				.replace("%rank-color%", CC.translate(getInstance().getRankHook().getRankColor(target)))
				.replace("%rank-name%", CC.translate(getInstance().getRankHook().getRankName(target))));*/

		if (nametag != null) {
			nametag.getPacket().create(name, color, formattedPrefix, "", true, visibility);
			nametag.getPacket().addToTeam(target.getName(), name);
		}

		return formattedPrefix + color;
	}

}
