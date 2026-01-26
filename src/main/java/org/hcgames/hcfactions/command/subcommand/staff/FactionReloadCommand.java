package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.command.FactionCommand;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


/**
 * Why SystemUpdate removed this?
 * Other thing i don't gonna know.
 */
public final class FactionReloadCommand extends FactionCommand {


	public FactionReloadCommand() {
		//  this.permission = "hcf.command.faction.argument." + getName();
	}


	 @Command(name = "faction.reload", description = "Reloads the messages file from .",permission = "factions.command.mute", aliases = { "f.reload"}, usage = "/<command>  reload",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 Player player = arg.getPlayer();
		//plugin.getMessages().reload();
		player.sendMessage(ChatColor.RED + "Command disabled.");
		return;
	}
}
