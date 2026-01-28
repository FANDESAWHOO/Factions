package org.hcgames.hcfactions.command.subcommand;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.util.JavaUtils;


public final class FactionHelpCommand extends FactionCommand {

	 @Command(name = "faction.help", description = "View help on how to use factions.", aliases = { "f.help"}, usage = "/f help",  playerOnly = true, adminsOnly = false)
		public void onCommand(CommandArgs arg) {
		 CommandSender sender = arg.getSender();
		if (arg.length() < 1) {
			showPage(sender,  1);
			return;
		}

		Integer page = JavaUtils.tryParseInt(arg.getArgs(0));

		if (page == null) {
			sender.sendMessage(Lang.of("Commands-Invalid-Number")
					.replace("{number}", arg.getArgs(0)));
			return;
		}

		showPage(sender, page);
	}


}
