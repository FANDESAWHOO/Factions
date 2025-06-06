package org.hcgames.hcfactions.timer.argument;


import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.timer.PlayerTimer;
import org.hcgames.hcfactions.timer.Timer;
import org.hcgames.hcfactions.timer.TimerSubCommand;
import org.hcgames.hcfactions.util.uuid.UUIDHandler;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
/**
 * Soon check if we need
 * To edit the task.
 */
public class TimerCheckArgument extends TimerSubCommand {


    public TimerCheckArgument(HCFactions plugin){
        super("check", "Check remaining timer time");

    }

    @Override
    public String getUsage(String label){
        return '/' + label + ' ' + getName() + " <timerName> <playerName>";
    }

   

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		  return args.length == 2 ? null : Collections.emptyList();
	}

	@Override
	public void onCommand(CommandSender sender, String label, String[] args) {
		if(args.length < 3){
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return;
        }

        PlayerTimer temporaryTimer = null;
        for(Timer timer : instance.getTimerManager().getTimers())
			if (timer instanceof PlayerTimer && timer.getName().equalsIgnoreCase(args[1])) {
				temporaryTimer = (PlayerTimer) timer;
				break;
			}

        if(temporaryTimer == null){
            sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
            return;
        }

        PlayerTimer playerTimer = temporaryTimer;
        new BukkitRunnable(){
            @Override
            public void run(){
                UUID uuid;
                try{
                    uuid = UUIDHandler.getUUID(args[2]);
                }catch(Exception ex){
                    sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
                    return;
                }

                long remaining = playerTimer.getRemaining(uuid);
                sender.sendMessage(ChatColor.YELLOW + args[2] + " has timer " + playerTimer.getName() + " for another " + DurationFormatUtils.formatDurationWords(remaining, true, true));
            }
        }.runTaskAsynchronously(instance);
        return;
		
	}
}
