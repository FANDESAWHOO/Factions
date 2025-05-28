package org.hcgames.hcfactions.command;


import lombok.Getter;

import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.subcommand.*;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AutoRegister
public class FactionCommands extends SimpleCommandGroup {

	/**
	 * The singleton of this class
	 */
	@Getter
	private final static SimpleCommandGroup instance = new FactionCommands();
    @Getter
	private static final List<SimpleSubCommand> arguments = new ArrayList<>();
    private final HCFactions plugin = HCFactions.getInstance();

    public final void addArgument(final SimpleSubCommand command) {
    	registerSubcommand(command.getClass());
    }

	/**
	 * Extending method to register subcommands, call
	 * {@link #registerSubcommand(SimpleSubCommand)} and {@link #registerHelpLine(String...)}
	 * there for your command group.
	 */
	@Override
	protected void registerSubcommands() {
	/*	registerSubcommand(FactionDepositCommand.class);
		registerSubcommand(FactionAcceptCommand.class);
		registerSubcommand(FactionPromoteCommand.class);
		registerSubcommand(FactionOpenCommand.class);
		registerSubcommand(FactionSnowCommand.class);
		registerSubcommand(FactionRenameCommand.class);
		registerSubcommand(FactionSetHomeCommand.class);
		registerSubcommand(FactionShowCommand.class);
		registerSubcommand(FactionRemoveCooldownCommand.class);
		registerSubcommand(FactionWithdrawCommand.class);
		registerSubcommand(FactionUnclaimCommand.class);
		registerSubcommand(FactionStuckCommand.class);
		registerSubcommand(FactionUnallyArgument.class);
    	registerSubcommand(FactionCreateCommand.class);
		registerSubcommand(FactionClaimCommand.class);
		registerSubcommand(FactionClaimChunkCommand.class);
		registerSubcommand(FactionDisbandCommand.class);
		registerSubcommand(FactionUnclaimCommand.class);
		registerSubcommand(FactionUninviteCommand.class);
		registerSubcommand(FactionMessageCommand.class);
		registerSubcommand(FactionReviveCommand.class);
		registerSubcommand(FactionListCommand.class);*/
     
        addArgument(new FactionAcceptCommand(plugin));
        addArgument(new FactionAllyCommand());
        addArgument(new FactionAnnouncementCommand());
        addArgument(new FactionChatCommand());
        //TODO addArgument(new FactionChatSpyArgument(plugin));
        addArgument(new FactionClaimCommand(plugin));
        addArgument(new FactionClaimChunkCommand());
      //  addArgument(new FactionClaimForCommand(plugin));
        addArgument(new FactionClaimsCommand());
       // addArgument(new FactionClearClaimsCommand(plugin));
        addArgument(new FactionCreateCommand());
        addArgument(new FactionDemoteCommand());
        addArgument(new FactionDepositCommand());
        addArgument(new FactionDisbandCommand());
     /*   addArgument(new FactionSetDtrRegenCommand(plugin));
        addArgument(new FactionCreateSystemCommand(plugin));
        addArgument(new FactionForceDemoteCommand(plugin));
        addArgument(new FactionForceJoinCommand(plugin));
        addArgument(new FactionForceKickCommand(plugin));
        addArgument(new FactionForceLeaderCommand(plugin));
        addArgument(new FactionForcePromoteCommand(plugin));
        addArgument(new FactionForceUnclaimHereCommand(plugin));*/
        addArgument(new FactionFriendlyFireCommand(plugin));
        addArgument(new FactionHelpCommand());
        addArgument(new FactionHomeCommand());
        addArgument(new FactionInviteCommand());
        addArgument(new FactionInvitesCommand());
        addArgument(new FactionKickCommand());
        addArgument(new FactionLeaderCommand());
        addArgument(new FactionLeaveCommand());
        addArgument(new FactionListCommand());
        addArgument(new FactionMapCommand());
        addArgument(new FactionMessageCommand(plugin));
        //addArgument(new FactionMuteCommand(plugin));
        //addArgument(new FactionBanCommand(plugin));
        addArgument(new FactionOpenCommand(plugin));
       // addArgument(new FactionRemoveCommand(plugin));
        addArgument(new FactionRenameCommand(plugin));
        addArgument(new FactionPromoteCommand(plugin));
       // addArgument(new FactionSetDtrCommand(plugin));
       // addArgument(new FactionSetDeathbanMultiplierCommand(plugin));
        addArgument(new FactionSetHomeCommand(plugin));
        addArgument(new FactionShowCommand(plugin));
        addArgument(new FactionStuckCommand(plugin));
        addArgument(new FactionUnclaimCommand(plugin));
      //  addArgument(new FactionUnallyCommand(plugin));
        addArgument(new FactionUninviteCommand(plugin));
        addArgument(new FactionWithdrawCommand(plugin));
     //   addArgument(new FactionLivesCommand(plugin));
        addArgument(new FactionReviveCommand(plugin));
     //   addArgument(new FactionFocusCommand(plugin));
        addArgument(new FactionRemoveCooldownCommand(plugin));
      //  addArgument(new FactionReloadCommand(plugin));
      //  addArgument(new FactionForceRenameCommand(plugin));
        addArgument(new FactionSnowCommand(plugin));
    //    addArgument(new FactionPastFactionsCommand(plugin));
		
	}
	private FactionCommands(){
		super("faction", Arrays.asList("f","fac"));
	}

}
