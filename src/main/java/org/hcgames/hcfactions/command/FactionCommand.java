package org.hcgames.hcfactions.command;

import lombok.Getter;
import org.hcgames.hcfactions.command.argument.staff.*;
import org.hcgames.hcfactions.command.subcommand.*;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.LinkedList;
import java.util.List;

/**
 *  Class to handle the command and tab completion for the faction command.
 */

@AutoRegister
public final class FactionCommand extends SimpleCommand {

	/**
	 * The singleton of this class
	 */
	@Getter
	private final static FactionCommand instance = new FactionCommand();
	@Getter
	private final List<FactionSubCommand> commands = new LinkedList<>();
	private final FactionHelpCommand help;
    public void addArgument(FactionSubCommand class1){
		commands.add(class1);
	}
	public FactionCommand() {
		super("faction | fac | f");
		addArgument(new FactionAcceptCommand());
		addArgument(new FactionAllyCommand());
		addArgument(new FactionAnnouncementCommand());
		addArgument(new FactionChatCommand());
		//TODO addArgument(new FactionChatSpyArgument());
		addArgument(new FactionClaimCommand());
		addArgument(new FactionClaimChunkCommand());
		addArgument(new FactionClaimForArgument());
		addArgument(new FactionAcceptCommand());
		addArgument(new FactionClearClaimsArgument());
		addArgument(new FactionAcceptCommand());
		addArgument(new FactionAcceptCommand());
		addArgument(new FactionDepositCommand());
		addArgument(new FactionDisbandCommand());
		addArgument(new FactionSetDtrRegenArgument());
		addArgument(new FactionForceDemoteArgument());
		addArgument(new FactionForceJoinArgument());
		addArgument(new FactionForceKickArgument());
		addArgument(new FactionForceLeaderArgument());
		addArgument(new FactionForcePromoteArgument());
		addArgument(new FactionForceUnclaimHereArgument());
		addArgument(help = new FactionHelpCommand());
		addArgument(new FactionHomeCommand());
		addArgument(new FactionAcceptCommand());
		addArgument(new FactionInvitesCommand());
		addArgument(new FactionKickCommand());
		addArgument(new FactionAcceptCommand());
		addArgument(new FactionLeaveCommand());
		addArgument(new FactionListCommand());
		addArgument(new FactionMapCommand());
		addArgument(new FactionMessageCommand());
		addArgument(new FactionMuteArgument());
		addArgument(new FactionBanArgument());
		addArgument(new FactionOpenCommand());
		addArgument(new FactionRemoveArgument());
		addArgument(new FactionAcceptCommand());
		addArgument(new FactionPromoteCommand());
		addArgument(new FactionSetDtrArgument());
		addArgument(new FactionSetDeathbanMultiplierArgument());
		addArgument(new FactionSetHomeCommand());
		addArgument(new FactionShowCommand());
		addArgument(new FactionStuckCommand());
		addArgument(new FactionUnclaimCommand());
		addArgument(new FactionUnallyArgument());
		addArgument(new FactionUninviteCommand());
		addArgument(new FactionWithdrawCommand());
		//addArgument(new FactionLivesCommand());
		addArgument(new FactionAcceptCommand());
		addArgument(new FactionFocusCommand());
		addArgument(new FactionRemoveCooldownCommand());
		addArgument(new FactionReloadArgument());
		addArgument(new FactionForceRenameArgument());
		addArgument(new FactionSnowCommand());
		//addArgument(new FactionPastFactionsCommand(plugin));
	}

	public FactionSubCommand getSubCommand(String key) {
		for(FactionSubCommand subCommand : commands)
			if(subCommand.getName().equalsIgnoreCase(key)) return subCommand;


		return null;
	}


	/**
	 * Executed when the command is run. You can get the variables sender and args directly,
	 * and use convenience checks in the simple command class.
	 */
	@Override
	protected void onCommand() {
		checkPerm(getPermission());
		if(args.length >= 1){
			FactionSubCommand argument = getSubCommand(args[0]);
			if(argument == null) tell(Lang.of("Commands-Unknown-Subcommand")
					.replace("{subCommand}", args[0])
					.replace("{commandLabel}", getName()));
			else argument.execute(sender, getLabel(), args);
		} else help.execute(sender, getLabel(), args);
	}
}
