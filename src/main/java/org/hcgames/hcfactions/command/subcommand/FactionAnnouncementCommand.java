package org.hcgames.hcfactions.command.subcommand;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FactionAnnouncementCommand extends SimpleSubCommand {
	private final HCFactions plugin;

	public FactionAnnouncementCommand() {
		super("announcement | motd | announce");
		setDescription("Set your faction announcement.");
		plugin = HCFactions.getInstance();
		if(!FactionCommands.getArguments().contains(this))
			FactionCommands.getArguments().add(this);
	}


	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <newAnnouncement>";
	}

	@Override
	public void onCommand() {
		checkConsole();

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

		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			sender.sendMessage(Lang.of("Commands-Factions-Announcement-OfficerRequired"));
			return;
		}

		Optional<String> oldAnnouncement = playerFaction.getAnnouncement();
		String newAnnouncement;
		if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("remove"))
			newAnnouncement = null;
		else newAnnouncement = HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(args, 1, args.length));

		if (!oldAnnouncement.isPresent() && newAnnouncement == null) {
			sender.sendMessage(Lang.of("Commands-Factions-Announcement-AlreadyUnset"));
			return;
		}

		if (oldAnnouncement.isPresent() && newAnnouncement != null && oldAnnouncement.get().equals(newAnnouncement)) {
			sender.sendMessage(Lang.of("Commands-Factions-Announcement-SameAnnouncement")
					.replace("%currentAnnouncement%", newAnnouncement));
			return;
		}

		playerFaction.setAnnouncement(newAnnouncement);

		if (newAnnouncement == null) {
			playerFaction.broadcast(Lang.of("Commands-Factions-Announcement-AnnouncementCleared")
					.replace("{player}", sender.getName()));
			return;
		}

		playerFaction.broadcast(Lang.of("Commands-Factions-Announcement-BroadcastSwitched")
				.replace("{player}", player.getName())
				.replace("{oldAnnouncement}", (oldAnnouncement.isPresent() ? oldAnnouncement.get() : "none"))
				.replace("{newAnnouncement}", newAnnouncement));
		return;
	}

	@Override
	public List<String> tabComplete() {
		if (!(sender instanceof Player)) return Collections.emptyList();
		else if (args.length == 2) return CLEAR_LIST;
		else return Collections.emptyList();
	}

	private static final ImmutableList<String> CLEAR_LIST = ImmutableList.of("clear");
}
