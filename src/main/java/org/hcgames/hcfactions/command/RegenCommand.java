/*
 *   COPYRIGHT NOTICE
 *
 *   Copyright (C) 2016, SystemUpdate, <admin@systemupdate.io>.
 *
 *   All rights reserved.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 *   NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 *   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 *   OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 *   OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *   Except as contained in this notice, the name of a copyright holder shall not
 *   be used in advertising or otherwise to promote the sale, use or other dealings
 *   in this Software without prior written authorization of the copyright holder.
 */

package org.hcgames.hcfactions.command;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.RegenStatus;
import org.hcgames.hcfactions.util.DurationFormatter;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Collections;
import java.util.List;


public class RegenCommand extends SimpleCommand {

    private final HCFactions plugin;

    public RegenCommand(){
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
