package org.hcgames.hcfactions.timer;


import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.timer.type.CombatTimer;
import org.hcgames.hcfactions.timer.type.InvincibilityTimer;
import org.hcgames.hcfactions.timer.type.StuckTimer;
import org.hcgames.hcfactions.timer.type.TeleportTimer;
import org.hcgames.hcfactions.util.DurationFormatter;
import org.hcgames.hcfactions.util.configuration.Config;
import org.mineacademy.fo.settings.Lang;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
public class TimerManager implements Listener {

	private final Set<Timer> timers;

	@Getter(AccessLevel.NONE)
	private final HCFactions plugin;
	@Getter(AccessLevel.NONE)
	private final Config config;


	private final InvincibilityTimer invincibilityTimer;
	private final StuckTimer stuckTimer;
	private final TeleportTimer teleportTimer;
	private final CombatTimer combatTimer;

	public TimerManager(HCFactions plugin) {
		this.plugin = plugin;
		timers = new LinkedHashSet<>();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);

		registerTimer(stuckTimer = new StuckTimer());
		registerTimer(invincibilityTimer = new InvincibilityTimer(plugin));
		registerTimer(combatTimer = new CombatTimer(plugin));
		registerTimer(teleportTimer = new TeleportTimer(plugin));


		config = new Config(plugin, "timers");
		for (Timer timer : timers) timer.load(config);
	}

	private void registerTimer(Timer timer) {
		timers.add(timer);
		if (timer instanceof Listener) plugin.getServer().getPluginManager().registerEvents((Listener) timer, plugin);
	}

	public void saveTimerData() {
		for (Timer timer : timers) timer.onDisable(config);

		config.save();
	}

	public void provideScoreboard(Player player, List<String> lines) {
		timers.stream().
				filter(PlayerTimer.class::isInstance)
				.map(PlayerTimer.class::cast).
				forEach(timer -> {
					long remaining = timer.getRemaining(player);

					if (remaining > 0)
						lines.add(Lang.of("scoreboard.timer").replace("{name}", timer.getDisplayName()).replace("{remaining}", DurationFormatter.getRemaining(remaining, true)));
				});
	}
}
