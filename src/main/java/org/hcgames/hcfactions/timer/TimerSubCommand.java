package org.hcgames.hcfactions.timer;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.HCFactions;
import org.mineacademy.fo.command.SimpleSubCommand;

@Getter
public abstract class TimerSubCommand extends SimpleSubCommand {
	private final String name;
	protected boolean isPlayerOnly = false;
	protected String description;
	protected String permission;
	protected String[] aliases;
	protected HCFactions instance;

    public TimerSubCommand(String name, String description){
		super(name);
		this.name = name;
		this.description = description;
		instance = HCFactions.getInstance();
	}
	public abstract void onCommand(CommandSender sender, String label, String[] args);

	public abstract String getUsage(String label);




}
