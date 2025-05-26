package org.hcgames.hcfactions.util;

import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;

public enum ClickAction {
	
	OPEN_URL(EnumClickAction.OPEN_URL), OPEN_FILE(EnumClickAction.OPEN_FILE), RUN_COMMAND(EnumClickAction.RUN_COMMAND), SUGGEST_COMMAND(EnumClickAction.SUGGEST_COMMAND);

	private EnumClickAction clickAction;

	private ClickAction(EnumClickAction action) {
		this.clickAction = action;
	}

	public EnumClickAction getNMS() {
		return this.clickAction;
	}
}