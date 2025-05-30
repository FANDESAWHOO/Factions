package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.manager.SearchCallback;
import org.hcgames.hcfactions.structure.ChatChannel;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class FactionAcceptCommand extends FactionSubCommand {

	private final HCFactions plugin;

	public FactionAcceptCommand() {
		super("accept|join|a");
		setAliases(new String[]{"make", "define"});
		setDescription("Accept a join request from an existing faction.");
		plugin = HCFactions.getInstance();

	}
    @Override
	public String getUsage() {
		return '/' + label + " " + getName()  + " <factionName>";
	}

	@Override
	public void onCommand() {
		checkConsole();

		if (args.length < 2) {
			tell(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
			return;
		}

		Player player = (Player) sender;

		try {
			if (plugin.getFactionManager().getPlayerFaction(player) != null) {
				tell(Lang.of("Commands-Factions-Accept-InFactionAlready"));
				return;
			}
		} catch (NoFactionFoundException e) {}

		plugin.getFactionManager().advancedSearch(args[1], PlayerFaction.class, new SearchCallback<PlayerFaction>() {
			@Override
			public void onSuccess(PlayerFaction faction) {
				if (faction.getMembers().size() >= Configuration.factionMaxMembers) {
					tell(Lang.of("Commands-Factions-Accept-FactionFull")
							.replace("{factionName}", faction.getFormattedName(sender))
							.replace("{factionPlayerLimits}", String.valueOf(Configuration.factionMaxMembers)));
					return;
				}

				if (!faction.isOpen() && !faction.getInvitedPlayerNames().contains(player.getName().toLowerCase())) {
					tell(Lang.of("Commands-Factions-Accept-NotInvited").replace("{factionName}", faction.getFormattedName(sender)));
					return;
				}

				if (faction.addMember(player, player, player.getUniqueId(), new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER), false))
					faction.broadcast(Lang.of("Commands-Factions-Accept-FactionJoinBroadcast").replace("{player}", sender.getName()));
			}

			@Override
			public void onFail(FailReason reason) {
				tell(Lang.of("commands.error.faction_not_found", args[1]));
			}
		});
	}

	@Override
	protected List<String> tabComplete() {
		if (args.length != 2 || !(sender instanceof Player)) return Collections.emptyList();

		return plugin.getFactionManager().getFactions().stream().
				filter(faction -> faction instanceof PlayerFaction && ((PlayerFaction) faction).getInvitedPlayerNames().contains(sender.getName())).
				map(faction -> sender.getName()).collect(Collectors.toList());
	}

}
