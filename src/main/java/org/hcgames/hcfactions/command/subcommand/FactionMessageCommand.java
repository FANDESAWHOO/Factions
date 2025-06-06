package org.hcgames.hcfactions.command.subcommand;

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

    public FactionMessageCommand() {
        super("message|msg");
        setDescription("Sends a message to your faction.");
        plugin = HCFactions.getInstance();
    }

 
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " <message>";
    }

    @Override
    public void onCommand() {
        if (args.length < 2) {
            tell(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
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

        String format = String.format(ChatChannel.FACTION.getRawFormat(player), "", HCFactions.SPACE_JOINER.join(Arrays.copyOfRange(args, 1, args.length)));
        for (Player target : playerFaction.getOnlinePlayers()) target.sendMessage(format);

        return;
    }
}
