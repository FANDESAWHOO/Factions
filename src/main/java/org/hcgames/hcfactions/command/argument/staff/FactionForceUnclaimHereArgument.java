package org.hcgames.hcfactions.command.argument.staff;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.Collections;
import java.util.List;

public class FactionForceUnclaimHereArgument extends SimpleSubCommand {

    private final HCFactions plugin;

    public FactionForceUnclaimHereArgument(HCFactions plugin) {
        super("forceunclaimhere");
        setDescription( "Forces land unclaim where you are standing.");
        this.plugin = plugin;
    //    this.permission = "hcf.command.faction.argument." + getName();
    }

   
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public void onCommand() {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(getLabel()));
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
