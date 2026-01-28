package org.hcgames.hcfactions.command.subcommand;


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

import java.util.Set;

public final class FactionUninviteCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionUninviteCommand() {
		plugin = HCFactions.getInstance();
	}

	@Command(name = "faction.uninvite", description = "Revoke an invitation to a player.", aliases = {"faction.deinvite","f.deinvite","faction.deinv","f.deinv","f.uninv","faction.uninv","faction.revoke","f.revoke","f.uninvite"}, usage = "/f uninvite <all|playerName>",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player player = arg.getPlayer();
		if (arg.length() < 1) {
			player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f uninvite <all|playerName>"));
			return;
		}
		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}
		FactionMember factionMember = playerFaction.getMember(player);

		if (factionMember.getRole() == Role.MEMBER) {
			player.sendMessage(Lang.of("Commands-Factions-Uninvite-OfficerRequired"));
			//player.sendMessage(ChatColor.RED + "You must be a faction officer to un-invite players.");
			return;
		}

		Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();

		if (arg.getArgs(0).equalsIgnoreCase("all")) {
			invitedPlayerNames.clear();
			player.sendMessage(Lang.of("Commands-Factions-Uninvite-ClearedAll"));
			//player.sendMessage(ChatColor.YELLOW + "You have cleared all pending invitations.");
			return;
		}

		if (!invitedPlayerNames.remove(arg.getArgs(0))) {
			player.sendMessage(Lang.of("Commands-Factions-Uninvite-NoPendingInvites")
					.replace("{name}", arg.getArgs(0)));
			//tell(ChatColor.RED + "There is not a pending invitation for " + args[1] + '.');
			return;
		}

		playerFaction.broadcast(Lang.of("Commands-Factions-Uninvite-Broadcast")
				.replace("{player}", factionMember.getRole().getAstrix() + player.getName())
				.replace("{name}", arg.getArgs(0)));
		//playerFaction.broadcast(ChatColor.YELLOW + factionMember.getRole().getAstrix() + sender.getName() + " has uninvited " +
		//        plugin.getConfiguration().getRelationColourEnemy() + args[1] + ChatColor.YELLOW + " from the faction.");

		return;
	}

    /*@Override SYSTEM UPDATE WHY YOU REMOVE THIS CODE????????
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>(COMPLETIONS);
        results.addAll(playerFaction.getInvitedPlayerNames());
        return results;
    }

    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");*/
}
