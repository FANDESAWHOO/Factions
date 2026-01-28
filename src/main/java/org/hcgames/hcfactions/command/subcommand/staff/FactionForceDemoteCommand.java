package org.hcgames.hcfactions.command.subcommand.staff;


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

public final class FactionForceDemoteCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionForceDemoteCommand() {
		plugin = HCFactions.getInstance();
		//   this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.forcedemote", description = "Forces the demotion status of a player." ,permission = "factions.command.forcedemote", aliases = { "f.forcedemote"}, usage = "/<command>  forcedemote <name>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 Player player = arg.getPlayer();
		if (args.length < 1) {
			Lang.of("Commands.error.usage", "/f  forcedemote <name>");
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
					player.sendMessage(Lang.of("Commands.error.member_not_found", args[1]));
					return;
				}

				if (member.getRole() == Role.LEADER) {
					player.sendMessage(Lang.of("Command.staff.forcedemote.leader_demote", member.getCachedName()));
					return;
				}

				if (member.getRole() == Role.LEADER) {
					player.sendMessage(Lang.of("Command.staff.forcedemote.user_demote", member.getCachedName()));
					return;
				}

				Role newRole;

				if (member.getRole() == Role.COLEADER) newRole = Role.CAPTAIN;
				else //Should never happen
					if (member.getRole() == Role.CAPTAIN) newRole = Role.MEMBER;
					else newRole = Role.MEMBER;

				member.setRole(newRole);
				faction.broadcast(Lang.of("Commands.staff.forcedemote.demote_broadcast", member.getCachedName(), newRole.getName()));
			}

			@Override
			public void onFail(FailReason reason) {
				player.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});
		return;
	}

}
