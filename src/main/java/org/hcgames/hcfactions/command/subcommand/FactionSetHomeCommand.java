package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;


public class FactionSetHomeCommand extends SimpleSubCommand {

    private final HCFactions plugin;

    public FactionSetHomeCommand(HCFactions plugin) {
        super("sethome");
        setDescription( "Sets the faction home location.");
        this.plugin = plugin;
        if(!FactionCommands.getArguments().contains(this))
            FactionCommands.getArguments().add(this);
    }

   
    public String getUsage(String label) {
        return '/' + label + ' ' + getName();
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.of("Commands-ConsoleOnly"));
            return;
        }

        Player player = (Player) sender;

        if (Configuration.maxHeightFactionHome != -1 && player.getLocation().getY() > Configuration.maxHeightFactionHome) {
            //sender.sendMessage(ChatColor.RED + "You can not set your faction home above y " + plugin.getConfiguration().getMaxHeightFactionHome() + ".");
            sender.sendMessage(Lang.of("Commands-Factions-SetHome-MaxHeight")
                    .replace("{maxHeight}", String.valueOf(Configuration.maxHeightFactionHome)));
            return;
        }

        PlayerFaction playerFaction;
        try {
            playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        } catch (NoFactionFoundException e) {
            sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
            return;
        }

        FactionMember factionMember = playerFaction.getMember(player);

        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(Lang.of("Commands-Factions-SetHome-OfficerRequired"));
            //sender.sendMessage(ChatColor.RED + "You must be a faction officer to set the home.");
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
            player.sendMessage(Lang.of("Commands-Factions-SetHome-NotInsideHomeTerritory"));
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
