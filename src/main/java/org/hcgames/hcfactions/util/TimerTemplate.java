package org.hcgames.hcfactions.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.remain.Remain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerTemplate implements Listener {

	@Getter
	private static final Map<String, Warmup> waiting = new HashMap<>();
	private static Map<UUID, Integer> timerTicks = new HashMap<>();
	private final JavaPlugin plugin;

	public TimerTemplate(JavaPlugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);

		BukkitRunnable tickTask = new BukkitRunnable() {
			@Override
			public void run() {
				Map<UUID, Integer> newTimer = new HashMap<>();
				timerTicks.forEach((uuid, time) -> {
					if (time - 1 >= 0) newTimer.put(uuid, time - 1);
				});
				timerTicks = newTimer;
			}
		};
		tickTask.runTaskTimerAsynchronously(plugin, 0L, 1L);
	}

	public void startTeleport(Player player, Location destination, int seconds) {
		if (waiting.containsKey(player.getName())) {
			player.sendMessage("§cYou are already teleporting!");
			return;
		}

		Warmup warmup = new Warmup(player, destination);
		warmup.runTaskLater(plugin, seconds * 20L);
		waiting.put(player.getName(), warmup);
		timerTicks.put(player.getUniqueId(), seconds * 20);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!waiting.containsKey(player.getName())) {
					cancel();
					return;
				}

				int remainingTicks = timerTicks.getOrDefault(player.getUniqueId(), 0);
				int secondsLeft = (remainingTicks + 19) / 20; // Redondeo hacia arriba

				if (secondsLeft > 0) Remain.sendActionBar(player, "§cTeleporting in " + secondsLeft + "s...");
			}
		}.runTaskTimer(plugin, 0L, 20L);

		player.sendMessage("§cYou will be teleported in " + seconds + " seconds.");
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (!waiting.containsKey(p.getName())) return;

		if (event.getFrom().getBlockX() != event.getTo().getBlockX()
				|| event.getFrom().getBlockY() != event.getTo().getBlockY()
				|| event.getFrom().getBlockZ() != event.getTo().getBlockZ()) cancelTeleport(p);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player p = (Player) event.getEntity();
		if (!waiting.containsKey(p.getName())) return;

		cancelTeleport(p);
	}

	public void cancelTeleport(Player player) {
		player.sendMessage("§cTeleport cancelled!");
		Remain.sendActionBar(player,"§cTeleport cancelled!");
		Warmup warmup = waiting.remove(player.getName());
		if (warmup != null) warmup.cancel();
		timerTicks.remove(player.getUniqueId());
	}

	public static class Warmup extends BukkitRunnable {
		private final Player player;
		private final Location location;

		public Warmup(Player player, Location location) {
			this.player = player;
			this.location = location;
		}

		@Override
		public void run() {
			waiting.remove(player.getName());
			player.teleport(location);
		}

		public int getSecondsLeft() {
			int ticks = timerTicks.getOrDefault(player.getUniqueId(), 0);
			return Math.max(0, (ticks + 19) / 20);
		}
	}
}