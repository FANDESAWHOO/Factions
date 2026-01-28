package org.hcgames.hcfactions.command;



import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.Lang;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;


public class FactionCommand {

    protected final int MAX_PAGES = 5;
    public FactionCommand() {
		HCFactions.getInstance().getCommandFramework().registerCommands(this);
		HCFactions.getInstance().getCommandFramework().registerHelp();
		
    }
    

    
    @Command(name = "faction", description = "The main command for Faction", aliases = { "f" ,"fac", "team", "t"}, usage = "/<command>",  playerOnly = false, adminsOnly = false)
    public void onCommand(CommandArgs arg) {
      if (arg.length() == 0) {
    	  showPage(arg.getSender(), 1);
    	  return;
      }
    }
	 public void showPage(CommandSender sender, int page) {
	        if (page < 1 || page > MAX_PAGES) {
	            sender.sendMessage(
	                Lang.of("Commands-Invalid-Number")
	                    .replace("{page}", String.valueOf(page))
	                    .replace("{max}", String.valueOf(MAX_PAGES))
	            );
	            return;
	        }

	        for (String line : Lang.ofList("faction-help-" + page)) {
	            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
	        }
	    }
}
