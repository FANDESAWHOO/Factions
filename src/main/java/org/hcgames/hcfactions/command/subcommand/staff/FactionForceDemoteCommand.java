package org.hcgames.hcfactions.command.subcommand.staff;


import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.Collections;
import java.util.List;

public final class FactionForceDemoteCommand extends FactionSubCommand {

	private final HCFactions plugin;

	public FactionForceDemoteCommand() {
		super("forcedemote");
		setDescription("Forces the demotion status of a player.");
		plugin = HCFactions.getInstance();
		//   this.permission = "hcf.command.faction.argument." + getName();
	}


	@Override
	public String getUsage() {
		return Lang.of("Commands.staff.forcedemote.usage", label, getName());
	}

	@Override
	public void onCommand() {
		if (args.length < 2) {
			Lang.of("Commands.error.usage", getUsage());
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
					tell(Lang.of("Commands.error.member_not_found", args[1]));
					return;
				}

				if (member.getRole() == Role.LEADER) {
					tell(Lang.of("Command.staff.forcedemote.leader_demote", member.getCachedName()));
					return;
				}

				if (member.getRole() == Role.LEADER) {
					tell(Lang.of("Command.staff.forcedemote.user_demote", member.getCachedName()));
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
				tell(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});
		return;
	}

	@Override
	public List<String> tabComplete() {
		return args.length == 2 ? null : Collections.emptyList();
	}
}
