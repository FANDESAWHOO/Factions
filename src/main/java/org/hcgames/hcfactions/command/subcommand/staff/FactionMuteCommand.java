package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class FactionMuteCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionMuteCommand() {
		plugin = HCFactions.getInstance();
		//    this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.mute", description = "Mutes every member in this faction.",permission = "factions.command.mute", aliases = { "f.mute"}, usage = "/<command>  mute <factionName> <time:(e.g. 1h2s)> <reason>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 Player player = arg.getPlayer();
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "Usage: " + "/<command>  mute <factionName> <time:(e.g. 1h2s)> <reason>");
			return;
		}

		plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<org.hcgames.hcfactions.faction.PlayerFaction>() {

			@Override
			public void onSuccess(org.hcgames.hcfactions.faction.PlayerFaction faction) {
				String extraArgs = HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(args, 2, args.length));
				ConsoleCommandSender console = Bukkit.getConsoleSender();
				for (UUID uuid : faction.getMembers().keySet()) {
					String commandLine = "mute " + uuid.toString() + " " + extraArgs;
					player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Executing " + ChatColor.RED + commandLine);
					console.getServer().dispatchCommand(player, commandLine);
				}

				player.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Executed mute action on faction " + faction.getName() + ".");
			}

			@Override
			public void onFail(FailReason reason) {
				player.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});

		return;
	}

}
