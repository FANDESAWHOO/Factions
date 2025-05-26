package org.hcgames.hcfactions.command.argument.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;

import net.md_5.bungee.api.ChatColor;
import technology.brk.util.command.CommandArgument;

public class FactionSaveArgument extends CommandArgument {
	
	private final HCFactions plugin;
	
	public FactionSaveArgument(HCFactions plugin) {
		super("save", "Save to database the factions :)");
		this.plugin = plugin;
		this.permission = "hcf.command.faction.argument." + getName();
	}
	
	  @Override
	    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		  sender.sendMessage(ChatColor.RED + "Saved succesfully.");
		  plugin.getFactionManager().saveFactionData();
		  return true;
	  }

	@Override
	public String getUsage(String label) {
		return "/f save";
	}

}
