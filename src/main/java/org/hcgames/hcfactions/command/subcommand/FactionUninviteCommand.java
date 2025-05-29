package org.hcgames.hcfactions.command.subcommand;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

import java.util.Set;

public final class FactionUninviteCommand extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionUninviteCommand(HCFactions plugin) {
        super("uninvite | deinvite | deinv | uninv | revoke");
        setDescription("Revoke an invitation to a player.");
        this.plugin = plugin;
    }

   
    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " <all|playerName>";
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can un-invite from a faction.");
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
        FactionMember factionMember = playerFaction.getMember(player);

        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(Lang.of("Commands-Factions-Uninvite-OfficerRequired"));
            //sender.sendMessage(ChatColor.RED + "You must be a faction officer to un-invite players.");
            return;
        }

        Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();

        if (args[1].equalsIgnoreCase("all")) {
            invitedPlayerNames.clear();
            sender.sendMessage(Lang.of("Commands-Factions-Uninvite-ClearedAll"));
            //sender.sendMessage(ChatColor.YELLOW + "You have cleared all pending invitations.");
            return;
        }

        if (!invitedPlayerNames.remove(args[1])) {
            sender.sendMessage(Lang.of("Commands-Factions-Uninvite-NoPendingInvites")
                    .replace("{name}", args[1]));
            //sender.sendMessage(ChatColor.RED + "There is not a pending invitation for " + args[1] + '.');
            return;
        }

        playerFaction.broadcast(Lang.of("Commands-Factions-Uninvite-Broadcast")
                .replace("{player}", factionMember.getRole().getAstrix() + sender.getName())
                .replace("{name}", args[1]));
        //playerFaction.broadcast(ChatColor.YELLOW + factionMember.getRole().getAstrix() + sender.getName() + " has uninvited " +
        //        plugin.getConfiguration().getRelationColourEnemy() + args[1] + ChatColor.YELLOW + " from the faction.");

        return;
    }

    /*@Override SYSTEM UPDATE WHY YOU REMOVE THIS CODE????????
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>(COMPLETIONS);
        results.addAll(playerFaction.getInvitedPlayerNames());
        return results;
    }

    private static final ImmutableList<String> COMPLETIONS = ImmutableList.of("all");*/
}
