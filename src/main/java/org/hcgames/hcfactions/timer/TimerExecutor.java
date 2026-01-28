package org.hcgames.hcfactions.timer;


import lombok.Getter;

import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;
import org.hcgames.hcfactions.timer.argument.TimerCheckArgument;
import org.hcgames.hcfactions.timer.argument.TimerSetArgument;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import java.util.ArrayList;


public final class TimerExecutor {

	/**
	 * The singleton of this class
	 */
	@Getter
	private final static TimerExecutor instance = new TimerExecutor();
	private final ArrayList<TimerSubCommand> arguments;

	public TimerExecutor() {
		arguments = new ArrayList<>();
		addArgument(new TimerCheckArgument(HCFactions.getInstance()));
		addArgument(new TimerSetArgument(HCFactions.getInstance()));
	}

	private void addArgument(TimerSubCommand subCommand) {
		arguments.add(subCommand);
	}

	private TimerSubCommand getArgument(String argument) {
		for (TimerSubCommand subCommand : arguments)
			if (subCommand.getName().equalsIgnoreCase(argument)) return subCommand;

		return null;
	}

	@Command(name = "timer", description = "The main command for Timer", usage = "/<command>",  playerOnly = true, adminsOnly = false)
	public void onCommand(CommandArgs arg) {
		String[] args = arg.getArgs();
		sendHelp(arg.getPlayer());
		if (args.length >= 1) {
			TimerSubCommand argument = getArgument(args[0]);
			if (argument == null) {
				arg.getPlayer().sendMessage(Lang.of("Commands-Unknown-Subcommand")
						.replace("{subCommand}", args[0])
						.replace("{commandLabel}", arg.getLabel()));
				return;
			} else argument.onCommand(arg.getPlayer(), arg.getLabel(), args);
		} else sendHelp(arg.getPlayer());
	}

	private void sendHelp(Player player) {
		player.sendMessage("&6&m---*--------------------------------------------*---/n&6Timer Help ");
		for (TimerSubCommand command : arguments)
			player.sendMessage("&6/timer {commandArgument} &7 {commandDescription}".replace("{commandArgument}", command.getName()).replace("{commandDescription}", command.getDescription()));

	}
}
