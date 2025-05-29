package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.*;

public class FactionDemoteCommand extends FactionSubCommand {


	private final HCFactions plugin;

	public FactionDemoteCommand() {
		super("demote | uncaptain | delcaptain | delofficer");
		setDescription("Demotes a player to a member.");
		plugin = HCFactions.getInstance();
	}

    @Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " <playerName>";
	}

	@Override
	public void onCommand() {
		checkConsole();

		if (args.length < 2) {
			sender.sendMessage(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
			return;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		//if (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
		//    sender.sendMessage(plugin.getMessagesOld().getString("Commands-Factions-Demote-OfficerRequired"));
		//    return;
		//}

		FactionMember targetMember = playerFaction.findMember(args[1]);

		if (targetMember == null) {
			sender.sendMessage(Lang.of("Commands-Factions-Demote-PlayerNotInFaction"));
			return;
		}


		Role currentRole = playerFaction.getMember(player.getUniqueId()).getRole();
		Role targetRole = targetMember.getRole();


		if(currentRole == Role.MEMBER || currentRole == Role.CAPTAIN && targetRole == Role.CAPTAIN){
			sender.sendMessage(Lang.of("Commands-Factions-Demote-LeaderRequired"));
			return;
		}

		if(targetRole == Role.MEMBER){
			sender.sendMessage(Lang.of("Commands-Factions-Demote-LowestRank").replace("{player}", targetMember.getCachedName()));
			return;
		}

		if(targetRole == Role.LEADER){
			sender.sendMessage(Lang.of("Comamnds-Factions-Demote-LeaderDemote"));
			return;
		}

		if(!(currentRole == Role.LEADER) && targetRole == Role.COLEADER){
			sender.sendMessage(Lang.of("Commands-Factions-Demote-LeaderRequired"));
			return;
		}

		if(!(currentRole == Role.COLEADER || currentRole == Role.LEADER) && targetRole != Role.CAPTAIN){
			sender.sendMessage(Lang.of("Commands-Factions-Demote-CoLeaderRequired"));
			return;
		}

		targetMember.setRole(targetRole == Role.COLEADER ? Role.CAPTAIN : Role.MEMBER);
		playerFaction.broadcast(Lang.of("Commands-Factions-Demote-Success").replace("{player}",
						targetMember.getCachedName()).replace("{newRole}", targetMember.getRole().getName())
				.replace("{oldRole}", targetRole.getName()));

	}

	// FIXME: 29/09/2016 (27-5-2025 ¿ Error where ?
    @Override
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
    }

}
