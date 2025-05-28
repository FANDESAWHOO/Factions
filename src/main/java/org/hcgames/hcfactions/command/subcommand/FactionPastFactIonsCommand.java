/**
 * This will be reformed if we can include this without complications.
 * package org.hcgames.hcfactions.command.subcommand;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.mineacademy.fo.command.SimpleSubCommand;

public class FactionPastFactionsCommand extends SimpleSubCommand {
	  private final HCFactions plugin;

	    public FactionPastFactionsCommand(HCFactions plugin) {
	        super("pastfactions");
	        setDescription( "See past factions of a user");
	        this.plugin = plugin;
	    }


	 
	    public String getUsage(String label){
	        return "/f " + s + " pastfactions [name]";
	    }

	    
	    @Override
	    public void onCommand(){
	        if(args.length < 2){
	            if(!(sender instanceof Player)){
	                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(commandLabel));
	                return;
	            }

	            List<String> pastFactions  = HCF.getPlugin().getUserManager().getUser(((Player)sender).getUniqueId()).getPastFactions();
	            sender.sendMessage(plugin.getMessages().getString("commands.pastfactions.own", pastFactions.isEmpty() ? "None" : HCFactions.COMMA_JOINER.join(pastFactions)));
	        }else{
	            Player player = plugin.getServer().getPlayer(args[1]);

	            if(player == null){
	                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
	                    UUID user = UUIDHandler.getUUID(args[1]);

	                    if(user == null || !HCF.getPlugin().getUserManager().userExists(user)){
	                        sender.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-Pay-UnknownPlayer").replace("{player}", args[1]));
	                        return;
	                    }

	                    List<String> pastFactions = HCF.getPlugin().getUserManager().getUser(user).getPastFactions();
	                    sender.sendMessage(plugin.getMessages().getString("commands.pastfactions.other", args[1], pastFactions.isEmpty() ? "None" : HCFactions.COMMA_JOINER.join(pastFactions)));
	                });
	            }else{
	                List<String> pastFactions = HCF.getPlugin().getUserManager().getUser(player.getUniqueId()).getPastFactions();
	                sender.sendMessage(plugin.getMessages().getString("commands.pastfactions.other", player.getName(), pastFactions.isEmpty() ? "None" : HCFactions.COMMA_JOINER.join(pastFactions)));
	            }
	        }

	        return;
	    }
}**/
