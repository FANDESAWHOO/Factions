package org.hcgames.hcfactions.command;

import org.hcgames.hcfactions.HCFactions;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;

@AutoRegister
public class FocusCommand extends SimpleCommand {

	public FocusCommand(){
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
