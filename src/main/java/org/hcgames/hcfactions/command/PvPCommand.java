package org.hcgames.hcfactions.command;

import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.timer.type.InvincibilityTimer;
import org.hcgames.hcfactions.util.BukkitUtils;
import org.hcgames.hcfactions.util.DurationFormatter;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Collections;
import java.util.List;
/**
 * Moved from Core to here.
 * Command used to manage the {@link InvincibilityTimer} of {@link Player}s.
 */
@AutoRegister
public final class PvPCommand extends SimpleCommand {

	private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("enable", "time");

	public PvPCommand(){
		super("pvp");
	}

	@Override
	protected void onCommand() {
		Player player = (Player) sender;
		InvincibilityTimer pvpTimer = HCFactions.getInstance().getTimerManager().getInvincibilityTimer();

		if(args.length < 1){
			printUsage(sender, getLabel(), pvpTimer);
			return;
		}

		if(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("off")){
			if(pvpTimer.getRemaining(player) <= 0L){
				sender.sendMessage(Lang.of("Commands.PvPTimer.Timer.NotActive")
						.replace("{timerName}", pvpTimer.getName()));
				return;
			}

			sender.sendMessage(Lang.of("Commands.PvPTimer.Timer.Disabled")
					.replace("{timerName}", pvpTimer.getName()));
			pvpTimer.clearCooldown(player);
			return;
		}

		if(args[0].equalsIgnoreCase("remaining") || args[0].equalsIgnoreCase("time") || args[0].equalsIgnoreCase("left") || args[0].equalsIgnoreCase("check")){
			long remaining = pvpTimer.getRemaining(player);
			if(remaining <= 0L){
				sender.sendMessage(Lang.of("Commands.PvPTimer.Timer.NotActive")
						.replace("{timerName}", pvpTimer.getName()));
				return;
			}

			sender.sendMessage(Lang.of("Commands.PvPTimer.Active.Output")
					.replace("{timerName}", pvpTimer.getName())
					.replace("{timerTimeRemaining}", DurationFormatter.getRemaining(remaining, true, false))
					.replace("{isPausedText}", (pvpTimer.isPaused(player) ? Lang.of("Commands.PvPTimer.Active.Paused") : "")));
			return;
		}

		printUsage(sender, getLabel(), pvpTimer);
	}


	@Override
	public List<String> tabComplete(){
		return args.length == 1 ? BukkitUtils.getCompletions(args, COMPLETIONS) : Collections.emptyList();
	}

	/**
	 * Prints the usage of this command to a sender.
	 *
	 * @param sender the sender to print for
	 * @param label  the label used for command
	 */
	private void printUsage(CommandSender sender, String label, InvincibilityTimer pvpTimer){
		sender.sendMessage(Lang.of("Commands.PvPTimer.Usage")
				.replace("{timerName}", pvpTimer.getName())
				.replace("{commandLabel}", label));
	}
}
