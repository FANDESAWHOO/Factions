package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.util.JavaUtils;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


public final class FactionForceRenameCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionForceRenameCommand() {
		plugin = HCFactions.getInstance();
		//    this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.forcename", description = "Forces a rename of a faction.",permission = "factions.command.forcename", aliases = { "f.forcename"}, usage = "/<command>  forcename <oldName> <newName>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
        String[] args = arg.getArgs();
        Player player = arg.getPlayer();
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "Usage: " + "/<command>  forcename <oldName> <newName>");
			return;
		}

		String newName = args[2];

		if (Configuration.factionDisallowedNames.contains(newName)) {
			//tell(ChatColor.RED + "'" + newName + "' is a blocked faction name.");
			player.sendMessage(Lang.of("Commands-Factions-Rename-BlockedName")
					.replace("{factionName}", newName));
			return;
		}

		int value = Configuration.factionNameMinCharacters;

		if (newName.length() < value) {
			//player.sendMessage(ChatColor.RED + "Faction names must have at least " + value + " characters.");
			player.sendMessage(Lang.of("Commands-Factions-Rename-MinimumChars")
					.replace("{minChars}", String.valueOf(value)));
			return;
		}

		value = Configuration.factionNameMaxCharacters;

		if (newName.length() > value) {
			//player.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + value + " characters.");
			player.sendMessage(Lang.of("Commands-Factions-Rename-MaximumChars")
					.replace("{maxChars}", String.valueOf(value)));
			return;
		}

		if (!JavaUtils.isAlphanumeric(newName)) {
			//player.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
			player.sendMessage(Lang.of("Commands-Factions-Rename-MustBeAlphanumeric"));
			return;
		}

		try {
			if (plugin.getFactionManager().getFaction(newName) != null) {
				//player.sendMessage(ChatColor.RED + "Faction " + newName + ChatColor.RED + " already exists.");
				player.sendMessage(Lang.of("Commands-Factions-Rename-NameAlreadyExists")
						.replace("{factionNewName}", newName));
				return;
			}
		} catch (NoFactionFoundException ignored) {
		}


		plugin.getFactionManager().advancedSearch(args[1], Faction.class, new SearchCallback<Faction>() {
			@Override
			public void onSuccess(Faction faction) {
				String oldName = faction.getName();
				if (faction.setName(newName, player))
					BukkitCommand.broadcastCommandMessage(player, ChatColor.YELLOW + "Renamed " + oldName + " to " + faction.getName(), true);
			}

			@Override
			public void onFail(FailReason reason) {
				player.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});

		return;
	}

}
