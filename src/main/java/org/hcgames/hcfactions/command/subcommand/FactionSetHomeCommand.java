package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;


public final class FactionSetHomeCommand extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionSetHomeCommand() {
        super("sethome");
        setDescription( "Sets the faction home location.");
        plugin = HCFactions.getInstance();

    }

   
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName();
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            tell(Lang.of("Commands-ConsoleOnly"));
            return;
        }

        Player player = (Player) sender;

        if (Configuration.maxHeightFactionHome != -1 && player.getLocation().getY() > Configuration.maxHeightFactionHome) {
            //tell(ChatColor.RED + "You can not set your faction home above y " + plugin.getConfiguration().getMaxHeightFactionHome() + ".");
            tell(Lang.of("Commands-Factions-SetHome-MaxHeight")
                    .replace("{maxHeight}", String.valueOf(Configuration.maxHeightFactionHome)));
            return;
        }

        PlayerFaction playerFaction;
        try {
            playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        } catch (NoFactionFoundException e) {
            tell(Lang.of("Commands-Factions-Global-NotInFaction"));
            return;
        }

        FactionMember factionMember = playerFaction.getMember(player);

        if (factionMember.getRole() == Role.MEMBER) {
            tell(Lang.of("Commands-Factions-SetHome-OfficerRequired"));
            //tell(ChatColor.RED + "You must be a faction officer to set the home.");
            return;
        }

        Location location = player.getLocation();

        boolean insideTerritory = false;
        for (Claim claim : playerFaction.getClaims())
			if (claim.contains(location)) {
				insideTerritory = true;
				break;
			}

        if (!insideTerritory) {
            tell(Lang.of("Commands-Factions-SetHome-NotInsideHomeTerritory"));
            //player.sendMessage(ChatColor.RED + "You may only set your home in your territory.");
            return;
        }

        playerFaction.setHome(location);
        playerFaction.broadcast(Lang.of("Commands-Factions-SetHome-Broadcast")
                .replace("{player}", factionMember.getRole().getAstrix() + sender.getName()));
        //playerFaction.broadcast(plugin.getConfiguration().getRelationColourTeammate() + factionMember.getRole().getAstrix() +
        //        sender.getName() + ChatColor.YELLOW + " has updated the faction home.");

        return;
    }
}
