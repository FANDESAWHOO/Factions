 package org.hcgames.hcfactions.command;

import lombok.Getter;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.RegenStatus;
import org.hcgames.hcfactions.util.DurationFormatter;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.settings.Lang;

@AutoRegister
public final class RegenCommand extends SimpleCommand {

    private final HCFactions plugin;
    /**
     * The singleton of this class
     */
    @Getter
    private final static SimpleCommand instance = new RegenCommand();

    private RegenCommand(){
        super("regen");
        plugin = HCFactions.getInstance();
    }


    public long getRemainingRegenMillis(PlayerFaction faction) {
        long millisPassedSinceLastUpdate = System.currentTimeMillis() - faction.getLastDtrUpdateTimestamp();
        double dtrRequired = faction.getMaximumDeathsUntilRaidable() - faction.getDeathsUntilRaidable();
        return (long) ((10 / 60) * dtrRequired) - millisPassedSinceLastUpdate;
    }

    /**
     * Executed when the command is run. You can get the variables sender and args directly,
     * and use convenience checks in the simple command class.
     */
    @Override
    protected void onCommand() {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.of("Error-Messages.PlayerOnly"));
            return ;
        }

        Player player = (Player) sender;
        PlayerFaction playerFaction;

        if(!plugin.getFactionManager().hasFaction(player)){
            sender.sendMessage(Lang.of("Error-Messages.NotInFaction"));
            return;
        }

        playerFaction = plugin.getFactionManager().getPlayerFaction(player);

        RegenStatus regenStatus = playerFaction.getRegenStatus();
        switch (regenStatus) {
            case FULL:
                sender.sendMessage(Lang.of("Commands.Regen.Full"));
                return;
            case PAUSED:
                sender.sendMessage(Lang.of("Commands.Regen.Paused")
                        .replace("{dtrFreezeTimeLeft}", DurationFormatUtils.formatDurationWords(playerFaction.getRemainingRegenerationTime(), true, true)));
                return;
            case REGENERATING:
                sender.sendMessage(Lang.of("Commands.Regen.Regenerating")
                        .replace("{regenSymbol}", regenStatus.getSymbol())
                        .replace("{factionDeathsUntilRaidable}", String.valueOf(playerFaction.getDeathsUntilRaidable()))
                        .replace("{factionDTRIncrement}", String.valueOf(Configuration.factionDtrUpdateIncrement))
                        .replace("{factionDTRIncrementWords}", String.valueOf(DurationFormatter.getRemaining(Configuration.factionDtrUpdateIncrement.longValue(),false))
                                .replace("{factionDTRETA}", DurationFormatUtils.formatDurationWords(getRemainingRegenMillis(playerFaction), true, true))));
                return;
        }

        sender.sendMessage(Lang.of("Commands.Regen.Unknown"));

    }
}
