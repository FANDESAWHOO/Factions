package org.hcgames.hcfactions.command;

import lombok.Getter;
import org.mineacademy.fo.command.SimpleCommand;

import java.util.ArrayList;
import java.util.List;

/**
 *  Class to handle the command and tab completion for the faction command.
 */
public class FactionCommand extends SimpleCommand {

	/**
	 * The singleton of this class
	 */
	@Getter
	private final static FactionCommand instance = new FactionCommand();
	@Getter
	private final List<FactionSubCommand> subCommands = new ArrayList<>();

	protected FactionCommand() {
		super("faction | fac | f");
	}

	/**
	 * Executed when the command is run. You can get the variables sender and args directly,
	 * and use convenience checks in the simple command class.
	 */
	@Override
	protected void onCommand() {
		if (args.length == 1) for (FactionSubCommand subCommand : getSubCommands())
			if (args[0].startsWith(subCommand.getName())) {
				String permission = subCommand.getPermission();
				if (permission == null || sender.hasPermission(permission)) {
					subCommand.execute(sender, getLabel(), args);
					return;
				} else {
					// LOGIC HELP HERE, PLEASE CONTINUE LATER.
				}
			}
	}
}
