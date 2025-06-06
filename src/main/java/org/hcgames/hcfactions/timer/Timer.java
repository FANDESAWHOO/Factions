package org.hcgames.hcfactions.timer;


import lombok.Getter;
import org.hcgames.hcfactions.util.configuration.Config;
import org.mineacademy.fo.settings.Lang;

/**
 * Represents a {@link Timer}, used to manage cooldowns.
 */
public abstract class Timer{

    @Getter
    protected final String name;
    protected final long defaultCooldown;
    protected String displayName;

    /**
     * Constructs a new {@link Timer} with a given name.
     *
     * @param name            the name
     * @param defaultCooldown the default cooldown in milliseconds
     */
    public Timer(String name, long defaultCooldown){
        this.name = name;
        this.defaultCooldown = defaultCooldown;

        displayName = Lang.of("Timer-" + name.replace(" ", "") + "-Name");

        if(displayName == null || displayName.isEmpty() || displayName.equals("Error! Please contact an administrator"))
			displayName = getScoreboardPrefix() + name;
    }

    /**
     * Gets the prefix this {@link Timer} will display on a scoreboard.
     *
     * @return the scoreboard prefix
     */
    public abstract String getScoreboardPrefix();

    /**
     * Gets the display name of this {@link Timer}.
     *
     * @return the display name
     */
    public final String getDisplayName(){
        return displayName;
        //return getScoreboardPrefix() + name;
    }

    /**
     * Loads the {@link Timer} data from storage.
     *
     * @param config the {@link Config} to load from
     */
    public void load(Config config){
    }

    /**
     * Saves the {@link Timer} data to storage.
     *
     * @param config the {@link Config} to save to
     */
    public void onDisable(Config config){
    }
}
