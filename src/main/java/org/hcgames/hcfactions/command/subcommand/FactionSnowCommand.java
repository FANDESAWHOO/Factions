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

package org.hcgames.hcfactions.command.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.command.FactionSubCommand;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.structure.Role;
import org.mineacademy.fo.settings.Lang;

public final class FactionSnowCommand extends FactionSubCommand {

    private final HCFactions plugin;

    public FactionSnowCommand(){
        super("snow");
        setDescription("Toggle snow fall in your faction");
      //  this.permission = "hcf.command.faction.argument." + getName();
        plugin = HCFactions.getInstance();

    }

    @Override
	public String getUsage(){
        return "/f " + label + " snow";
    }

    @Override
    public void onCommand(){
       /* if(!BreakConfig.christmasMap){
            sender.sendMessage(ChatColor.RED + "Requires christmas map break config option.");
            return true;
        }*/

        ClaimableFaction faction;
        boolean own = false;

        if(args.length < 2){
            if(!(sender instanceof Player)){
                sender.sendMessage(ChatColor.RED + "Usage: " + getUsage() + " [factionName]");
                return;
            }

            Player player = (Player) sender;
            if(!plugin.getFactionManager().hasFaction(player)){
                sender.sendMessage(ChatColor.RED + "You are not in a faction.");
                return;
            }

            PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
            if(playerFaction.getMember(player).getRole() == Role.MEMBER){
                sender.sendMessage(Lang.of("Commands-Factions-Kick-OfficerRequired"));
                return;
            }

            faction = playerFaction;
            own = true;
        }else if(sender instanceof ConsoleCommandSender || sender.hasPermission(getPermission() + ".staff")){
            Faction found;

            try{
                found = plugin.getFactionManager().getFaction(args[1]);
            }catch(NoFactionFoundException e){
                sender.sendMessage(ChatColor.RED + "No faction found by name " + args[1]);
                return;
            }

            if(!(found instanceof ClaimableFaction)){
                sender.sendMessage(ChatColor.RED + "You cannot toggle snow for that faction.");
                return;
            }

            faction = (ClaimableFaction) found;
        }else{
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        boolean newState = !faction.isSnowfall();
        faction.setSnowfall(newState);
        sender.sendMessage(Lang.of("commands.snow." + (own ? "own" : "other"), newState ? "enabled" : "disabled", faction.getName()));
        return;
    }
}
