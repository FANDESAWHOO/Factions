package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.ChatChannel;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.*;

public final class FactionChatCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionChatCommand() {
		plugin = HCFactions.getInstance();
	}

	 @Command(name = "faction.chat", description = "Toggle faction chat only mode on or off.", aliases = { "f.c"}, usage = "/f [fac|public|ally] [message]",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 Player p = arg.getPlayer();
		 Player player = arg.getPlayer();
			PlayerFaction playerFaction;
			try {
				playerFaction = plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
			} catch (NoFactionFoundException e) {
				player.sendMessage(ChatColor.RED + "You are not in a faction.");
				return;
			}
			
			FactionMember member = playerFaction.getMember(player.getUniqueId());
			ChatChannel currentChannel = member.getChatChannel();
			ChatChannel parsed;
			
			if(arg.length() >= 1) {
				parsed = ChatChannel.parse(arg.getArgs(0), null);

				if (parsed != null && parsed == ChatChannel.OFFICER && member.getRole() == Role.MEMBER) {
					player.sendMessage(Lang.of("Commands-Factions-Chat-OfficerOnly"));
					return;
				}
			}else {
				parsed = currentChannel.getRotation();

				if (parsed == ChatChannel.OFFICER && member.getRole() == Role.MEMBER) parsed = currentChannel.getRotation();
			}
			if (parsed == null && currentChannel != ChatChannel.PUBLIC) {
				Collection<Player> recipients = playerFaction.getOnlinePlayers();
				if (currentChannel == ChatChannel.ALLIANCE)
					for (PlayerFaction ally : playerFaction.getAlliedFactions()) recipients.addAll(ally.getOnlinePlayers());

				String format = String.format(currentChannel.getRawFormat(player), "", HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(arg.getArgs(), 1, arg.length())));
				for (Player recipient : recipients) recipient.sendMessage(format);

				// spawn radius, border, allies, minigames,
				return;
			}

			ChatChannel newChannel = parsed == null ? currentChannel.getRotation() : parsed;

			if (newChannel == ChatChannel.OFFICER && member.getRole() == Role.MEMBER) newChannel = newChannel.getRotation();

			member.setChatChannel(newChannel);
			player.sendMessage(Lang.of("Commands-Factions-Chat-SwitchedMode")
					.replace("{newMode}", newChannel.getDisplayName().toLowerCase()));
	 }

}
