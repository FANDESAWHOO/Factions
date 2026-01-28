package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class FactionForcePromoteCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionForcePromoteCommand() {
		plugin = HCFactions.getInstance();
		// this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.forcepromote", description = "Forces the promotion status of a player.",permission = "factions.command.forcepromote", aliases = { "f.forcepromote"}, usage = "/<command>  forcepromote <name>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		String[] args = arg.getArgs();
		Player player = arg.getPlayer();
		if (args.length < 2) {
			player.sendMessage(ChatColor.RED + "Usage: " + "/<command>  forcepromote <name>");
			return;
		}

		plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
			@Override
			public void onSuccess(PlayerFaction faction) {
				FactionMember member = null;

				for (FactionMember search : faction.getMembers().values())
					if (search.getCachedName().equalsIgnoreCase(args[1])) {
						member = search;
						break;
					}

				if (member == null) {
					player.sendMessage(ChatColor.RED + "Faction containing member with IGN or UUID " + args[1] + " not found.");
					return;
				}

				if (member.getRole() != Role.MEMBER) {
					player.sendMessage(ChatColor.RED + member.getCachedName() + " is already a " + member.getRole().getName() + '.');
					return;
				}

				member.setRole(Role.CAPTAIN);
				faction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + player.getName() + " has been forcefully assigned as a captain.");
			}

			@Override
			public void onFail(FailReason reason) {
				player.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});
		return;
	}

}
