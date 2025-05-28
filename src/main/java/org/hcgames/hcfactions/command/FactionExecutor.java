//package org.hcgames.hcfactions.command;


/**import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.argument.*;
import org.hcgames.hcfactions.command.argument.staff.*;
import org.hcgames.hcfactions.command.subcommand.FactionReviveArgument;
import org.hcgames.hcfactions.command.subcommand.FactionSetHomeArgument;
import org.hcgames.hcfactions.command.subcommand.FactionShowArgument;
import org.hcgames.hcfactions.command.subcommand.FactionSnowArgument;
import org.hcgames.hcfactions.command.subcommand.FactionStuckArgument;
import org.hcgames.hcfactions.command.subcommand.FactionUnallyArgument;
import org.hcgames.hcfactions.command.subcommand.FactionUnclaimArgument;
import org.hcgames.hcfactions.command.subcommand.FactionUninviteArgument;
import org.hcgames.hcfactions.command.subcommand.FactionWithdrawArgument;

import technology.brk.util.command.ArgumentExecutor;
import technology.brk.util.command.CommandArgument;


 Class to handle the command and tab completion for the faction command.
 
public class FactionExecutor extends ArgumentExecutor {

    private final CommandArgument helpArgument;

    public FactionExecutor(HCFactions plugin) {
        super("faction");

        addArgument(new FactionAcceptArgument(plugin));
        addArgument(new FactionAllyArgument(plugin));
        addArgument(new FactionAnnouncementArgument(plugin));
        addArgument(new FactionChatArgument(plugin));
        //TODO addArgument(new FactionChatSpyArgument(plugin));
        addArgument(new FactionClaimArgument(plugin));
        addArgument(new FactionClaimChunkArgument(plugin));
        addArgument(new FactionClaimForArgument(plugin));
        addArgument(new FactionClaimsArgument(plugin));
        addArgument(new FactionClearClaimsArgument(plugin));
        addArgument(new FactionCreateArgument(plugin));
        addArgument(new FactionDemoteArgument(plugin));
        addArgument(new FactionDepositArgument(plugin));
        addArgument(new FactionDisbandArgument(plugin));
        addArgument(new FactionSetDtrRegenArgument(plugin));
        addArgument(new FactionCreateSystemArgument(plugin));
        addArgument(new FactionForceDemoteArgument(plugin));
        addArgument(new FactionForceJoinArgument(plugin));
        addArgument(new FactionForceKickArgument(plugin));
        addArgument(new FactionForceLeaderArgument(plugin));
        addArgument(new FactionForcePromoteArgument(plugin));
        addArgument(new FactionForceUnclaimHereArgument(plugin));
        addArgument(new FactionFriendlyFireArgument(plugin));
        addArgument(helpArgument = new FactionHelpArgument(this, plugin));
        addArgument(new FactionHomeArgument(this, plugin));
        addArgument(new FactionInviteArgument(plugin));
        addArgument(new FactionInvitesArgument(plugin));
        addArgument(new FactionKickArgument(plugin));
        addArgument(new FactionLeaderArgument(plugin));
        addArgument(new FactionLeaveArgument(plugin));
        addArgument(new FactionListArgument(plugin));
        addArgument(new FactionMapArgument(plugin));
        addArgument(new FactionMessageArgument(plugin));
        addArgument(new FactionMuteArgument(plugin));
        addArgument(new FactionBanArgument(plugin));
        addArgument(new FactionOpenArgument(plugin));
        addArgument(new FactionRemoveArgument(plugin));
        addArgument(new FactionRenameArgument(plugin));
        addArgument(new FactionPromoteArgument(plugin));
        addArgument(new FactionSetDtrArgument(plugin));
        addArgument(new FactionSetDeathbanMultiplierArgument(plugin));
        addArgument(new FactionSetHomeArgument(plugin));
        addArgument(new FactionShowArgument(plugin));
        addArgument(new FactionStuckArgument(plugin));
        addArgument(new FactionUnclaimArgument(plugin));
        addArgument(new FactionUnallyArgument(plugin));
        addArgument(new FactionUninviteArgument(plugin));
        addArgument(new FactionWithdrawArgument(plugin));
        addArgument(new FactionLivesArgument(plugin));
        addArgument(new FactionReviveArgument(plugin));
        addArgument(new FactionFocusArgument(plugin));
        addArgument(new FactionRemoveCooldownArgument(plugin));
        addArgument(new FactionReloadArgument(plugin));
        addArgument(new FactionForceRenameArgument(plugin));
        addArgument(new FactionSnowArgument(plugin));
        addArgument(new FactionPastFactionsArgument(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            helpArgument.onCommand(sender, command, label, args);
            return true;
        }

        CommandArgument argument = getArgument(args[0]);
		//   sender.sendMessage(HCF.getPlugin().getMessagesOld().getString("Commands-Unknown-Subcommand")
		//         .replace("{subCommand}", args[0])
		//       .replace("{commandLabel}", command.getName()));
		if (argument != null) {
            String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                argument.onCommand(sender, command, label, args);
                return true;
            }
        }else return true;

        helpArgument.onCommand(sender, command, label, args);
        return true;
    }
}*/
