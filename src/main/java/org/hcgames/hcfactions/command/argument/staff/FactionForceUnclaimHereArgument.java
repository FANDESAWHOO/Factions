package org.hcgames.hcfactions.command.argument.staff;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;

import java.util.Collections;
import java.util.List;

public final class FactionForceUnclaimHereArgument extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionForceUnclaimHereArgument() {
        super("forceunclaimhere");
        setDescription( "Forces land unclaim where you are standing.");
        plugin = HCFactions.getInstance();
    //    this.permission = "hcf.command.faction.argument." + getName();
    }

   
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName();
    }

    @Override
    public void onCommand() {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage());
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can un-claim land from a faction.");
            return;
        }

        Player player = (Player) sender;
        Claim claimAt = plugin.getFactionManager().getClaimAt(player.getLocation());

        if (claimAt == null) {
            sender.sendMessage(ChatColor.RED + "There is not a claim at your current position.");
            return;
        }

        try {
            if(claimAt.getFaction().removeClaim(claimAt, sender)){
                player.sendMessage(ChatColor.YELLOW + "Removed claim " + claimAt.getClaimUniqueID().toString() + " owned by " + claimAt.getFaction().getName() + ".");
                return;
            }
        } catch (NoFactionFoundException e) {
            sender.sendMessage(ChatColor.RED + "Failed to remove claim " + claimAt.getClaimUniqueID().toString());
        }

        sender.sendMessage(ChatColor.RED + "Failed to remove claim " + claimAt.getClaimUniqueID().toString());
        return;
    }

    @Override
    public List<String> tabComplete() {
        return Collections.emptyList();
    }
}
