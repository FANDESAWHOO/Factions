package org.hcgames.hcfactions.command.subcommand.staff;


import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.util.JavaUtils;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


public final class FactionCreateSystemCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionCreateSystemCommand() {
		plugin = HCFactions.getInstance();
		//  this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.createsystem", description = "Create a system faction." ,permission = "factions.command.createsystem", aliases = { "f.createsystem"}, usage = "/<command>  createsystem <name>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 CommandSender sender = arg.getSender();
		if (args.length < 1) {
			sender.sendMessage(Lang.of("Command.error.usage", "/f createsystem"));
			return;
		}
		String name = args[0];
		int value = Configuration.factionNameMinCharacters;

		if (name.length() < value) {
			sender.sendMessage(Lang.of("Commands-Factions-Create-MinimumChars")
					.replace("{minChars}", String.valueOf(value)));
			return;
		}

		value = Configuration.factionNameMaxCharacters;

		if (name.length() > value) {
			sender.sendMessage(Lang.of("Commands-Factions-Create-MaximumChars")
					.replace("{maxChars}", String.valueOf(value)));
			return;
		}

		if (!JavaUtils.isAlphanumeric(name)) {
			sender.sendMessage(Lang.of("Commands-Factions-Create-MustBeAlphanumeric"));
			return;
		}

		try {
			if (plugin.getFactionManager().getFaction(name) != null) {
				sender.sendMessage(Lang.of("Commands-Factions-Create-NameAlreadyExists")
						.replace("{factionName}", name));
				return;
			}
		} catch (NoFactionFoundException e) {
		}
		//plugin.getFactionManager().createFaction(new SystemTeam(name), sender);
		return;
	}

}
