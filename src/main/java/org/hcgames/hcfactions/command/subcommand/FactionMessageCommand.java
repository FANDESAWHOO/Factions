package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.ChatChannel;
import org.mineacademy.fo.settings.Lang;

import java.util.Arrays;

public final class FactionMessageCommand extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionMessageCommand(HCFactions plugin) {
        super("message | msg");
        setDescription("Sends a message to your faction.");
        this.plugin = plugin;
    }

 
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " <message>";
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use faction chat.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
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
        for (Player target : playerFaction.getOnlinePlayers()) target.sendMessage(format);

        return;
    }
}
