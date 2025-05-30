package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.World;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.api.FactionsAPI;
import org.hcgames.hcfactions.api.TimerAPI;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.mineacademy.fo.settings.Lang;

/**
 * Faction argument used to teleport to a nearby {@link org.bukkit.Location} safely if stuck.
 */
public final class FactionStuckCommand extends FactionSubCommand {



    public FactionStuckCommand() {
        super("stuck|trap|trapped");
        setDescription("Teleport to a safe position.");

    }

   
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName();
    }

    @Override
    public void onCommand() {
        checkConsole();

        Player player = (Player) sender;
        /*
        if(HCF.getPlugin().getSOTWManager().isPaused()){
            tell(HCF.getPlugin().getMessages().getString("Commands.Factions.Subcommand.Stuck.SOTW-Paused-Disabled"));
            return true;
        }*/

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            tell(Lang.of("Commands-Factions-Stuck-OverworldOnly"));
            //tell(ChatColor.RED + "You can only use this command from the overworld.");
            return;
        }
        TimerAPI.callStuck(player, FactionsAPI.getPlayerFaction(player), getLabel());
       /** MOVED TO TIMERS API.
        StuckTimer stuckTimer = HCFactions.getInstance().getTimerManager().getStuckTimer();

        if (!stuckTimer.setCooldown(player, player.getUniqueId())) {
            tell(Lang.of("Commands-Factions-Stuck-TimerRunning")
                    .replace("{timerName}", stuckTimer.getDisplayName()));
            //tell(ChatColor.RED + "Your " + stuckTimer.getName() + ChatColor.RED + " timer is already active.");
            return;
        }

        tell(Lang.of("Commands-Factions-Stuck-Teleporting")
                .replace("{time}", DurationFormatter.getRemaining(stuckTimer.getRemaining(player), true, false))
                .replace("{maxBlocksDistance}", String.valueOf(StuckTimer.MAX_MOVE_DISTANCE)));*/
        //tell(ChatColor.YELLOW + stuckTimer.getName() + ChatColor.YELLOW + " timer has started. " +
        //        "Teleport will occur in " + ChatColor.AQUA + DurationFormatter.getRemaining(stuckTimer.getRemaining(player), true, false) + ChatColor.YELLOW + ". " +
        //        "This will cancel if you move more than " + StuckTimer.MAX_MOVE_DISTANCE + " blocks.");

        return;
    }
}
