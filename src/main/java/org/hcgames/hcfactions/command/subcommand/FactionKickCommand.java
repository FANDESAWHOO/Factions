package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.hcgames.hcfactions.util.text.CC;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.Optional;

public final class FactionKickCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionKickCommand() {
		plugin = HCFactions.getInstance();
	}


	@Command(name = "faction.kick", description = "Kick a player from the faction.", aliases = { "f.kick"}, usage = "/f kick <memberName>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		if (arg.length() < 1) {
			player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f kick <memberName>"));
			return;
		}

		PlayerFaction playerFaction = null;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		if (playerFaction.isRaidable() && !Configuration.kitMap) { //  && !HCF.getPlugin().getEotwHandler().isEndOfTheWorld()
			player.sendMessage(Lang.of("Commands-Factions-Kick-Raidable"));
			//player.sendMessage(ChatColor.RED + "You cannot kick players whilst your faction is raidable.");
			return;
		}

		FactionMember targetMember = playerFaction.findMember(arg.getArgs(0));

		if (targetMember == null) {
			player.sendMessage(Lang.of("Commands-Factions-Kick-NoMemberNamed")
					.replace("{name}", arg.getArgs(0)));
			//player.sendMessage(ChatColor.RED + "Your faction does not have a member named '" + args[1] + "'.");
			return;
		}

		Role selfRole = playerFaction.getMember(player.getUniqueId()).getRole();

		if (selfRole == Role.MEMBER) {
			player.sendMessage(Lang.of("Commands-Factions-Kick-OfficerRequired"));
			//player.sendMessage(ChatColor.RED + "You must be a faction officer to kick members.");
			return;
		}

		Role targetRole = targetMember.getRole();

		if (targetRole == Role.LEADER) {
			player.sendMessage(Lang.of("Commands-Factions-Kick-CannotKickLeader"));
			//player.sendMessage(ChatColor.RED + "You cannot kick the faction leader.");
			return;
		}

		if ((targetRole == Role.CAPTAIN || targetRole == Role.COLEADER) && selfRole == Role.CAPTAIN) {
			player.sendMessage(Lang.of("Commands-Factions-Kick-CoLeaderRequired"));
			//player.sendMessage(ChatColor.RED + "You must be a faction leader to kick captains.");
			return;
		}

		if (targetRole == Role.COLEADER && selfRole == Role.COLEADER) {
			player.sendMessage(Lang.of("Commands-Factions-Kick-LeaderRequired"));
			return;
		}

		Optional<Player> onlineTarget = targetMember.toOnlinePlayer();
		if (playerFaction.removeMember(player, onlineTarget.orElse(null), targetMember.getUniqueId(), true, true)) {
			//onlineTarget.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You were kicked from the faction by " + sender.getName() + '.');
			if (onlineTarget.isPresent())
				onlineTarget.get().sendMessage(CC.translate(Lang.of("Commands-Factions-Kick-Kicked")
						.replace("{sender}", player.getName())));

			playerFaction.broadcast(CC.translate(Lang.of("Commands-Factions-Kick-KickedBroadcast")
					.replace("{player}", targetMember.getCachedName())
					.replace("{sender}", playerFaction.getMember(player).getRole().getAstrix() + player.getName())));
			//playerFaction.broadcast(plugin.getConfiguration().getRelationColourEnemy() + targetMember.getName() + ChatColor.YELLOW + " has been kicked by " +
			//        plugin.getConfiguration().getRelationColourTeammate() + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + '.');
		}

		return;
	}

    /*@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            return Collections.emptyList();
        }

        Role memberRole = playerFaction.getMember(player.getUniqueId()).getRole();
        if (memberRole == Role.MEMBER) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        for (UUID entry : playerFaction.getMembers().keySet()) {
            Role targetRole = playerFaction.getMember(entry).getRole();
            if (targetRole == Role.LEADER || (targetRole == Role.CAPTAIN && memberRole != Role.LEADER)) {
                continue;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
            String targetName = target.getName();
            if (targetName != null && !results.contains(targetName)) {
                results.add(targetName);
            }
        }

        return results;
    }*/
}
