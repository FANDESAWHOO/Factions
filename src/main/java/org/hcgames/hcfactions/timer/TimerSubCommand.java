package org.hcgames.hcfactions.timer;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.hcgames.hcfactions.HCFactions;

import lombok.Getter;

@Getter
public abstract class TimerSubCommand implements TabCompleter {
	private final String name;
	protected boolean isPlayerOnly = false;
	protected String description;
	protected String permission;
	protected String[] aliases;
	protected HCFactions instance;

    public TimerSubCommand(String name, String description){
		this.name = name;
		this.description = description;
		instance = HCFactions.getInstance();
	}
	public abstract void onCommand(CommandSender sender, String label, String[] args);

	public abstract String getUsage(String label);


}
