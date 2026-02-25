package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.manager.SearchCallback;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;



public class FactionSetDeathbanCommand extends FactionCommand {

	private final HCFactions plugin;
	
	public FactionSetDeathbanCommand() {
		plugin = HCFactions.getInstance();
	}	
	 @Command(name = "faction.setdeathban", description = "Set deathban boolean." ,permission = "factions.command.setdeathban", aliases = { "f.setdeathban"}, usage = "/<command>  setdeathban <name>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 CommandSender sender = arg.getSender();
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Usage: " + "/f setdeathban <name> <boolean>");
			return;
		}


		plugin.getFactionManager().advancedSearch(args[0], Faction.class, new SearchCallback<Faction>() {
			@Override
			public void onSuccess(Faction faction) {
				faction.setDeathban(Boolean.valueOf(args[1]));
			}

			@Override
			public void onFail(FailReason reason) {
				sender.sendMessage(Lang.of("Commands.error.faction_not_found", args[0]));
			}
		});

		return;
	 }
	
}
