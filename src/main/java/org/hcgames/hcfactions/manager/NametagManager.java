package org.hcgames.hcfactions.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;

import io.github.thatkawaiisam.ostentus.BufferedNametag;
import io.github.thatkawaiisam.ostentus.OstentusAdapter;

public class NametagManager implements OstentusAdapter {

	@Override
	public List<BufferedNametag> getPlate(Player player) {
	    List<BufferedNametag> tags = new ArrayList<>();
	    PlayerFaction playerFaction = null;
	    try {
	        playerFaction = HCFactions.getInstance().getFactionManager().getPlayerFaction(player);
	    } catch (NoFactionFoundException e) {
	    }
	 
	    for (Player target : Bukkit.getOnlinePlayers()) {
	        BufferedNametag nametag;
	        PlayerFaction targetFaction = null;
	        try {
	            targetFaction = HCFactions.getInstance().getFactionManager().getPlayerFaction(target);
	        } catch (NoFactionFoundException e) {
	            // targetFaction permanece null.
	        }
	        
	        if (player.equals(target)) {
	            nametag = new BufferedNametag(target.getName(), ChatColor.DARK_GREEN.toString(), "", false, target);
	        } else if (playerFaction != null) {
	            if (playerFaction.getOnlinePlayers().contains(target)) {
	                nametag = new BufferedNametag(target.getName(), ChatColor.DARK_GREEN.toString(), "", false, target);
	            } else if (targetFaction != null && playerFaction.getAlliedFactions().contains(targetFaction)) {
	                nametag = new BufferedNametag(target.getName(), ChatColor.BLUE.toString(), "", false, target);
	            } else {
	                nametag = new BufferedNametag(target.getName(), ChatColor.RED.toString(), "", false, target);
	            }
	        } else {
	            nametag = new BufferedNametag(target.getName(), ChatColor.RED.toString(), "", false, target);
	        }

	        tags.add(nametag);
	    }

	    return tags;
	}


	@Override
	public boolean showHealthBelowName(Player player) {
		return false;
	}

}
