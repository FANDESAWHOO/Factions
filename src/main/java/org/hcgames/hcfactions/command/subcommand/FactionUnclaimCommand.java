package org.hcgames.hcfactions.command.subcommand;


import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FactionUnclaimCommand extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionUnclaimCommand() {
        super("unclaim");
        setDescription("Unclaims land from your faction.");
        plugin = HCFactions.getInstance();
    }

    
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " [all]";
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            tell(ChatColor.RED + "Only players can un-claim land from a faction.");
            return;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction;
        try {
            playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        } catch (NoFactionFoundException e) {
            tell(Lang.of("Commands-Factions-Global-NotInFaction"));
            return;
        }

        FactionMember factionMember = playerFaction.getMember(player);

        if(factionMember.getRole() == Role.MEMBER || factionMember.getRole() == Role.CAPTAIN){
            tell(Lang.of("Commands-Factions-Unclaim-CoLeaderRequired"));
            return;
        }

        Collection<Claim> factionClaims = playerFaction.getClaims();

        if (factionClaims.isEmpty()) {
            tell(Lang.of("Commands-Factions-Unclaim-NoClaims"));
            //tell(ChatColor.RED + "Your faction does not own any claims.");
            return;
        }

        // Find out what claims the player wants removed.
        Collection<Claim> removingClaims;
        if (args.length > 1 && args[1].equalsIgnoreCase("all")) removingClaims = new ArrayList<>(factionClaims);
		else {
            Location location = player.getLocation();
            Claim claimAt = plugin.getFactionManager().getClaimAt(location);
            if (claimAt == null || !factionClaims.contains(claimAt)) {
                tell(Lang.of("Commands-Factions-Unclaim-NoClaimHere"));
                //tell(ChatColor.RED + "Your faction does not own a claim here.");
                return;
            }

            removingClaims = Collections.singleton(claimAt);
        }

        if (!playerFaction.removeClaims(removingClaims, player)) {
            tell(Lang.of("Commands-Factions-Unclaim-ErrorRemoving"));
            //tell(ChatColor.RED + "Error when removing claims, please contact an Administrator.");
            return;
        }

        int removingAmount = removingClaims.size();
        playerFaction.broadcast(Lang.of("Commands-Factions-Unclaim-RemovedClaims")
                .replace("{player}", factionMember.getRole().getAstrix() + sender.getName())
                .replace("{amountOfClaims}", String.valueOf(removingAmount))
                .replace("{s}", (removingAmount > 1 ? "s" : "")));
        //playerFaction.broadcast(ChatColor.RED + ChatColor.BOLD.toString() + factionMember.getRole().getAstrix() +
        //        sender.getName() + " has removed " + removingAmount + " claim" + (removingAmount > 1 ? "s" : "") + '.');
        return;
    }

    @Override
    public List<String> tabComplete() {
        return args.length == 2 ? COMPLETIONS : Collections.<String>emptyList();
    }

    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");
}
