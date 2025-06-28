package org.hcgames.hcfactions.nametag;

import lombok.Getter;

/**
 * Will be used right this
 * But soon will be reformed
 */
@Getter
public enum NameVisibility {
	ALWAYS("always"),
	NEVER("never"),
	HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
	HIDE_FOR_OWN_TEAM("hideForOwnTeam");

	private final String name;

	NameVisibility(String name) {
		this.name = name;
	}

}
