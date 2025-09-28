package org.hcgames.hcfactions.command;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

/**
 * Used to handle give the player
 * The wand of ClaimWand!
 */
@AutoRegister
public final class ClaimWandCommand extends SimpleCommand {

	/**
	 * The singleton of this class
	 */
	@Getter
	private final static ClaimWandCommand instance = new ClaimWandCommand();
	private final ItemStack wand = ItemCreator.of(CompMaterial.STICK, "&aClaim Selection", "Right click block = First point", "Left click block = Second point", "Shift + Left click = Confirm region", "Right click air = Cancel selection").make();

	private ClaimWandCommand() {
		super("claimwand|cwand");
	}

	@Override
	protected void onCommand() {
		checkConsole();
		Player player = (Player) sender;
		PlayerUtil.addItemsOrDrop(player, wand);

	}
}
