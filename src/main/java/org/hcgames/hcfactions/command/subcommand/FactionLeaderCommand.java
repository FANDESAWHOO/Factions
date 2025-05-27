package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.UUID;

public class FactionLeaderCommand extends SimpleSubCommand {

	private final HCFactions plugin;

	public FactionLeaderCommand() {
		super("leader | setleader | newleader");
		setDescription("Sets the new leader for your faction.");
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
			sender.sendMessage(Lang.of("Commands-Factions-Leader-PlayerOnlyCMD"));
			return;
		}

		if (args.length < 2) {
			sender.sendMessage(Lang.of("Commands-Usage").replace("{usage}", getUsage(getLabel())));
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

		UUID uuid = player.getUniqueId();
		FactionMember selfMember = playerFaction.getMember(uuid);
		Role selfRole = selfMember.getRole();

		if (selfRole != Role.LEADER) {
			sender.sendMessage(Lang.of("Commands-Factions-Leader-LeaderRequired"));
			//sender.sendMessage(ChatColor.RED + "You must be the current faction leader to transfer the faction.");
			return;
		}

		FactionMember targetMember = playerFaction.findMember(args[1]);

		if (targetMember == null) {
			//sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' is not in your faction.");
			sender.sendMessage(Lang.of("Commands-Factions-Leader-PlayerNotInFaction")
					.replace("{name}", args[1]));
			return;
		}

		if (targetMember.getUniqueId().equals(uuid)) {
			//sender.sendMessage(ChatColor.RED + "You are already the faction leader.");
			sender.sendMessage(Lang.of("Commands-Factions-Leader-AlreadyLeader"));
			return;
		}

		targetMember.setRole(Role.LEADER);
		selfMember.setRole(Role.CAPTAIN);

		playerFaction.broadcast(Lang.of("Commands-Factions-Leader-LeaderTransferBroadcast")
				.replace("{previousLeaderName}", selfMember.getRole().getAstrix() + selfMember.getCachedName())
				.replace("{newLeaderName}", targetMember.getRole().getAstrix() + targetMember.getCachedName()));

		//ChatColor colour = plugin.getConfiguration().getRelationColourTeammate();
		//playerFaction.broadcast(colour + selfMember.getRole().getAstrix() + selfMember.getName() + ChatColor.YELLOW +
		//        " has transferred the faction to " + colour + targetMember.getRole().getAstrix() + targetMember.getName() + ChatColor.YELLOW + '.');

	}

    /*@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || (playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER)) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        Map<UUID, FactionMember> members = playerFaction.getMembers();
        for (Map.Entry<UUID, FactionMember> entry : members.entrySet()) {
            if (entry.getValue().getRole() != Role.LEADER) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(entry.getKey());
                String targetName = target.getName();
                if (targetName != null && !results.contains(targetName)) {
                    results.add(targetName);
                }
            }
        }

        return results;
    }*/
}
