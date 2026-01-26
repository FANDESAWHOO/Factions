package org.hcgames.hcfactions.command;

import java.util.LinkedList;
import java.util.List;

import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.subcommand.*;
import org.hcgames.hcfactions.command.subcommand.staff.FactionBanCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionClaimForCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionClearClaimsCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceDemoteCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceJoinCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceKickCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceLeaderCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForcePromoteCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceRenameCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionForceUnclaimHereCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionMuteCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionReloadCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionRemoveCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionSaveCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionSetDeathbanMultiplierCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionSetDtrCommand;
import org.hcgames.hcfactions.command.subcommand.staff.FactionSetDtrRegenCommand;

import com.minnymin.command.Command;
import com.minnymin.command.CommandArgs;

import lombok.Getter;

public class FactionCommand {

	private final FactionHelpCommand help;
    @Getter private static FactionCommand instance = new FactionCommand();
	@Getter
	private final List<FactionCommand> commands = new LinkedList<>();
    public FactionCommand() {
		HCFactions.getInstance().getCommandFramework().registerCommands(this);
		HCFactions.getInstance().getCommandFramework().registerHelp();
		help = new FactionHelpCommand();
		new FactionAcceptCommand();
		new FactionAllyCommand();
		new FactionAnnouncementCommand();
		new FactionChatCommand();
		//TODO new FactionChatSpyArgument());
		new FactionClaimCommand();
		new FactionClaimChunkCommand();
		new FactionClaimForCommand();
		new FactionClearClaimsCommand();
		new FactionDepositCommand();
		new FactionDisbandCommand();
		new FactionSetDtrRegenCommand();
		new FactionForceDemoteCommand();
		new FactionForceJoinCommand();
		new FactionForceKickCommand();
		new FactionForceLeaderCommand();
		new FactionForcePromoteCommand();
		new FactionForceUnclaimHereCommand();
		new FactionSaveCommand();
		new FactionHomeCommand();
		new FactionInvitesCommand();
		new FactionKickCommand();
		new FactionLeaveCommand();
		new FactionListCommand();
		new FactionMapCommand();
		new FactionMessageCommand();
		new FactionMuteCommand();
		new FactionBanCommand();
		new FactionOpenCommand();
		new FactionRemoveCommand();
		new FactionAcceptCommand();
		new FactionPromoteCommand();
		new FactionSetDtrCommand();
		new FactionSetDeathbanMultiplierCommand();
		new FactionSetHomeCommand();
		new FactionShowCommand();
		new FactionStuckCommand();
		new FactionUnclaimCommand();
		new FactionUnallyArgument();
		new FactionUninviteCommand();
		new FactionWithdrawCommand();
		new FactionInviteCommand();
		new FactionInvitesCommand();
		new FactionFriendlyFireCommand();
		//new FactionLivesCommand();
		new FactionFocusCommand();
		new FactionRemoveCooldownCommand();
		new FactionReloadCommand();
		new FactionForceRenameCommand();
		new FactionSnowCommand();
		new FactionCreateCommand();
		new FactionPastFactionsCommand();
    }
    
	
	public void addArgument(FactionCommand class1) {
		commands.add(class1);
	}

    
    @Command(name = "faction", description = "The main command for Faction", aliases = { "f" ,"fac", "team", "t"}, usage = "/<command>",  playerOnly = false, adminsOnly = false)
    public void onCommand(CommandArgs arg) {
    	if (arg.getArgs().length == 0) {
    //		help.execute(arg.getSender(), arg.getLabel(), arg.getArgs());
    	}
    }

/*	@Override
	protected List<String> tabComplete() {
		if (args.length == 1) return commands.stream()
				.flatMap(sub -> Stream.concat(
						Stream.of(sub.getName()),
						Arrays.stream(sub.getAliases())
				))
				.filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
				.collect(Collectors.toList());
		if (args.length >= 2) {
			FactionSubCommand sub = getSubCommand(args[0]);
			if (sub != null) {
				String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
				return sub.onTabComplete(sender, null, getLabel(), subArgs);
			}
		}
		return Collections.emptyList();
	}*/
}
