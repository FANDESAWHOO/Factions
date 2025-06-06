package org.hcgames.hcfactions.timer.type;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.manager.FactionManager;
import org.hcgames.hcfactions.timer.PlayerTimer;
import org.hcgames.hcfactions.timer.TimerCooldown;
import org.hcgames.hcfactions.util.text.CC;
import org.mineacademy.fo.settings.Lang;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Timer that handles teleportation warmups for {@link Player}s.
 */
public class TeleportTimer extends PlayerTimer implements Listener{

    private final Map<UUID, Location> destinationMap = new HashMap<>();
    private final HCFactions plugin;

    private String scoreboardPrefix = null;

    public TeleportTimer(HCFactions plugin){
        super("Teleport", TimeUnit.SECONDS.toMillis(10L), false);
        this.plugin = plugin;

        scoreboardPrefix = Lang.of("Timer-" + name.replace(" ", "") + "-SBPrefix");

        if(scoreboardPrefix == null || scoreboardPrefix.isEmpty() || scoreboardPrefix.equals("Error! Please contact an administrator"))
			scoreboardPrefix = null;
    }

    @Override
    public void handleExpiry(@Nullable Player player, UUID userUUID){
        if(player != null){
            Location destination = destinationMap.remove(userUUID);
            if(destination != null){
                destination.getChunk(); // pre-load the chunk before teleport.
                player.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND);
            }
        }
    }

    /**
     * Gets the {@link Location} this {@link TeleportTimer} will send to.
     *
     * @param player the {@link Player} to get for
     * @return the {@link Location}
     */
    public Location getDestination(Player player){
        return destinationMap.get(player.getUniqueId());
    }

    @Override
    public String getScoreboardPrefix(){
        if(!(scoreboardPrefix == null)) return scoreboardPrefix;

        return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD;
    }

    @Override
    public TimerCooldown clearCooldown(UUID uuid){
        TimerCooldown runnable = super.clearCooldown(uuid);
        if(runnable != null){
            destinationMap.remove(uuid);
            return runnable;
        }

        return null;
    }

    /**
     * Gets the amount of enemies nearby a {@link Player}.
     *
     * @param player   the {@link Player} to get for
     * @param distance the radius to get within
     * @return the amount of players within enemy distance
     */
    public int getNearbyEnemies(Player player, int distance){
        FactionManager factionManager = plugin.getFactionManager();
        Faction playerFaction = factionManager.hasFaction(player) ? factionManager.getPlayerFaction(player) : null;
        int count = 0;

        Collection<Entity> nearby = player.getNearbyEntities(distance, distance, distance);
        for(Entity entity : nearby)
			if (entity instanceof Player) {
				Player target = (Player) entity;

				// If the nearby player or sender cannot see each-other, continue.
				if (!target.canSee(player) || !player.canSee(target)) continue;

				if (playerFaction == null || (factionManager.hasFaction(target) && factionManager.getPlayerFaction(target) != playerFaction))
					count++;
			}

        return count;
    }

    /**
     * Teleports a {@link Player} to a {@link Location} with a delay.
     *
     * @param player        the {@link Player} to teleport
     * @param location      the {@link Location} to teleport to
     * @param millis        the time in milliseconds until teleport
     * @param warmupMessage the message to send whilst waiting
     * @param cause         the cause for teleporting
     * @return true if {@link Player} was successfully teleported
     */
    public boolean teleport(Player player, Location location, long millis, String warmupMessage, PlayerTeleportEvent.TeleportCause cause){
        cancelTeleport(player, null); // cancels any previous teleport for the player.

        boolean result;
        if(millis <= 0){ // if there is no delay, just instantly teleport.
            result = player.teleport(location, cause);
            clearCooldown(player.getUniqueId());
        }else{
            UUID uuid = player.getUniqueId();
            player.sendMessage(warmupMessage);
            destinationMap.put(uuid, location.clone());
            setCooldown(player, uuid, millis, true, null);
            result = true;
        }

        return result;
    }

    /**
     * Cancels a {@link Player}s' teleport process for a given reason.
     *
     * @param player the {@link Player} to cancel for
     * @param reason the reason for cancelling
     */
    public void cancelTeleport(Player player, String reason){
        UUID uuid = player.getUniqueId();
        if(getRemaining(uuid) > 0L){
            clearCooldown(uuid);
            if(reason != null && !reason.isEmpty()) player.sendMessage(CC.translate(reason));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event){
        Location from = event.getFrom();
        Location to = event.getTo();
        // Player didn't move a block.
        if(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
			return;

        cancelTeleport(event.getPlayer(), Lang.of("Timer-Teleport-MovedABlock"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof Player)
			cancelTeleport((Player) entity, Lang.of("Timer-Teleport-TookDamage"));
    }
}
