package org.hcgames.hcfactions.command;

import lombok.Getter;
import org.hcgames.hcfactions.HCFactions;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

@AutoRegister
public final class FocusCommand extends SimpleCommand {

	/**
	 * The singleton of this class
	 */
	@Getter
	private final static SimpleCommand instance = new FocusCommand();

	private FocusCommand(){
		super("focus");
	}

	/**
	 * Executed when the command is run. You can get the variables sender and args directly,
	 * and use convenience checks in the simple command class.
	 */
	@Override
	protected void onCommand() {
		checkConsole();
		HCFactions.getInstance().getServer().dispatchCommand(sender, "f focus " + (args.length > 0 ? args[0] : ""));
	}
}
