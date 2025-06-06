package org.hcgames.hcfactions.command.subcommand;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class FactionAnnouncementCommand extends FactionSubCommand {
	private final HCFactions plugin;

	public FactionAnnouncementCommand() {
		super("announcement|motd|announce");
		setDescription("Set your faction announcement.");
		plugin = HCFactions.getInstance();

	}

    @Override
	public String getUsage() {
		return '/' + label + ' ' + getName() + " <newAnnouncement>";
	}

	@Override
	public void onCommand() {


		if (args.length < 2) {
			tell(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
			return;
		}

		Player player = (Player) sender;
		PlayerFaction playerFaction;

		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(player);
		} catch (NoFactionFoundException e) {
			tell(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
			tell(Lang.of("Commands-Factions-Announcement-OfficerRequired"));
			return;
		}

		Optional<String> oldAnnouncement = playerFaction.getAnnouncement();
		String newAnnouncement;
		if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("remove"))
			newAnnouncement = null;
		else newAnnouncement = HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(args, 1, args.length));

		if (!oldAnnouncement.isPresent() && newAnnouncement == null) {
			tell(Lang.of("Commands-Factions-Announcement-AlreadyUnset"));
			return;
		}

		if (oldAnnouncement.isPresent() && newAnnouncement != null && oldAnnouncement.get().equals(newAnnouncement)) {
			tell(Lang.of("Commands-Factions-Announcement-SameAnnouncement")
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
