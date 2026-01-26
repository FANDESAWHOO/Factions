package org.hcgames.hcfactions.command.subcommand.staff;

import net.md_5.bungee.api.ChatColor;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

/**
 * This works and Reload no
 * Why? 18:20 28/5/2025
 */

public final class FactionSaveCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionSaveCommand() {
		plugin = HCFactions.getInstance();
		// this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.save", description = "Save to database the factions :)", permission = "factions.command.remove", aliases = { "f.save"}, usage = "/<command> save",  playerOnly = false, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		arg.getSender().sendMessage(ChatColor.RED + "Saved succesfully.");
		plugin.getFactionManager().saveFactionData();
		return;
	}

}
