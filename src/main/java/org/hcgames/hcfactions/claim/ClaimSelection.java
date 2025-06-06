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

package org.hcgames.hcfactions.claim;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.util.cuboid.Cuboid;


import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class ClaimSelection implements Cloneable {

    private final UUID uuid;
    private final World world;

    private long lastUpdateMillis;
    private Location pos1;
    private Location pos2;

    /**
     * Constructs a {@link ClaimSelection} from a given {@link World}.
     *
     * @param world the {@link World} to construct from
     */
    public ClaimSelection(World world) {
        this.uuid = UUID.randomUUID();
        this.world = world;
    }

    /**
     * Gets the {@link UUID} of {@link ClaimSelection}.
     *
     * @return the {@link UUID} of this {@link ClaimSelection}
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the {@link World} of this {@link ClaimSelection}.
     *
     * @return the {@link World}
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets the price of this {@link ClaimSelection}.
     *
     * @param playerFaction the {@link Faction} looking up
     * @param selling       if the {@link Faction} is selling this {@link ClaimSelection}
     * @return the price of this {@link ClaimSelection}
     */
    public int getPrice(PlayerFaction playerFaction, boolean selling) {
        requireNonNull(playerFaction, "Player faction cannot be null");
        if (pos1 == null || pos2 == null) {
            return 0;
        } else {
            return JavaPlugin.getPlugin(HCFactions.class).getClaimHandler().calculatePrice(new Cuboid(pos1, pos2),
                    playerFaction.getClaims().size(), selling);
        }
    }

    /**
     * Converts this {@link ClaimSelection} to a {@link Claim}.
     *
     * @param faction the faction this {@link Claim} is for
     * @return the converted {@link Claim} instance
     */
    public Claim toClaim(Faction faction) {
        requireNonNull(faction, "Faction cannot be null");
        return pos1 == null || pos2 == null ? null : new Claim(faction, pos1, pos2);
    }

    /**
     * Gets the time in milliseconds the first or second position was last updated.
     *
     * @return time in milliseconds at last update
     */
    public long getLastUpdateMillis() {
        return lastUpdateMillis;
    }

    /**
     * Gets the first {@link Location} of this {@link ClaimSelection}.
     *
     * @return the first {@link ClaimSelection} {@link Location}
     */
    public Location getPos1() {
        return pos1;
    }

    /**
     * Sets the first {@link Location} of the {@link ClaimSelection}.
     *
     * @param location the {@link Location} to set
     */
    public void setPos1(Location location) {
        requireNonNull(location, "The location cannot be null");
        this.pos1 = location;
        this.lastUpdateMillis = System.currentTimeMillis();
    }

    /**
     * Gets the second {@link Location} of this {@link ClaimSelection}.
     *
     * @return the second {@link ClaimSelection} {@link Location}
     */
    public Location getPos2() {
        return pos2;
    }

    /**
     * Sets the second {@link Location} of the {@link ClaimSelection}.
     *
     * @param location the {@link Location} to set
     */
    public void setPos2(Location location) {
        requireNonNull(location, "The location is null");
        this.pos2 = location;
        this.lastUpdateMillis = System.currentTimeMillis();
    }

    /**
     * Checks if the {@link ClaimSelection} has both {@link Location}s set.
     *
     * @return true if both positions are set
     */
    public boolean hasBothPositionsSet() {
        return pos1 != null && pos2 != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimSelection)) return false;

        ClaimSelection that = (ClaimSelection) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (world != null ? !world.equals(that.world) : that.world != null) return false;
        if (pos1 != null ? !pos1.equals(that.pos1) : that.pos1 != null) return false;
        return !(pos2 != null ? !pos2.equals(that.pos2) : that.pos2 != null);
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (world != null ? world.hashCode() : 0);
        result = 31 * result + (pos1 != null ? pos1.hashCode() : 0);
        result = 31 * result + (pos2 != null ? pos2.hashCode() : 0);
        return result;
    }

    @Override
    public ClaimSelection clone() {
        try {
            return (ClaimSelection) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
