package org.hcgames.hcfactions.timer.argument;


import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.timer.PlayerTimer;
import org.hcgames.hcfactions.timer.Timer;
import org.hcgames.hcfactions.timer.TimerSubCommand;
import org.hcgames.hcfactions.util.JavaUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Soon we need to create
 * The findOfflinePlayer Method.
 */
public class TimerSetArgument extends TimerSubCommand {

	private static final Pattern WHITESPACE_TRIMMER = Pattern.compile("\\s");

	private final HCFactions plugin;

	public TimerSetArgument(HCFactions plugin) {
		super("set", "Set remaining timer time");
		this.plugin = plugin;
	}

	@Override
	public String getUsage(String label) {
		return '/' + label + ' ' + getName() + " <timerName> <all|playerName> <remaining>";
	}

	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
		if (args.length < 4) {
			sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
			return;
		}

		long duration = JavaUtils.parse(args[3]);

		if (duration == -1L) {
			sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
			return;
		}

		PlayerTimer playerTimer = null;
		for (Timer timer : plugin.getTimerManager().getTimers()) {
			if (timer instanceof PlayerTimer && WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("").equalsIgnoreCase(args[1])) {
				playerTimer = (PlayerTimer) timer;
				break;
			}
		}

		if (playerTimer == null) {
			sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
			return;
		}

		if (args[2].equalsIgnoreCase("all")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				playerTimer.setCooldown(player, player.getUniqueId(), duration, true, null);
			}

			sender.sendMessage(ChatColor.BLUE + "Set timer " + playerTimer.getName() + " for all to " + DurationFormatUtils.formatDurationWords(duration, true, true) + '.');
		} else {
			OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]); //TODO: breaking
			Player targetPlayer = null;

			if (target == null || (sender instanceof Player && ((targetPlayer = target.getPlayer()) != null) && !((Player) sender).canSee(targetPlayer))) {
				sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
				return;
			}

			playerTimer.setCooldown(targetPlayer, target.getUniqueId(), duration, true, null);
			sender.sendMessage(ChatColor.BLUE + "Set timer " + playerTimer.getName() + " duration to " + DurationFormatUtils.formatDurationWords(duration, true, true) + " for " + target.getName() + '.');
		}

		return;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 2) {
			return FluentIterable.from(plugin.getTimerManager().getTimers()).filter(new Predicate<Timer>() {
				@Override
				public boolean apply(Timer timer) {
					return timer instanceof PlayerTimer;
				}
			}).transform(new Function<Timer, String>() {
				@Nullable
				@Override
				public String apply(Timer timer) {
					return WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("");
				}
			}).toList();
		} else if (args.length == 3) {
			List<String> list = new ArrayList<>();
			list.add("ALL");
			Player player = sender instanceof Player ? (Player) sender : null;
			for (Player target : Bukkit.getOnlinePlayers()) {
				if (player == null || player.canSee(target)) {
					list.add(target.getName());
				}
			}

			return list;
		}

		return Collections.emptyList();
	}
}
