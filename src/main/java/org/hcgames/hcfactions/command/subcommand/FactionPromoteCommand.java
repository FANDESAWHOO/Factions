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

import java.util.UUID;

public final class FactionPromoteCommand extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionPromoteCommand(HCFactions plugin) {
        super("promote | captain | officer | mod | moderator");
        setDescription( "Promotes a player to a captain.");
        this.plugin = plugin;

    }


    @Override
	public String getUsage() {
        return '/' + label + ' ' + getName() + " <playerName>";
    }

    @Override
    public void onCommand() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set faction captains.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Lang.of("Commands-Usage").replace("{usage}", getUsage()));
            return;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        PlayerFaction playerFaction;
        try {
            playerFaction = plugin.getFactionManager().getPlayerFaction(uuid);
        } catch (NoFactionFoundException e) {
            sender.sendMessage(Lang.of("Commands-Factions-Global-NotInFaction"));
            return;
        }

        //if (playerFaction.getMember(uuid).getRole() != Role.LEADER) {
          //  sender.sendMessage(plugin.getMessagesOld().getString("Commands-Factions-Promote-LeaderRequired"));
            //sender.sendMessage(ChatColor.RED + "You must be a faction leader to assign members as a captain.");
         //   return;
        //}

        FactionMember targetMember = playerFaction.findMember(args[1]);

        if (targetMember == null) {
            sender.sendMessage(Lang.of("Commands-Factions-Promote-PlayerNotInFaction"));
            //sender.sendMessage(ChatColor.RED + "That player is not in your faction.");
            return;
        }

//        if (targetMember.getRole() != Role.MEMBER) {
  //          sender.sendMessage(plugin.getMessagesOld().getString("Commands-Factions-Promote-PromotionTooHigh")
    //                .replace("{player}", targetMember.getName())
     //               .replace("{playerRole}", targetMember.getRole().getName()));
           //sender.sendMessage(ChatColor.RED + "You can only assign captains to members, " + targetMember.getName() + " is a " + targetMember.getRole().getName() + '.');
      //      return;
      //  }

        Role currentRole = playerFaction.getMember(uuid).getRole();
        Role targetRole = targetMember.getRole();

        if(targetRole == Role.MEMBER && !(currentRole == Role.LEADER || currentRole == Role.COLEADER)){
            sender.sendMessage(Lang.of("Commands-Factions-Promote-CoLeaderRequired"));
            //Need to be co leader or leader to promote members to captains
            return;
        }

        if(targetRole == Role.CAPTAIN && currentRole != Role.LEADER){
            sender.sendMessage(Lang.of("Commands-Factions-Promote-LeaderRequired"));
            return;
        }

        if(targetRole == Role.COLEADER || targetRole == Role.LEADER){
            sender.sendMessage(Lang.of("Commands-Factions-Promote-PromotionTooHigh")
                    .replace("{player}", targetMember.getCachedName()).replace("{playerRole}",
                            targetMember.getRole().getName()));
            //promotion too high
            return;
        }

        targetMember.setRole(targetRole == Role.MEMBER ? Role.CAPTAIN : Role.COLEADER);

        //Role role = Role.CAPTAIN;
        //targetMember.setRole(role);

        playerFaction.broadcast(Lang.of("Commands-Factions-Promote-Broadcast")
                .replace("{player}", targetMember.getRole().getAstrix() + targetMember.getCachedName()));
        //playerFaction.broadcast(Relation.MEMBER.toChatColour() + role.getAstrix() + targetMember.getName() + ChatColor.YELLOW + " has been assigned as a faction captain.");
        return;
    }
     // I DONT KNOW WHY SYSTEM UPDATE REMOVE THIS, WILL BE CHECKED IN ANOTHER DAY.
    /*@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() != Role.LEADER) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        for (Map.Entry<UUID, FactionMember> entry : playerFaction.getMembers().entrySet()) {
            if (entry.getValue().getRole() == Role.MEMBER) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(entry.getKey());
                String targetName = target.getName();
                if (targetName != null) {
                    results.add(targetName);
                }
            }
        }

        return results;
    }*/
}
