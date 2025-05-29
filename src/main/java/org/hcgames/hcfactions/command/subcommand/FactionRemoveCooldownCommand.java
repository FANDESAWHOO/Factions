package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.mineacademy.fo.settings.Lang;

public final class FactionRemoveCooldownCommand extends FactionSubCommand {
	
	 private final HCFactions plugin;

	    public FactionRemoveCooldownCommand(HCFactions plugin){
	        super("removecooldown");
	        setDescription("Removes a faction cool down for a player.");
	        this.plugin = plugin;

	    }

	 
	    @Override
		public final String getUsage() {
	        return Lang.of("Commands.Factions.RemoveCooldown.Usage");
	    }

	    @Override
	    public void onCommand(){
	        if(args.length < 3){
	            sender.sendMessage(getUsage());
	            return;
	        }

	        Player player = plugin.getServer().getPlayer(args[1]);

	        if(player == null){
	            sender.sendMessage(Lang.of("Error-Messages.InvalidPlayer").replace("{player}", args[1]));
	            return;
	        }

	        Faction faction;
	        try {
	            faction = plugin.getFactionManager().getFaction(args[2]);
	        } catch (NoFactionFoundException e) {
	            sender.sendMessage(Lang.of("Error-Messages.InvalidFaction").replace("{faction}", args[2]));
	            return;
	        }

	        if(!(faction instanceof PlayerFaction)){
	            sender.sendMessage(Lang.of("Commands.Factions.RemoveCooldown.InvalidFactionType").replace("{faction}", faction.getName()));
	            return;
	        }

	        PlayerFaction pFaction = (PlayerFaction) faction;

	        if(!pFaction.hasCooldown(player.getUniqueId())){
	            sender.sendMessage(Lang.of("Commands.Factions.RemoveCooldown.NotOnCooldown").replace("{player}", player.getName()));
	            return;
	        }

	        pFaction.removeCooldown(player.getUniqueId());
	        sender.sendMessage(Lang.of("Commands.Factions.RemoveCooldown.CooldownRemoved").replace("{player}", player.getName()).replace("{faction}", faction.getName()));

	    }

}
