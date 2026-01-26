package org.hcgames.hcfactions.command.subcommand.staff;


import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class FactionBanCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionBanCommand() {
		plugin = HCFactions.getInstance();
		//    this.permission = "hcf.command.faction.argument." + getName();
	}


	 @Command(name = "faction.ban", description = "Bans every member in this faction.",permission = "factions.command.ban", aliases = { "f.ban"}, usage = "/<command> <aliases> <faction>",  playerOnly = false, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 CommandSender sender = arg.getSender();
		if (arg.length() < 2) {
			sender.sendMessage(Lang.of("Commands.error.usage", "/f ban <faction>"));
			return;
		}

		plugin.getFactionManager().advancedSearch(arg.getArgs(0), PlayerFaction.class, new SearchCallback<PlayerFaction>() {

			@Override
			public void onSuccess(PlayerFaction faction) {
				String extraArgs = HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(arg.getArgs(), 2, arg.length()));
				ConsoleCommandSender console = plugin.getServer().getConsoleSender();

				for (UUID uuid : faction.getMembers().keySet()) {
					String commandLine = "ban " + uuid.toString() + " " + extraArgs;
					sender.sendMessage(Lang.of("Commands.staff.ban.executing", commandLine));
					console.getServer().dispatchCommand(sender, commandLine);
				}

				sender.sendMessage(Lang.of("Commands.staff.ban.executed", faction.getName()));
			}

			@Override
			public void onFail(FailReason reason) {
				sender.sendMessage(Lang.of("Commands.error.faction_not_found", arg.getArgs(0)));
			}
		});
		return;
	}

}
