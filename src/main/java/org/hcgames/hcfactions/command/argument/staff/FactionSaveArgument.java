package org.hcgames.hcfactions.command.argument.staff;

import org.hcgames.hcfactions.HCFactions;
import org.mineacademy.fo.command.SimpleSubCommand;

import net.md_5.bungee.api.ChatColor;
/**
 * This works and Reload no
 * Why? 18:20 28/5/2025
 */

public class FactionSaveArgument extends SimpleSubCommand {
	
	private final HCFactions plugin;
	
	public FactionSaveArgument(HCFactions plugin) {
		super("save");
		setDescription( "Save to database the factions :)");
		this.plugin = plugin;
		// this.permission = "hcf.command.faction.argument." + getName();
	}
	
	  @Override
	    public void onCommand() {
		  sender.sendMessage(ChatColor.RED + "Saved succesfully.");
		  plugin.getFactionManager().saveFactionData();
		  return;
	  }

	
	public String getUsage(String label) {
		return "/f save";
	}

}
