package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;


import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.*;

public final class FactionDemoteCommand extends FactionCommand {


	private final HCFactions plugin;

	public FactionDemoteCommand() {
		plugin = HCFactions.getInstance();
	}

	 @Command(name = "faction.demote", description = "Demotes a player to a member.", aliases = { "f.demote","f.uncaptain","f.delcaptain","faction.uncaptain","faction.delofficer"}, usage = "/f demote [playerName]",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
			Player player = arg.getPlayer();
		 if (arg.length() < 1) {
				player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f demote [playerName]"));
				return;
			}

			PlayerFaction playerFaction;
			try {
				playerFaction = plugin.getFactionManager().getPlayerFaction(player);
			} catch (NoFactionFoundException e) {
				player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
				return;
			}

			//if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
			//    player.sendMessage(plugin.getMessagesOld().getString("Commands-Factions-Demote-OfficerRequired"));
			//    return;
			//}

			FactionMember targetMember = playerFaction.findMember(arg.getArgs(0));

			if (targetMember == null) {
				player.sendMessage(Lang.of("Commands-Factions-Demote-PlayerNotInFaction"));
				return;
			}


			Role currentRole = playerFaction.getMember(player.getUniqueId()).getRole();
			Role targetRole = targetMember.getRole();


			if (currentRole == Role.MEMBER || currentRole == Role.CAPTAIN && targetRole == Role.CAPTAIN) {
				player.sendMessage(Lang.of("Commands-Factions-Demote-LeaderRequired"));
				return;
			}

			if (targetRole == Role.MEMBER) {
				player.sendMessage(Lang.of("Commands-Factions-Demote-LowestRank").replace("{player}", targetMember.getCachedName()));
				return;
			}

			if (targetRole == Role.LEADER) {
				player.sendMessage(Lang.of("Comamnds-Factions-Demote-LeaderDemote"));
				return;
			}

			if (!(currentRole == Role.LEADER) && targetRole == Role.COLEADER) {
				player.sendMessage(Lang.of("Commands-Factions-Demote-LeaderRequired"));
				return;
			}

			if (!(currentRole == Role.COLEADER || currentRole == Role.LEADER) && targetRole != Role.CAPTAIN) {
				player.sendMessage(Lang.of("Commands-Factions-Demote-CoLeaderRequired"));
				return;
			}

			targetMember.setRole(targetRole == Role.COLEADER ? Role.CAPTAIN : Role.MEMBER);
			playerFaction.broadcast(Lang.of("Commands-Factions-Demote-Success").replace("{player}",
							targetMember.getCachedName()).replace("{newRole}", targetMember.getRole().getName())
					.replace("{oldRole}", targetRole.getName()));

	 }


	// FIXME: 29/09/2016 (27-5-2025 ¿ Error where ?
	/*@Override
	protected List<String> tabComplete() {
		if (args.length != 2 || !(sender instanceof Player)) return Collections.emptyList();

		Player player = (Player) sender;
		PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		if (playerFaction == null || (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER))
			return Collections.emptyList();

		List<String> results = new ArrayList<>();
		Collection<UUID> keySet = playerFaction.getMembers().keySet();
		for (UUID entry : keySet) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
			String targetName = target.getName();
			if (targetName != null && playerFaction.getMember(target.getUniqueId()).getRole() == Role.CAPTAIN)
				results.add(targetName);
		}

		return results;
	}*/

}
