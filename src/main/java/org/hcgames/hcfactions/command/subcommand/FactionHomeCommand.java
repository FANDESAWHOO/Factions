package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.TimerAPI;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Optional;
import java.util.UUID;

public class FactionHomeCommand extends SimpleSubCommand {

	private final HCFactions plugin;

	public FactionHomeCommand() {
		super("home");
		setDescription("Teleport to the faction home.");
		plugin = HCFactions.getInstance();
		if(!FactionCommands.getArguments().contains(this))
			FactionCommands.getArguments().add(this);
	}


	public String getUsage(String label) {
		return '/' + label + ' ' + getName();
	}

	@Override
	public void onCommand() {
		checkConsole();

		Player player = (Player) sender;

		if (args.length >= 2 && args[1].equalsIgnoreCase("set")) {
			player.performCommand("f sethome");
			return;
		}

		UUID uuid = player.getUniqueId();
/* This will be managed by TimerAPI
		PlayerTimer timer = HCF.getPlugin().getTimerManager().getEnderPearlTimer();
		long remaining = timer.getRemaining(player);

		if (remaining > 0L) {
			sender.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-Factions-Home-TimerActive")
					.replace("{timerName}", timer.getName()));

			return true;
		}

		if ((remaining = (timer = HCF.getPlugin().getTimerManager().getCombatTimer()).getRemaining(player)) > 0L) {
			sender.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-Factions-Home-TimerActive")
					.replace("{timerName}", timer.getDisplayName()));

			return true;
		}*/

		PlayerFaction playerFaction;
		try {
			playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);
		} catch (NoFactionFoundException e) {
			sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
			return;
		}

		Optional<Location> home = playerFaction.getHome();

		if (!home.isPresent()) {
			sender.sendMessage(Lang.of("Commands-Factions-Home-NoFactionHomeSet"));
			return;
		}

		if (Configuration.maxHeightFactionHome != -1 && home.get().getY() > Configuration.maxHeightFactionHome) {
			sender.sendMessage(Lang.of("Commands-Factions-Home-HomeAboveHeightLimit")
					.replace("{factionHomeHeightLimit}", String.valueOf(Configuration.maxHeightFactionHome))
					.replace("{factionHomeX}", String.valueOf(home.get().getBlockX()))
					.replace("{factionHomeZ}", String.valueOf(home.get().getBlockZ())));

			return;
		}
		TimerAPI.callHome(player, playerFaction);
	/**
	 * This need to be moved to Core Plugin, tomorrow.
	 *  Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());

		if (factionAt != playerFaction && factionAt instanceof PlayerFaction && Configuration.allowTeleportingInEnemyTerritory) {
			sender.sendMessage(Lang.of("Commands-Factions-Home-InEnemyClaim")
					.replace("{commandLabel}", getLabel()));
			return;
		}

		long millis;
		if (factionAt.isSafezone()) millis = 0L;
		else {
			String name;
			switch (player.getWorld().getEnvironment()) {
				case THE_END:
					name = "End";
					millis = Configuration.factionHomeTeleportDelayEnd;
					break;
				case NETHER:
					name = "Nether";
					millis = Configuration.factionHomeTeleportDelayNether;
					break;
				case NORMAL:
					name = "Overworld";
					millis = Configuration.factionHomeTeleportDelayNormal;
					break;
				default:
					throw new IllegalArgumentException("Unrecognised environment");
			}

			if (millis == -1L) {
				sender.sendMessage(Lang.of("Commands-Factions-Home-DisabledInWorld")
						.replace("{worldName}", name));
				return;
			}
		}

		if (factionAt != playerFaction && factionAt instanceof PlayerFaction) millis *= 2L;

		HCF.getPlugin().getTimerManager().getTeleportTimer().teleport(player, home.get(), millis,
				HCF.getPlugin().getMessagesOld().getString("Commands-Factions-Home-Teleporting")
						.replace("{time}", DurationFormatter.getRemaining(millis, true, false)),
				PlayerTeleportEvent.TeleportCause.COMMAND);

		return;*/
	}
}
