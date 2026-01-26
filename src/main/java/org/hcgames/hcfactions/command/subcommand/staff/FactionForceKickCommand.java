package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.Collections;
import java.util.List;

public final class FactionForceKickCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionForceKickCommand() {
		plugin = HCFactions.getInstance();
		//this.permission = "hcf.command.faction.argument." + getName();
	}

	 @Command(name = "faction.forcekick", description = "Forcefully kick a player from their faction.",permission = "factions.command.forcekick", aliases = { "f.forcekick"}, usage = "/<command>  forcekick <name>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 Player player = arg.getPlayer();
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "Usage: " + "/<command>  forcekick <name>");
			return;
		}

		plugin.getFactionManager().advancedSearch(args[0], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
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
					player.sendMessage(ChatColor.RED + "You cannot forcefully kick faction leaders. Use /f forceremove instead.");
					return;
				}

				if (faction.removeMember(player, null, member.getUniqueId(), true, true))
					faction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + member.getCachedName() + " has been forcefully kicked by " + player.getName() + '.');
			}

			@Override
			public void onFail(FailReason reason) {
				player.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
			}
		});
		return;
	}

}
