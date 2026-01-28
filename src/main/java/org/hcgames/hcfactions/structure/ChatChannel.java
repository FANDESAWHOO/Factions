package org.hcgames.hcfactions.structure;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.Lang;

import java.util.Locale;


public enum ChatChannel {

	FACTION("Faction"), OFFICER("Officer"), ALLIANCE("Alliance"), PUBLIC("Public");

	private final String name;

	ChatChannel(String name) {
		this.name = name;
	}

	/**
	 * Parse an {@link ChatChannel} from a String.
	 *
	 * @param id  the id to search
	 * @param def the default {@link ChatChannel} if null
	 * @return the {@link ChatChannel} by name
	 */
	public static ChatChannel parse(String id, ChatChannel def) {
		id = id.toLowerCase(Locale.ENGLISH);
		switch (id) {
			case "f":
			case "faction":
			case "fc":
			case "fac":
			case "fact":
				return ChatChannel.FACTION;
			case "a":
			case "alliance":
			case "ally":
			case "ac":
				return ChatChannel.ALLIANCE;
			case "p":
			case "pc":
			case "g":
			case "gc":
			case "global":
			case "pub":
			case "publi":
			case "public":
				return ChatChannel.PUBLIC;
			case "officer":
			case "o":
			case "offic":
			case "c":
			case "captain":
				return ChatChannel.OFFICER;
			default:
				return def == null ? null : def.getRotation();
		}
	}

	/**
	 * Gets the name of this {@link ChatChannel}.
	 *
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	public final String getDisplayName() {
		String prefix;

		switch (this) {
			case FACTION:
			case OFFICER:
				prefix = Configuration.relationColourTeammate.toString();
				break;
			case ALLIANCE:
				prefix = Configuration.relationColourAlly.toString();
				break;
			case PUBLIC:
			default:
				prefix = Configuration.relationColourEnemy.toString();
				break;
		}

		return prefix + name;
	}

	/**
	 * Gets the next {@link ChatChannel} from the current.
	 *
	 * @return the next rotation value
	 */
	public final ChatChannel getRotation() {
		switch (this) {
			case FACTION:
				return OFFICER;
			case OFFICER:
				return Configuration.factionMaxAllies > 0 ? ALLIANCE : PUBLIC;
			case ALLIANCE:
				return PUBLIC;
			case PUBLIC:
				return FACTION;
			default:
				return PUBLIC;
		}
	}

	public final String getRawFormat(Player player) {
		ChatColor relationColour;

		switch (this) {
			case FACTION:
			case OFFICER:
			case ALLIANCE:
				relationColour = Configuration.relationColourTeammate;
				break;
			default:
				throw new IllegalArgumentException("Cannot get the relation colour for public chat channel (" + this.name + ")");
		}

		return Lang.of("factions.chat")
				.replace("{relationColour}", relationColour.toString())
				.replace("{channelName}", getDisplayName())
				.replace("{player}", player.getName())
				.replace("{message}", "%2$s");
	}
}
