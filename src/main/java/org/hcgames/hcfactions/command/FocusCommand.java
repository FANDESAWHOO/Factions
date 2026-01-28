package org.hcgames.hcfactions.command;

import org.hcgames.hcfactions.HCFactions;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

public final class FocusCommand{

	public FocusCommand() {
		HCFactions.getInstance().getCommandFramework().registerCommands(this);
		HCFactions.getInstance().getCommandFramework().registerHelp();
	}
  
    @Command(name = "focus", description = "The main command for Focus", usage = "/focus <name>",  playerOnly = true, adminsOnly = false)
    public void onCommand(CommandArgs arg) {
    	String[] args = arg.getArgs();
    	HCFactions.getInstance().getServer().dispatchCommand(arg.getPlayer(), "f focus " + (args.length > 0 ? args[0] : ""));
	}
}
