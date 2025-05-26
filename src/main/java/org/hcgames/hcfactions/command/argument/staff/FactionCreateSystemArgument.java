package org.hcgames.hcfactions.command.argument.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.faction.system.SystemTeam;

import com.doctordark.hcf.HCF;

import net.md_5.bungee.api.ChatColor;
import technology.brk.util.JavaUtils;
import technology.brk.util.command.CommandArgument;

public class FactionCreateSystemArgument extends CommandArgument {
	
	private HCFactions plugin;
	
	public FactionCreateSystemArgument(HCFactions plugin) {
	    super("createsystem", "Create a system faction.");
		this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + getName();
	}

	@Override
	public String getUsage(String arg0) {
		return "f createsystem name";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 2) {
            sender.sendMessage(plugin.getMessages().getString("command.error.usage", getUsage(label)));
            return true;
        }
	    String name = args[1];
	    int value = plugin.getConfiguration().getFactionNameMinCharacters();
	   
	    if (name.length() < value) {
            sender.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-Factions-Create-MinimumChars")
                    .replace("{minChars}", String.valueOf(value)));
            return true;
        }

        value = plugin.getConfiguration().getFactionNameMaxCharacters();

        if (name.length() > value) {
            sender.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-Factions-Create-MaximumChars")
                    .replace("{maxChars}", String.valueOf(value)));
            return true;
        }

        if (!JavaUtils.isAlphanumeric(name)) {
            sender.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-Factions-Create-MustBeAlphanumeric"));
            return true;
        }

        try {
            if(plugin.getFactionManager().getFaction(name) != null){
                sender.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-Factions-Create-NameAlreadyExists")
                        .replace("{factionName}", name));
                return true;
            }
        } catch (NoFactionFoundException e) {}
        plugin.getFactionManager().createFaction(new SystemTeam(name), sender);
		return true;
	}

}
