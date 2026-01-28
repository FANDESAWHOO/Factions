package org.hcgames.hcfactions.command.subcommand;

import com.google.common.collect.ImmutableList;
import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.command.FactionCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;

import java.util.Arrays;
import java.util.Optional;

public final class FactionAnnouncementCommand extends FactionCommand {
	private static final ImmutableList<String> CLEAR_LIST = ImmutableList.of("clear");
	private final HCFactions plugin;

	public FactionAnnouncementCommand() {
		plugin = HCFactions.getInstance();

	}
	 @Command(name = "faction.announcement", description = "Set your faction announcement.", aliases = { "f.motd","f.announce"}, usage = "/<command> <aliases> <newAnnouncement>",  playerOnly = true, adminsOnly = false)
	    public void onCommand(CommandArgs arg) {
		 Player player = arg.getPlayer();
		 if (arg.getArgs().length < 1) {
				player.sendMessage(Lang.of("Commands-Usage").replace("{usage}", "/f motd <newAnnouncement>"));
				return;
			}
		 PlayerFaction playerFaction;

			try {
				playerFaction = plugin.getFactionManager().getPlayerFaction(player);
			} catch (NoFactionFoundException e) {
				player.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
				return;
			}

			if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
				player.sendMessage(Lang.of("Commands-Factions-Announcement-OfficerRequired"));
				return;
			}

			Optional<String> oldAnnouncement = playerFaction.getAnnouncement();
			String newAnnouncement;
			if (arg.getArgs(0).equalsIgnoreCase("clear") || arg.getArgs(0).equalsIgnoreCase("none") || arg.getArgs(0).equalsIgnoreCase("remove"))
				newAnnouncement = null;
			else newAnnouncement = HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(arg.getArgs(), 1, arg.getArgs().length));

			if (!oldAnnouncement.isPresent() && newAnnouncement == null) {
				player.sendMessage(Lang.of("Commands-Factions-Announcement-AlreadyUnset"));
				return;
			}

			if (oldAnnouncement.isPresent() && newAnnouncement != null && oldAnnouncement.get().equals(newAnnouncement)) {
				player.sendMessage(Lang.of("Commands-Factions-Announcement-SameAnnouncement")
						.replace("%currentAnnouncement%", newAnnouncement));
				return;
			}

			playerFaction.setAnnouncement(newAnnouncement);

			if (newAnnouncement == null) {
				playerFaction.broadcast(Lang.of("Commands-Factions-Announcement-AnnouncementCleared")
						.replace("{player}", player.getName()));
				return;
			}

			playerFaction.broadcast(Lang.of("Commands-Factions-Announcement-BroadcastSwitched")
					.replace("{player}", player.getName())
					.replace("{oldAnnouncement}", (oldAnnouncement.isPresent() ? oldAnnouncement.get() : "none"))
					.replace("{newAnnouncement}", newAnnouncement));
			return;
	 }
	
}
