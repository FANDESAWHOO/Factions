package org.hcgames.hcfactions.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.enums.TimerTypes;
import org.hcgames.hcfactions.api.events.FactionTimerEvent;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.timer.type.StuckTimer;
import org.hcgames.hcfactions.util.DurationFormatter;
import org.hcgames.hcfactions.util.text.CC;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.settings.Lang;

import java.util.Optional;

/**
 * This is to
 * Check if we need to
 * Use our Timers API
 * Or call Event to let
 * Other Plugins works.
 */
public final class TimerAPI {

	private static final boolean useEvents = !Configuration.api;
	private static final HCFactions plugin = HCFactions.getInstance();

	private static void tell(Player player, String s) {
		player.sendMessage(CC.translate(s));
	}

	public static void callStuck(Player player, PlayerFaction faction, String label) {
		if (useEvents)
			Common.callEvent(new FactionTimerEvent(player,  faction, TimerTypes.STUCK, false));
		else {
			StuckTimer stuckTimer = HCFactions.getInstance().getTimerManager().getStuckTimer();

			if (!stuckTimer.setCooldown(player, player.getUniqueId())) {
				tell(player, Lang.of("Commands-Factions-Stuck-TimerRunning")
						.replace("{timerName}", stuckTimer.getDisplayName()));
				return;
			}
			tell(player, Lang.of("Commands-Factions-Stuck-Teleporting")
					.replace("{time}", DurationFormatter.getRemaining(stuckTimer.getRemaining(player), true, false))
					.replace("{maxBlocksDistance}", String.valueOf(StuckTimer.MAX_MOVE_DISTANCE)));
		}
	}

	public static void callHome(Player player, PlayerFaction faction, String label) {
		if (useEvents)
			Common.callEvent(new FactionTimerEvent( player, faction, TimerTypes.TELEPORT,false));
		else {
			Faction factionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
			Optional<Location> home = faction.getHome();
			if (factionAt != faction && factionAt instanceof PlayerFaction && Configuration.allowTeleportingInEnemyTerritory) {
				tell(player, Lang.of("Commands-Factions-Home-InEnemyClaim")
						.replace("{commandLabel}", label));
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
					tell(player, Lang.of("Commands-Factions-Home-DisabledInWorld")
							.replace("{worldName}", name));
					return;
				}
			}

			if (factionAt != faction && factionAt instanceof PlayerFaction) millis *= 2L;

			plugin.getTimerManager().getTeleportTimer().teleport(player, home.get(), millis,
					Lang.of("Commands-Factions-Home-Teleporting")
							.replace("{time}", DurationFormatter.getRemaining(millis, true, false)),
					PlayerTeleportEvent.TeleportCause.COMMAND);
		}
	}


}
