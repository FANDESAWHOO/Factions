package org.hcgames.hcfactions.command.subcommand;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.ChatChannel;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.settings.Lang;

public class FactionMessageCommand extends SimpleSubCommand {

    private final HCFactions plugin;

    public FactionMessageCommand(HCFactions plugin) {
        super("message | msg");
        setDescription("Sends a message to your faction.");
        this.plugin = plugin;
    }

 
    public String getUsage(String label) {
        return '/' + label + ' ' + getName() + " <message>";
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use faction chat.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Lang.of("Commands-Usage").replace("{usage}", getUsage(getLabel())));
            return;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction;
        try {
            playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        } catch (NoFactionFoundException e) {
            sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
            return;
        }

        String format = String.format(ChatChannel.FACTION.getRawFormat(player), "", HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(args, 1, args.length)));
        for (Player target : playerFaction.getOnlinePlayers()) {
            target.sendMessage(format);
        }

        return;
    }
}
