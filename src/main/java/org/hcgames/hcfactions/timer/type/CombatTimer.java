package org.hcgames.hcfactions.timer.type;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.api.FactionsAPI;
import org.hcgames.hcfactions.event.claim.PlayerClaimEnterEvent;
import org.hcgames.hcfactions.event.playerfaction.PlayerJoinFactionEvent;
import org.hcgames.hcfactions.event.playerfaction.PlayerLeaveFactionEvent;
import org.hcgames.hcfactions.timer.PlayerTimer;
import org.hcgames.hcfactions.timer.TimerCooldown;
import org.hcgames.hcfactions.timer.event.TimerStartEvent;
import org.hcgames.hcfactions.util.BukkitUtils;
import org.hcgames.hcfactions.util.DurationFormatter;
import org.hcgames.hcfactions.util.text.CC;
import org.hcgames.hcfactions.visualise.VisualType;
import org.mineacademy.fo.settings.Lang;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Timer used to tag {@link Player}s in combat to prevent entering safe-zones.
 */
public class CombatTimer extends PlayerTimer implements Listener{

    private final HCFactions plugin;

    private String scoreboardPrefix = null;

    public CombatTimer(HCFactions plugin){
        super("Combat", TimeUnit.SECONDS.toMillis(45L));
        this.plugin = plugin;

        scoreboardPrefix = Lang.of("Timer-" + name.replace(" ", "") + "-SBPrefix");

        if(scoreboardPrefix == null || scoreboardPrefix.isEmpty() || scoreboardPrefix.equals("Error! Please contact an administrator"))
			scoreboardPrefix = null;
    }

    @Override
    public String getScoreboardPrefix(){
        if(!(scoreboardPrefix == null)) return scoreboardPrefix;

        return ChatColor.DARK_RED.toString() + ChatColor.BOLD;
    }

    @Override
    public TimerCooldown clearCooldown(@Nullable Player player, UUID playerUUID){
        TimerCooldown cooldown = super.clearCooldown(player, playerUUID);
        if(cooldown != null && player != null)
			plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);

        return cooldown;
    }

    @Override
    public void handleExpiry(@Nullable Player player, UUID playerUUID){
        super.handleExpiry(player, playerUUID);
        if(player != null) plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionJoin(PlayerJoinFactionEvent event){
        Optional<Player> optional = event.getPlayer();
        if(optional.isPresent()){
            Player player = optional.get();
            long remaining = getRemaining(player);
            if(remaining > 0L){
                event.setCancelled(true);
                player.sendMessage(CC.translate(Lang.of("Timer-Combat-CannotJoinFactions")
                        .replace("{timerName}", getDisplayName())
                        .replace("{timeLeft}", DurationFormatter.getRemaining(getRemaining(player), true, false))));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(PlayerLeaveFactionEvent event){
        if(event.isForce()) return;

        Optional<Player> optional = event.getPlayer();
        if(optional.isPresent()){
            Player player = optional.get();
            long remaining = getRemaining(player);
            if(remaining > 0L){
                event.setCancelled(true);

                CommandSender sender = event.getSender();
                if(sender == player) sender.sendMessage(CC.translate(Lang.of("Timer-Combat-CannotKick")
                        .replace("{player}", player.getName())
                        .replace("{timerName}", getDisplayName())
                        .replace("{timeLeft}", DurationFormatter.getRemaining(remaining, true, false))));
				else{
                    sender.sendMessage(CC.translate(Lang.of("Timer-Combat-CannotLeave")
                            .replace("{timerName}", getDisplayName())
                            .replace("{timeLeft}", DurationFormatter.getRemaining(remaining, true, false))));
                    sender.sendMessage(CC.translate("You cannot leave factions whilst your " + getDisplayName() + ChatColor.RED + " timer is active [" +
                            ChatColor.BOLD + DurationFormatter.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]"));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPreventClaimEnter(PlayerClaimEnterEvent event){
        if(event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) return;

        // Prevent entering spawn if the player is spawn tagged.
        Player player = event.getPlayer();
        if(!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone() && getRemaining(player) > 0L){
            event.setCancelled(true);
            player.sendMessage(CC.translate(Lang.of("Timer-Combat-CannotEnter")
                    .replace("{areaName}", event.getToFaction().getFormattedName(player))
                    .replace("{timerName}", getDisplayName())
                    .replace("{timeLeft}", DurationFormatter.getRemaining(getRemaining(player), true, false))));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        Player attacker = BukkitUtils.getFinalAttacker(event, true);
        Entity entity;
        if(attacker != null && (entity = event.getEntity()) instanceof Player){
            Player attacked = (Player) entity;
            setCooldown(attacker, attacker.getUniqueId(), defaultCooldown, false);
            setCooldown(attacked, attacked.getUniqueId(), defaultCooldown, false);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStart(TimerStartEvent event){
        if(event.getTimer() == this){
            Optional<Player> optional = event.getPlayer();
            if(optional.isPresent()){
                Player player = optional.get();
                player.sendMessage(Lang.of("Timer-Combat-InCombat"));
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event){
        clearCooldown(event.getPlayer(), event.getPlayer().getUniqueId());
    }

    @Override
    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite, @Nullable Predicate<Long> currentCooldownPredicate){
        if(player != null && FactionsAPI.getFactionAt(player.getLocation()).isSafezone())
			return false;
        return super.setCooldown(player, playerUUID, duration, overwrite, currentCooldownPredicate);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPreventClaimEnterMonitor(PlayerClaimEnterEvent event){
        if(event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && (!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone()))
			clearCooldown(event.getPlayer(), event.getPlayer().getUniqueId());
    }
}
