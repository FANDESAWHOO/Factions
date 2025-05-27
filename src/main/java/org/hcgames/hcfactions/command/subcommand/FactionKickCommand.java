package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Optional;

public class FactionKickCommand extends SimpleSubCommand {

	private final HCFactions plugin;

	public FactionKickCommand() {
		super("kick | kickmember | kickplayer");
		setDescription("Kick a player from the faction.");
		plugin = HCFactions.getInstance();
		if(!FactionCommands.getArguments().contains(this))
			FactionCommands.getArguments().add(this);
	}


	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <playerName>";
	}

	@Override
	public void onCommand() {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Lang.of("Commands-Factions-Kick-PlayerOnlyCMD"));
			//sender.sendMessage(ChatColor.RED + "Only players can kick from a faction.");
			return;
		}

		if (args.length < 2) {
			sender.sendMessage(Lang.of("Commands-Usage").replace("{usage}", getUsage(getLabel())));
			return;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction = null;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		if (playerFaction.isRaidable() && !Configuration.kitMap) { //  && !HCF.getPlugin().getEotwHandler().isEndOfTheWorld()
			sender.sendMessage(Lang.of("Commands-Factions-Kick-Raidable"));
			//sender.sendMessage(ChatColor.RED + "You cannot kick players whilst your faction is raidable.");
			return;
		}

		FactionMember targetMember = playerFaction.findMember(args[1]);

		if (targetMember == null) {
			sender.sendMessage(Lang.of("Commands-Factions-Kick-NoMemberNamed")
					.replace("{name}", args[1]));
			//sender.sendMessage(ChatColor.RED + "Your faction does not have a member named '" + args[1] + "'.");
			return;
		}

		Role selfRole = playerFaction.getMember(player.getUniqueId()).getRole();

		if (selfRole == Role.MEMBER) {
			sender.sendMessage(Lang.of("Commands-Factions-Kick-OfficerRequired"));
			//sender.sendMessage(ChatColor.RED + "You must be a faction officer to kick members.");
			return;
		}

		Role targetRole = targetMember.getRole();

		if (targetRole == Role.LEADER) {
			sender.sendMessage(Lang.of("Commands-Factions-Kick-CannotKickLeader"));
			//sender.sendMessage(ChatColor.RED + "You cannot kick the faction leader.");
			return;
		}

		if ((targetRole == Role.CAPTAIN || targetRole == Role.COLEADER) && selfRole == Role.CAPTAIN) {
			sender.sendMessage(Lang.of("Commands-Factions-Kick-CoLeaderRequired"));
			//sender.sendMessage(ChatColor.RED + "You must be a faction leader to kick captains.");
			return;
		}

		if(targetRole == Role.COLEADER && selfRole == Role.COLEADER){
			sender.sendMessage(Lang.of("Commands-Factions-Kick-LeaderRequired"));
			return;
		}

		Optional<Player> onlineTarget = targetMember.toOnlinePlayer();
		if (playerFaction.removeMember(sender, onlineTarget.orElse(null), targetMember.getUniqueId(), true, true)) {
			//onlineTarget.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You were kicked from the faction by " + sender.getName() + '.');
			if (onlineTarget.isPresent()) onlineTarget.get().sendMessage(Lang.of("Commands-Factions-Kick-Kicked")
					.replace("{sender}", sender.getName()));

			playerFaction.broadcast(Lang.of("Commands-Factions-Kick-KickedBroadcast")
					.replace("{player}", targetMember.getCachedName())
					.replace("{sender}", playerFaction.getMember(player).getRole().getAstrix() + sender.getName()));
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
