package org.hcgames.hcfactions.util;

import net.minecraft.server.v1_8_R3.ChatHoverable.EnumHoverAction;

public enum HoverAction {
	SHOW_TEXT(EnumHoverAction.SHOW_TEXT), SHOW_ITEM(EnumHoverAction.SHOW_ITEM), SHOW_ACHIEVEMENT(EnumHoverAction.SHOW_ACHIEVEMENT);

	private EnumHoverAction hoverAction;

	private HoverAction(EnumHoverAction hoverAction) {
		this.hoverAction = hoverAction;
	}

	public EnumHoverAction getNMS() {
		return this.hoverAction;
	}
}