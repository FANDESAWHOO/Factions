package org.hcgames.hcfactions.command.argument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;

import com.doctordark.hcf.HCF;

import technology.brk.util.command.CommandArgument;

public class FactionFriendlyFireArgument extends CommandArgument  {

    private final HCFactions plugin;

    public FactionFriendlyFireArgument(HCFactions plugin) {
        super("friendlyfire", "Toggle friendly fire.", new String[]{"ff", "damage"});
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		 if (!(sender instanceof Player)) {
	            sender.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-ConsoleOnly"));
	            return true;
	        }
		  Player player = (Player) sender;
	        PlayerFaction playerFaction;

	        try {
	            playerFaction = plugin.getFactionManager().getPlayerFaction(player);
	        } catch (NoFactionFoundException e) {
	            sender.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-Factions-Global-NotInFaction"));
	            return true;
	        }
	        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
	            player.sendMessage("Must be an Officer or higher...");
	            return true;
	        }
	        if(playerFaction.isFriendly_fire()) {
	        	playerFaction.broadcast("An officer has toggled off the friendly fire...");	   
	        } else {
	        	playerFaction.broadcast("An officer has toggled on the friendly fire...");
	        }
        	playerFaction.setFriendly_fire(!playerFaction.isFriendly_fire());	        
		return true;
	}
}
