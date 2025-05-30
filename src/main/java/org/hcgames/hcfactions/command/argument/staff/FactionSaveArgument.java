package org.hcgames.hcfactions.command.argument.staff;

import net.md_5.bungee.api.ChatColor;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
/**
 * This works and Reload no
 * Why? 18:20 28/5/2025
 */

public final class FactionSaveArgument extends FactionSubCommand {
	
	private final HCFactions plugin;
	
	public FactionSaveArgument() {
		super("save");
		setDescription( "Save to database the factions :)");
		plugin = HCFactions.getInstance();
		// this.permission = "hcf.command.faction.argument." + getName();
	}
	
	  @Override
	    public void onCommand() {
		  tell(ChatColor.RED + "Saved succesfully.");
		  plugin.getFactionManager().saveFactionData();
		  return;
	  }

	
	@Override
	public String getUsage() {
		return "/f save";
	}

}
