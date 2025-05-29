package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.World;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionCommands;
import org.hcgames.hcfactions.timer.type.StuckTimer;
import org.hcgames.hcfactions.util.DurationFormatter;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

/**
 * Faction argument used to teleport to a nearby {@link org.bukkit.Location} safely if stuck.
 */
public class FactionStuckCommand extends SimpleSubCommand {

    private final HCFactions plugin;

    public FactionStuckCommand(HCFactions plugin) {
        super("stuck | trap | trapped");
        setDescription("Teleport to a safe position.");
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
        /*
        if(HCF.getPlugin().getSOTWManager().isPaused()){
            sender.sendMessage(HCF.getPlugin().getMessages().getString("Commands.Factions.Subcommand.Stuck.SOTW-Paused-Disabled"));
            return true;
        }*/

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(Lang.of("Commands-Factions-Stuck-OverworldOnly"));
            //sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
            return;
        }

        StuckTimer stuckTimer = HCFactions.getInstance().getTimerManager().getStuckTimer();

        if (!stuckTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(Lang.of("Commands-Factions-Stuck-TimerRunning")
                    .replace("{timerName}", stuckTimer.getDisplayName()));
            //sender.sendMessage(ChatColor.RED + "Your " + stuckTimer.getName() + ChatColor.RED + " timer is already active.");
            return;
        }

        sender.sendMessage(Lang.of("Commands-Factions-Stuck-Teleporting")
                .replace("{time}", DurationFormatter.getRemaining(stuckTimer.getRemaining(player), true, false))
                .replace("{maxBlocksDistance}", String.valueOf(StuckTimer.MAX_MOVE_DISTANCE)));
        //sender.sendMessage(ChatColor.YELLOW + stuckTimer.getName() + ChatColor.YELLOW + " timer has started. " +
        //        "Teleport will occur in " + ChatColor.AQUA + DurationFormatter.getRemaining(stuckTimer.getRemaining(player), true, false) + ChatColor.YELLOW + ". " +
        //        "This will cancel if you move more than " + StuckTimer.MAX_MOVE_DISTANCE + " blocks.");

        return;
    }
}
