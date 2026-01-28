package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.system.FactionSystem;
import org.hcgames.hcfactions.util.JavaUtils;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public class FactionColorCommand extends FactionCommand {
	private final HCFactions plugin;

	public FactionColorCommand() {
		plugin = HCFactions.getInstance();
	}


	 @Command(name = "faction.color", description = "Change color for a system faction." ,permission = "factions.command.createsystem", aliases = { "f.color"}, usage = "/<command>  color <name>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 CommandSender sender = arg.getSender();
		if (args.length < 2) {
			sender.sendMessage(Lang.of("Command.error.usage", "/f color <name> <color>"));
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
		ChatColor color;
		try {
			color = ChatColor.valueOf(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Please provide a color avaiable, that is on ChatColor.class");
			return;
		}

		try {
			if (plugin.getFactionManager().getFaction(name) == null) {
				sender.sendMessage(Lang.of("Commands.error.faction_not_found")
						.replace("{factionName}", name));
				return;
			}
		} catch (NoFactionFoundException e) {
		}
		plugin.getFactionManager().getFaction(name).setDisplayName(color + name);
		return;
	}
	
}
