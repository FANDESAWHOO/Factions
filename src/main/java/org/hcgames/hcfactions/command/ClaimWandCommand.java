package org.hcgames.hcfactions.command;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.util.ItemCreator;
import org.hcgames.hcfactions.util.PlayerUtil;

import com.cryptomorin.xseries.XMaterial;
import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class ClaimWandCommand {

	private final ItemStack wand = ItemCreator.of(XMaterial.STICK.parseMaterial(), "&aClaim Selection", "Right click block = First point", "Left click block = Second point", "Shift + Left click = Confirm region", "Right click air = Cancel selection").make();

	public ClaimWandCommand() {
		HCFactions.getInstance().getCommandFramework().registerCommands(this);
	}

    @Command(name = "claimwand", description = "The main command for Claimwand", aliases = {"cwand"},usage = "/claimwand",  playerOnly = true, adminsOnly = false)
    public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		PlayerUtil.addItemsOrDrop(player, wand);

	}
}
