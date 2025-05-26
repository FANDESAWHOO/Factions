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



import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.Faction;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.settings.Lang;

import java.util.Collections;
import java.util.List;


public class LocationCommand extends SimpleCommand {

    private final HCFactions plugin;

    public LocationCommand() {
        super("location | loc | whereami");
        plugin = HCFactions.getInstance();
    }

    /**
     * Executed when the command is run. You can get the variables sender and args directly,
     * and use convenience checks in the simple command class.
     */
    @Override
    protected void onCommand() {
        Player target;
        if (args.length >= 1) {
            target = plugin.getServer().getPlayer(args[0]);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
                sender.sendMessage(Lang.of("Commands.Location.Usage")
                      .replace("{commandLabel}", getLabel()));
            return;
        }

        if (target == null || (sender instanceof Player && !((Player) sender).canSee(target))) {
             sender.sendMessage(Lang.of("Commands.Location.Output")
                   .replace("{player}", args[0]));
            return;
        }

        Location location = target.getLocation();
        Faction factionAt = plugin.getFactionManager().getFactionAt(location);

        sender.sendMessage(Lang.of("Commands.Location.Output")
                .replace("{player}", target.getName())
                .replace("{factionName}", factionAt.getFormattedName(sender))
                .replace("{isDeathBanLocation}", factionAt.isSafezone() ?
                        Lang.of("Commands.Location.NonDeathban") :
                        Lang.of("Commands.Location.Deathban")));
    }

    @Override
    protected List<String> tabComplete() {
        return  args.length == 1 && sender.hasPermission(getPermission()) ? null : Collections.emptyList();
    }
}
