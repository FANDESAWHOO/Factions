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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class FactionForceLeaderCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionForceLeaderCommand() {
		plugin = HCFactions.getInstance();
		//   this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.forceleader", description = "Forces the leader of a faction.",permission = "factions.command.forceleader", aliases = { "f.forceleader"}, usage = "/<command>  forceleader <name>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		String[] args = arg.getArgs();
		Player player = arg.getPlayer();
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "Usage: " +"/<command>  forceleader <name>");
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

				if (member.getRole() == Role.LEADER) {
					player.sendMessage(ChatColor.RED + member.getCachedName() + " is already the leader of " + faction.getFormattedName(player) + ChatColor.RED + '.');
					return;
				}

				Optional<FactionMember> leader = faction.getLeader();
				String oldLeaderName = leader.isPresent() ? "none" : leader.get().getCachedName();
				String newLeaderName = member.getCachedName();

				// Demote the previous leader, promoting the new.
				if (leader.isPresent()) leader.get().setRole(Role.CAPTAIN);

				member.setRole(Role.LEADER);
				faction.broadcast(ChatColor.YELLOW + player.getName() + " has forcefully set the leader to " + newLeaderName + '.');

				player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Leader of " + faction.getName() + " was forcefully set from " + oldLeaderName + " to " + newLeaderName + '.');
			}

			@Override
			public void onFail(FailReason reason) {
				player.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});

		return;
	}

}
