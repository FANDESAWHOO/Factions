package org.hcgames.hcfactions.command.subcommand;

import com.google.common.base.Joiner;
import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class FactionInvitesCommand extends FactionCommand {
	private static final Joiner JOINER = Joiner.on(ChatColor.WHITE + ", " + ChatColor.GRAY);

	private final HCFactions plugin;

	public FactionInvitesCommand() {
		plugin = HCFactions.getInstance();
	}



	@Command(name = "faction.invites", description = "View faction invitations.", aliases = { "f.invites"}, usage = "/f invites",  playerOnly = true, adminsOnly = false)
	 public void onCommand(CommandArgs arg) {
		Player sender = arg.getPlayer();
		List<String> receivedInvites = new ArrayList<>();
		for (Faction faction : plugin.getFactionManager().getFactions())
			if (faction instanceof PlayerFaction) {
				PlayerFaction targetPlayerFaction = (PlayerFaction) faction;
				if (targetPlayerFaction.getInvitedPlayerNames().contains(sender.getName()))
					receivedInvites.add(targetPlayerFaction.getFormattedName(sender));
			}

		try {
			PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction((Player) sender);
			Set<String> sentInvites = playerFaction.getInvitedPlayerNames();
			sender.sendMessage(Lang.of("Commands-Factions-Invites-SentBy")
					.replace("{factionName}", playerFaction.getFormattedName(sender))
					.replace("{inviteCount}", String.valueOf(sentInvites.size()))
					.replace("{invites}", (sentInvites.isEmpty() ? Lang.of("Commands-Factions-SentByNoInvites-SentBy") : JOINER.join(sentInvites))));
		} catch (NoFactionFoundException e) {
		}


		sender.sendMessage(Lang.of("Commands-Factions-Invites-Requested")
				.replace("{inviteCount}", String.valueOf(receivedInvites.size()))
				.replace("{invites}", (receivedInvites.isEmpty() ? Lang.of("Commands-Factions-Invites-RequestedNoInvites") : JOINER.join(receivedInvites))));
		//tell(ChatColor.AQUA + "Requested (" + receivedInvites.size() + ')' + ChatColor.DARK_AQUA + ": " +
		//        ChatColor.GRAY + (receivedInvites.isEmpty() ? "No factions have invited you." : JOINER.join(receivedInvites) + '.'));

	}
}
