package org.hcgames.hcfactions.command.subcommand.staff;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.ChatChannel;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Faction argument used to forcefully join {@link Faction}s.
 */
public final class FactionForceJoinCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionForceJoinCommand() {

		plugin = HCFactions.getInstance();
		// this.permission = "hcf.command.faction.argument." + getName();
	}


	 @Command(name = "faction.forcejoin", description = "Forcefully join a faction." ,permission = "factions.command.forcejoin", aliases = { "f.forcejoin"}, usage = "/<command>  forcejoin <name>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 String[] args = arg.getArgs();
		 Player player = arg.getPlayer();
		if (args.length < 1) {
			player.sendMessage(ChatColor.RED + "Usage: " + "/f  forcejoin <name>");
			return;
		}



		try {
			plugin.getFactionManager().getPlayerFaction(player);
			player.sendMessage(ChatColor.RED + "You are already in a faction.");
		} catch (NoFactionFoundException e) {
			plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
				@Override
				public void onSuccess(PlayerFaction faction) {
					if (faction.addMember(player, player, player.getUniqueId(), new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER), true))
						faction.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + player.getName() + " has forcefully joined the faction.");
				}

				@Override
				public void onFail(FailReason reason) {
					player.sendMessage(Lang.of("Commands.error.faction_not_found", args[1]));
				}
			});
		}
		return;
	}

}
