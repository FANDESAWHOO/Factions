package org.hcgames.hcfactions.nametag;

import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * Credits to Azurite
 * (Keano) USING PacketPlayOutScoreboardTeam
 */
@Getter
public abstract class NametagPacket {

	protected final Player player;

	public NametagPacket(Player player) {
		this.player = player;
	}

	public abstract void create(String name, String color, String prefix, String suffix, boolean friendlyInvis, NameVisibility visibility);

	public abstract void addToTeam(String target, String team);

	public abstract void delete();
}