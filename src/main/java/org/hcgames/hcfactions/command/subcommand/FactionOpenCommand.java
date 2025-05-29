package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;


public class FactionOpenCommand extends SimpleSubCommand {
	   private final HCFactions plugin;

	    public FactionOpenCommand(HCFactions plugin) {
	        super("open");
	        setDescription("Opens the faction to the public.");
	        this.plugin = plugin;
			if(!FactionCommands.getArguments().contains(this))
				FactionCommands.getArguments().add(this);
	    }


	    public String getUsage(String label) {
	        return '/' + label + ' ' + getName();
	    }

	    @Override
	    public void onCommand() {
	        checkConsole();

	        Player player = (Player) sender;
	        PlayerFaction playerFaction = null;
	        try {
	            playerFaction = plugin.getFactionManager().getPlayerFaction(player);
	        } catch (NoFactionFoundException e) {
	            sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
	            return;
	        }

	        FactionMember factionMember = playerFaction.getMember(player.getUniqueId());

	        if (factionMember.getRole() != Role.LEADER) {
	            sender.sendMessage(Lang.of("Commands-Factions-Open-LeaderRequired"));
	            return;
	        }

	        boolean newOpen = !playerFaction.isOpen();
	        playerFaction.setOpen(newOpen);
	        playerFaction.broadcast(Lang.of("Commands-Factions-Open-Broadcast")
	                .replace("{player}", sender.getName())
	                .replace("{state}", (newOpen ? Lang.of("Commands-Factions-Open-OpenedText") : Lang.of("Commands-Factions-Open-ClosedText"))));
	        //playerFaction.broadcast(ChatColor.YELLOW + sender.getName() + " has " + (newOpen ? ChatColor.GREEN + "opened" : ChatColor.RED + "closed") + ChatColor.YELLOW + " the faction to public.");
	        
	    }
}
