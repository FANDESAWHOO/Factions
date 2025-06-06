package org.hcgames.hcfactions.timer.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.hcgames.hcfactions.timer.Timer;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Event called when a {@link Timer} expires.
 */
public class TimerExpireEvent extends Event{

    private static final HandlerList handlers = new HandlerList();
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private Optional<Player> player;

    public TimerExpireEvent(Timer timer){
        userUUID = Optional.empty();
        this.timer = timer;
    }

    public TimerExpireEvent(UUID userUUID, Timer timer){
        this.userUUID = Optional.ofNullable(userUUID);
        this.timer = timer;
    }

    public TimerExpireEvent(Player player, Timer timer){
        Objects.requireNonNull(player);

        this.player = Optional.of(player);
        userUUID = Optional.of(player.getUniqueId());
        this.timer = timer;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    public Optional<Player> getPlayer(){
        if(player == null)
			player = userUUID.isPresent() ? Optional.of(Bukkit.getPlayer(userUUID.get())) : Optional.empty();

        return player;
    }

    /**
     * Gets the optional UUID of the user this has expired for.
     * <p>This may return absent if the timer is not of a player type</p>
     *
     * @return the expiring user UUID or {@link Optional#empty()}
     */
    public Optional<UUID> getUserUUID(){
        return userUUID;
    }

    /**
     * Gets the {@link Timer} that was expired.
     *
     * @return the expiring timer
     */
    public Timer getTimer(){
        return timer;
    }

    @Override
    public HandlerList getHandlers(){
        return handlers;
    }
}
