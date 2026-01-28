package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.ChatChannel;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


public final class FactionAcceptCommand extends FactionCommand {

	private final HCFactions plugin;

	public FactionAcceptCommand() {
		plugin = HCFactions.getInstance();

	}
	 @Command(name = "faction.join", description = "Accept a join request from an existing faction.", aliases = { "f.accept","f.a","f.join" ,"fac.join", "team.join", "t.join"}, usage = "/<command> <aliases> <faction>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 Player player = arg.getPlayer();
		 if (arg.getArgs().length < 1) {
			 player.sendMessage(ChatColor.BLUE + "/" + arg.getLabel() + " accept <faction>");
			 return;
		 }
			try {
				if (plugin.getFactionManager().getPlayerFaction(player) != null) {
					player.sendMessage(Lang.of("Commands-Factions-Accept-InFactionAlready"));
					return;
				}
			} catch (NoFactionFoundException e) {
			}

			plugin.getFactionManager().advancedSearch(arg.getArgs(0), PlayerFaction.class, new SearchCallback<PlayerFaction>() {
				@Override
				public void onSuccess(PlayerFaction faction) {
					if (faction.getMembers().size() >= Configuration.factionMaxMembers) {
						player.sendMessage(Lang.of("Commands-Factions-Accept-FactionFull")
								.replace("{factionName}", faction.getFormattedName(player))
								.replace("{factionPlayerLimits}", String.valueOf(Configuration.factionMaxMembers)));
						return;
					}

					if (!faction.isOpen() && !faction.getInvitedPlayerNames().contains(player.getName().toLowerCase())) {
						player.sendMessage(Lang.of("Commands-Factions-Accept-NotInvited").replace("{factionName}", faction.getFormattedName(player)));
						return;
					}

					if (faction.addMember(player, player, player.getUniqueId(), new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER), false))
						faction.broadcast(Lang.of("Commands-Factions-Accept-FactionJoinBroadcast").replace("{player}", player.getName()));
				}

				@Override
				public void onFail(FailReason reason) {
					player.sendMessage(Lang.of("commands.error.faction_not_found", arg.getArgs(0)));
				}
			});
	 }

}
