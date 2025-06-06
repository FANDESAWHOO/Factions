/**
 * This will be included on the core
 * With a custom system!
package org.hcgames.hcfactions.command.argument;

import com.doctordark.hcf.HCF;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import technology.brk.util.command.CommandArgument;

public class FactionLivesArgument extends CommandArgument {

    private final HCFactions plugin;

    public FactionLivesArgument(HCFactions plugin){
        super("lives", "Management of the factions lives");
        this.plugin = plugin;
    }

    @Override
    public String getUsage(String commandLabel){
        return HCF.getPlugin().getMessages().getString("Commands.Factions.Lives.Usage");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage(HCF.getPlugin().getMessages().getString("Error-Messages.PlayerOnly"));
            //player required
            return true;
        }

        if(args.length < 3){
            sender.sendMessage(getUsage(commandLabel));
            return true;
        }

        Player player = (Player) sender;

        if(args[1].equalsIgnoreCase("deposit") || args[1].equalsIgnoreCase("d")){
            int amount;

            try{
                amount = Integer.valueOf(args[2]);
            }catch (Exception e){
                sender.sendMessage(HCF.getPlugin().getMessages().getString("Error-Messages.InvalidNumber").replace("{number}", args[2]));
                //Invalid number
                return true;
            }

            if(amount <= 0){
                sender.sendMessage(HCF.getPlugin().getMessages().getString("Error-Messages.MustBePositive").replace("{number}", args[2]));
                //must be above 0
                return true;
            }

            if(amount > HCF.getPlugin().getDeathbanManager().getLives(player.getUniqueId())){
                sender.sendMessage(HCF.getPlugin().getMessages().getString("Commands.Factions.Lives.Deposit.Not-Enough").replace("{amount}", args[2]));
                //not enough lives
                return true;
            }

            PlayerFaction playerFaction;
            try {
                playerFaction = plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
            } catch (NoFactionFoundException e) {
                sender.sendMessage(HCF.getPlugin().getMessages().getString("Error-Messages.NotInFaction"));
                //not in a faction
                return true;
            }

            HCF.getPlugin().getDeathbanManager().takeLives(player.getUniqueId(), amount);
            playerFaction.setLives(playerFaction.getLives() + amount);
            playerFaction.broadcast(HCF.getPlugin().getMessages().getString("Commands.Faction.Lives.Deposit.Broadcast").replace("{player}",
                    player.getName()).replace("{amount}", args[0]).replace("{s}", amount == 1 ? "" : "s"));
            //Broadcast
            return true;
        }

        if(args[1].equalsIgnoreCase("withdraw") || args[1].equalsIgnoreCase("w")){
            int amount;

            try{
                amount = Integer.valueOf(args[2]);
            }catch (Exception e){
                sender.sendMessage(HCF.getPlugin().getMessages().getString("Error-Messages.InvalidNumber").replace("{number}", args[2]));
                //Invalid number
                return true;
            }

            if(amount <= 0){
                sender.sendMessage(HCF.getPlugin().getMessages().getString("Error-Messages.MustBePositive").replace("{number}", args[2]));
                //must be above 0
                return true;
            }

            PlayerFaction playerFaction;
            try {
                playerFaction = plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
            } catch (NoFactionFoundException e) {
                sender.sendMessage(HCF.getPlugin().getMessages().getString("Error-Messages.NotInFaction"));
                //not in a faction
                return true;
            }

            if(playerFaction.getMember(player).getRole() == Role.MEMBER){
                sender.sendMessage(HCF.getPlugin().getMessages().getString("Commands.Factions.Lives.Withdraw.Officer-Required"));
                //captain or higher required
                return true;
            }

            if(amount > playerFaction.getLives()){
                sender.sendMessage(HCF.getPlugin().getMessages().getString("Commands.Factions.Lives.Withdraw.Not-Enough")
                        .replace("{amount}", args[2]).replace("{s}", amount == 1 ? "" : "s"));
                //faction doesn't have that many lives
                return true;
            }

            playerFaction.setLives(playerFaction.getLives() - amount);
            HCF.getPlugin().getDeathbanManager().addLives(player.getUniqueId(), amount);
            sender.sendMessage(HCF.getPlugin().getMessages().getString("Commands.Factions.Lives.Withdraw.Withdrew")
                    .replace("{amount}", args[0]).replace("{s}", amount == 1 ? "" : "s"));
            //send message
            return true;
        }

        sender.sendMessage(getUsage(commandLabel));
        return true;
    }
}**/



