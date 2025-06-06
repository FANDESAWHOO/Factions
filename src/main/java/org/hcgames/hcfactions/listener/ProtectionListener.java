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

package org.hcgames.hcfactions.listener;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.material.Cauldron;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.event.claim.PlayerClaimEnterEvent;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.faction.system.WarzoneFaction;
import org.hcgames.hcfactions.structure.Raidable;
import org.hcgames.hcfactions.structure.Role;
import org.hcgames.hcfactions.util.BukkitUtils;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.Lang;

import javax.annotation.Nullable;
/**
 * This need to be reformed to use
 * CompMaterial or other CrossVersion
 * Class Utility!
 */
public class ProtectionListener implements Listener {

    public static final String PROTECTION_BYPASS_PERMISSION = "hcf.faction.protection.bypass";

    // List of materials a player can not right click in enemy territory. ~No such ImmutableEnumMultimap in current Guava build :/
    private static final ImmutableMultimap<Material, Material> ITEM_ON_BLOCK_RIGHT_CLICK_DENY = ImmutableMultimap.<Material, Material>builder().
            put(CompMaterial.DIAMOND_HOE.getMaterial(), CompMaterial.GRASS_BLOCK.getMaterial()).
            put(CompMaterial.GOLDEN_HOE.getMaterial(), CompMaterial.GRASS_BLOCK.getMaterial()).
            put(CompMaterial.IRON_HOE.getMaterial(), CompMaterial.GRASS_BLOCK.getMaterial()).
            put(CompMaterial.STONE_HOE.getMaterial(), CompMaterial.GRASS_BLOCK.getMaterial()).
            put(CompMaterial.WOODEN_HOE.getMaterial(), CompMaterial.GRASS_BLOCK.getMaterial()).
            build();

    // List of materials a player can not right click in enemy territory.
    private static final ImmutableSet<Material> BLOCK_RIGHT_CLICK_DENY = Sets.immutableEnumSet(
            CompMaterial.RED_BED.getMaterial(),
            CompMaterial.RED_BED.getMaterial(),
            CompMaterial.BEACON.getMaterial(),
            CompMaterial.OAK_FENCE_GATE.getMaterial(),
            CompMaterial.IRON_DOOR.getMaterial(),
            CompMaterial.OAK_TRAPDOOR.getMaterial(),
            CompMaterial.OAK_DOOR.getMaterial(),
            CompMaterial.DARK_OAK_DOOR.getMaterial(),
            CompMaterial.IRON_DOOR.getMaterial(),
            CompMaterial.CHEST.getMaterial(),
            CompMaterial.TRAPPED_CHEST.getMaterial(),
            CompMaterial.FURNACE.getMaterial(),
            CompMaterial.BREWING_STAND.getMaterial(),
            CompMaterial.HOPPER.getMaterial(),
            CompMaterial.DROPPER.getMaterial(),
            CompMaterial.DISPENSER.getMaterial(),
            CompMaterial.STONE_BUTTON.getMaterial(),
            CompMaterial.OAK_BUTTON.getMaterial(),
            CompMaterial.ENCHANTING_TABLE.getMaterial(),
            CompMaterial.CRAFTING_TABLE.getMaterial(),
            CompMaterial.ANVIL.getMaterial(),
            CompMaterial.LEVER.getMaterial(),
            CompMaterial.FIRE.getMaterial());

    private final HCFactions plugin;
    @Getter
    private static final ProtectionListener protectionListener = new ProtectionListener();

    private ProtectionListener(){
        plugin = HCFactions.getInstance();
    }

    private void handleMove(PlayerMoveEvent event, PlayerClaimEnterEvent.EnterCause enterCause) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
			return;

        Player player = event.getPlayer();
        boolean cancelled = false;

        Faction fromFaction = plugin.getFactionManager().getFactionAt(from);
        Faction toFaction = plugin.getFactionManager().getFactionAt(to);

       if (fromFaction != toFaction) {
           /* if(enterCause.equals(PlayerClaimEnterEvent.EnterCause.TELEPORT) && fromFaction instanceof CapturableFaction){
                CapturableFaction capturableFaction = (CapturableFaction) fromFaction;
                for (CaptureZone captureZone : capturableFaction.getCaptureZones()) {
                    Cuboid cuboid = captureZone.getCuboid();
                    if (cuboid == null) {
                        continue;
                    }

                    if (cuboid.contains(from)) {
                        if (!cuboid.contains(to)) {
                            CaptureZoneLeaveEvent calledEvent = new CaptureZoneLeaveEvent(player, capturableFaction, captureZone);
                            Bukkit.getPluginManager().callEvent(calledEvent);
                            cancelled = calledEvent.isCancelled();
                            break;
                        }
                    }
                }
            }*/

            PlayerClaimEnterEvent calledEvent = new PlayerClaimEnterEvent(player, from, to, fromFaction, toFaction, enterCause);
            Bukkit.getPluginManager().callEvent(calledEvent);
            cancelled = calledEvent.isCancelled();
        } /*else if (toFaction instanceof CapturableFaction) {
            CapturableFaction capturableFaction = (CapturableFaction) toFaction;
            for (CaptureZone captureZone : capturableFaction.getCaptureZones()) {
                Cuboid cuboid = captureZone.getCuboid();
                if (cuboid == null) {
                    continue;
                }

                if (cuboid.contains(from)) {
                    if (!cuboid.contains(to)) {
                        CaptureZoneLeaveEvent calledEvent = new CaptureZoneLeaveEvent(player, capturableFaction, captureZone);
                        Bukkit.getPluginManager().callEvent(calledEvent);
                        cancelled = calledEvent.isCancelled();
                        break;
                    }
                } else {
                    if (cuboid.contains(to)) {
                        CaptureZoneEnterEvent calledEvent = new CaptureZoneEnterEvent(player, capturableFaction, captureZone);
                        Bukkit.getPluginManager().callEvent(calledEvent);
                        cancelled = calledEvent.isCancelled();
                        break;
                    }
                }
            }
        }*/

        if (cancelled) if (enterCause == PlayerClaimEnterEvent.EnterCause.TELEPORT) event.setCancelled(true);
		else {
			from.setX(from.getBlockX() + 0.5);
			from.setZ(from.getBlockZ() + 0.5);
			event.setTo(from);
		}
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        handleMove(event, PlayerClaimEnterEvent.EnterCause.MOVEMENT);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerTeleportEvent event) {
        handleMove(event, PlayerClaimEnterEvent.EnterCause.TELEPORT);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        switch (event.getCause()) {
            case FLINT_AND_STEEL:
            case ENDER_CRYSTAL:
                return;
        }

        Faction factionAt = plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) event.setCancelled(true);
    }

    // Original source by mFactions: https://github.com/MassiveCraft/Factions/blob/dab81ede383aeb76606daf5a3c859775e1b3778/src/com/massivecraft/factions/engine/EngineExploit.java
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonExtend(BlockPistonExtendEvent event) {
        Block block = event.getBlock();

        // Targets end-of-the-line empty (AIR) block which is being pushed into, including if piston itself would extend into air.
        Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
        if (targetBlock.isEmpty() || targetBlock.isLiquid()) { // If potentially pushing into AIR/WATER/LAVA in another territory, check it out.
            Faction targetFaction = plugin.getFactionManager().getFactionAt(targetBlock.getLocation());
            if (targetFaction instanceof Raidable && !((Raidable) targetFaction).isRaidable() && targetFaction != plugin.getFactionManager().getFactionAt(block))
				event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonRetract(BlockPistonRetractEvent event) {
        if (!event.isSticky()) return; // If not a sticky piston, retraction should be fine.

        // If potentially retracted block is just AIR/WATER/LAVA, no worries
        Location retractLocation = event.getRetractLocation();
        Block retractBlock = retractLocation.getBlock();
        if (!retractBlock.isEmpty() && !retractBlock.isLiquid()) {
            Block block = event.getBlock();
            Faction targetFaction = plugin.getFactionManager().getFactionAt(retractLocation);
            if (targetFaction instanceof Raidable && !((Raidable) targetFaction).isRaidable() && targetFaction != plugin.getFactionManager().getFactionAt(block))
				event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block fromBlock = event.getBlock();
        Material fromType = fromBlock.getType();
        /**
         * This is used for blocks only.
         * In 1.13- WATER will turn into STATIONARY_WATER after it finished spreading.
         * After 1.13+ this uses
         * <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/Levelled.html">Levelled</a> water flowing system.
         */
        //  if (fromType == CompMaterial.WATER.getMaterial() || fromType == CompMaterial.STATIONARY_WATER || fromType == Material.LAVA || fromType == Material.STATIONARY_LAVA)
        if (fromType == CompMaterial.WATER.getMaterial() || fromType == CompMaterial.LAVA.getMaterial())
			if (!canBuildAt(fromBlock.getLocation(), event.getToBlock().getLocation())) event.setCancelled(true);
    }

   /* This need to be in other plugin!
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Faction toFactionAt = plugin.getFactionManager().getFactionAt(event.getTo());
            if (toFactionAt.isSafezone() && !plugin.getFactionManager().getFactionAt(event.getFrom()).isSafezone()) {
                Player player = event.getPlayer();
                player.sendMessage(ChatColor.RED + "You cannot Enderpearl into safe-zones, used Enderpearl has been refunded.");
                HCFactions.getInstance().getTimerManager().getEnderPearlTimer().refund(player);
                event.setCancelled(true);
            }
        }
    }*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            Location from = event.getFrom();
            Location to = event.getTo();
            Player player = event.getPlayer();

            Faction fromFac = plugin.getFactionManager().getFactionAt(from);
            if (fromFac.isSafezone()) { // teleport player to spawn point of target if came from safe-zone.
                event.setTo(to.getWorld().getSpawnLocation().add(0.5, 0, 0.5));
                event.useTravelAgent(false);
                player.sendMessage(Lang.of("factions.protection.teleport_spawn_safezone"));
                return;
            }

            if (event.useTravelAgent() && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
                TravelAgent travelAgent = event.getPortalTravelAgent();
                if (!travelAgent.getCanCreatePortal()) return;

                Location foundPortal = travelAgent.findPortal(to);
                if (foundPortal != null) return; // there is already an exit portal, so ignore

                Faction factionAt = plugin.getFactionManager().getFactionAt(to);
                if (factionAt instanceof ClaimableFaction) {
                    boolean shouldCancel = false;

                    try{
                        Faction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
                        if (playerFaction != factionAt) shouldCancel = true;
                    } catch (NoFactionFoundException e){
                        shouldCancel = true;
                    }
                    
                    if (shouldCancel){
                        player.sendMessage(Lang.of("factions.protection.portal_creation_cancelled")
                                .replace("{faction}", factionAt.getFormattedName(player)));
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    // Prevent mobs from spawning in the Warzone, safe-zones or claims.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
		// allow slimes to always split
		if (reason == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) return;

        Location location = event.getLocation();
        Faction factionAt = plugin.getFactionManager().getFactionAt(location);
		// allow creatures to spawn in safe-zones by Spawner
		if (factionAt.isSafezone() && reason == CreatureSpawnEvent.SpawnReason.SPAWNER) return;

        if (factionAt instanceof ClaimableFaction && ((!(factionAt instanceof Raidable) || !((Raidable) factionAt).isRaidable())) && event.getEntity() instanceof Monster)
			switch (reason) {
				case SPAWNER:
				case EGG:
				case CUSTOM:
				case BUILD_WITHER:
				case BUILD_IRONGOLEM:
				case BUILD_SNOWMAN:
					return;
				default:
					event.setCancelled(true);
			}
    }

    // Prevents players attacking or taking damage when in safe-zone protected areas.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Faction playerFactionAt = plugin.getFactionManager().getFactionAt(player.getLocation());
            EntityDamageEvent.DamageCause cause = event.getCause();
            if (playerFactionAt.isSafezone() && cause != EntityDamageEvent.DamageCause.SUICIDE && cause != EntityDamageEvent.DamageCause.VOID)
				event.setCancelled(true);

            Player attacker = BukkitUtils.getFinalAttacker(event, true);
            if (attacker != null) {
                Faction attackerFactionAt = plugin.getFactionManager().getFactionAt(attacker.getLocation());
                if (attackerFactionAt.isSafezone()) {
                    event.setCancelled(true);
                    attacker.sendMessage(Lang.of("factions.protection.cannot_attack_in_safezone"));
                    return;
                } else if (playerFactionAt.isSafezone()) {
                    // it's already cancelled above.
                    attacker.sendMessage(Lang.of("factions.protection.cannot_attack_players_in_safezone"));
                    return;
                }

                try{
                    PlayerFaction attackerFaction = plugin.getFactionManager().getPlayerFaction(attacker);
                    PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);

                    Role role = playerFaction.getMember(player).getRole();
                    String hiddenAstrixedName = role.getAstrix() + (player.hasPotionEffect(PotionEffectType.INVISIBILITY) ? "???" : player.getName());
                    if (attackerFaction == playerFaction) {
                        attacker.sendMessage(Lang.of("factions.protection.attack_faction_member")
                                .replace("{teammateColour}", Configuration.relationColourTeammate.toString())
                                .replace("{player}", hiddenAstrixedName));
                        if(!playerFaction.isFriendly_fire()) event.setCancelled(true);
                    } else if (attackerFaction.getAllied().contains(playerFaction.getUniqueID())) {
                        ChatColor color = Configuration.relationColourAlly;
                        if (Configuration.preventAllyAttackDamage) {
                            event.setCancelled(true);
                            attacker.sendMessage(Lang.of("factions.protection.attack_ally_cancelled")
                                    .replace("{allyColour}", color.toString())
                                    .replace("{player}", hiddenAstrixedName));
                            attacker.sendMessage(color + hiddenAstrixedName + ChatColor.YELLOW + " is an ally.");
                        } else attacker.sendMessage(Lang.of("factions.protection.attack_ally")
								.replace("{allyColour}", color.toString())
								.replace("{player}", hiddenAstrixedName));
                    }
                }catch (NoFactionFoundException ignored){}
            }
        }
    }

    // Prevents losing hunger in safe-zones.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player && ((Player) entity).getFoodLevel() > event.getFoodLevel() && plugin.getFactionManager().getFactionAt(entity.getLocation()).isSafezone())
			event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        if (!BukkitUtils.isDebuff(potion)) return;

        // Prevents potion effecting players that are in safe-zones.
        Faction factionAt = plugin.getFactionManager().getFactionAt(potion.getLocation());
        if (factionAt.isSafezone()) {
            event.setCancelled(true);
            return;
        }

        ProjectileSource source = potion.getShooter();
        if (source instanceof Player) {
            Player player = (Player) source;
            //Allow faction members to splash damage their own, PlayerFaction playerFaction = plugin.getFactionManager().getPlayerFaction(player);
            for (LivingEntity affected : event.getAffectedEntities())
				if (affected instanceof Player && !player.equals(affected)) {
					Player target = (Player) affected;
					if (target.equals(source)) continue; // allow the source to be affected regardless
					if (plugin.getFactionManager().getFactionAt(target.getLocation()).isSafezone()/*Nope || playerFaction.getMembers().containsKey(other.getUniqueId())*/)
						event.setIntensity(affected, 0);
				}
        }
    }

    // Prevent monsters targeting players in safe-zones or their own claims.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityTarget(EntityTargetEvent event) {
        switch (event.getReason()) {
            case CLOSEST_PLAYER:
            case RANDOM_TARGET:
                Entity target = event.getTarget();
                if (event.getEntity() instanceof LivingEntity && target instanceof Player) {
                    // Check LivingEntity instance, things like experience orbs might lag spam ;/
                    Faction factionAt = plugin.getFactionManager().getFactionAt(target.getLocation());

                    try{
                        if (factionAt.isSafezone() || factionAt == plugin.getFactionManager().getPlayerFaction((Player) target))
							event.setCancelled(true);
                    } catch (NoFactionFoundException ignored) {}
                }
                break;
            default:
                break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;

        Block block = event.getClickedBlock();
        Action action = event.getAction();
        if (action == Action.PHYSICAL) { // Prevent players from trampling on crops or pressure plates, etc.
            if (!attemptBuild(event.getPlayer(), block.getLocation(), null)) event.setCancelled(true);
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            boolean canRightClick;
            MaterialData blockData;
            Material blockType = block.getType();

            // Firstly, check if this block is not on the explicit blacklist.
            canRightClick = !BLOCK_RIGHT_CLICK_DENY.contains(blockType);
            if (canRightClick) {
                Material itemType = event.hasItem() ? event.getItem().getType() : null;

				// If the player is right clicking an Ender Portal Frame with an Ender Portal Eye and it is empty.
				if (CompMaterial.ENDER_EYE.getMaterial() == itemType && CompMaterial.END_PORTAL_FRAME.getMaterial() == blockType && block.getData() != 4)
					canRightClick = false;
				else // If the player is right clicking a Cauldron that contains liquid with a Glass Bottle.
					if (CompMaterial.GLASS_BOTTLE.getMaterial() == itemType && (blockData = block.getState().getData()) instanceof Cauldron && !((Cauldron) blockData).isEmpty())
						canRightClick = false;
					else // Finally, check if this block is not blacklisted with the item the player right clicked it with.
					if (itemType != null && ITEM_ON_BLOCK_RIGHT_CLICK_DENY.get(itemType).contains(block.getType()))
						canRightClick = false;
            } else // Allow workbench use in safezones.
				if (block.getType() == CompMaterial.CRAFTING_TABLE.getMaterial() && plugin.getFactionManager().getFactionAt(block.getLocation()).isSafezone())
					canRightClick = true;

            if (!canRightClick && !attemptBuild(event.getPlayer(), block.getLocation(), Lang.of("factions.protection.cannot_interact"), true))
				event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBurn(BlockBurnEvent event) {
        Faction factionAt = plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof WarzoneFaction || (factionAt instanceof Raidable && !((Raidable) factionAt).isRaidable()))
			event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFade(BlockFadeEvent event) {
        Faction factionAt = plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) event.setCancelled(true);
    }

    /*@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockSpread(BlockSpreadEvent event) {
        Faction factionAt = plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockForm(BlockFormEvent event) {
        Faction factionAt = plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && !attemptBuild(entity, event.getBlock().getLocation(), null))
			event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!attemptBuild(event.getPlayer(), event.getBlock().getLocation(), Lang.of("factions.protection.cannot_build")))
			event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!attemptBuild(event.getPlayer(), event.getBlockPlaced().getLocation(), Lang.of("factions.protection.cannot_build"), false, true))
			event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (!attemptBuild(event.getPlayer(), event.getBlockClicked().getLocation(), Lang.of("factions.protection.cannot_build")))
			event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!attemptBuild(event.getPlayer(), event.getBlockClicked().getLocation(), Lang.of("factions.protection.cannot_build")))
			event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        if (remover instanceof Player)
			if (!attemptBuild(remover, event.getEntity().getLocation(), Lang.of("factions.protection.cannot_build")))
				event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (!attemptBuild(event.getPlayer(), event.getEntity().getLocation(), Lang.of("factions.protection.cannot_build")))
			event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entered = event.getEntered();
        if (entered instanceof Player) {
            Vehicle vehicle = event.getVehicle();

			//Allow entering minecarts in other people's claims
			if(vehicle instanceof Minecart) return;

            if (!attemptBuild(event.getEntered(), vehicle.getLocation(), Lang.of("factions.protection.cannot_enter_vehicle"))) {
                event.setCancelled(true);
                return;
            }

            // Prevent players using horses that don't belong to them.
            if (vehicle instanceof Horse) {
                Horse horse = (Horse) event.getVehicle();
                AnimalTamer owner = horse.getOwner();
                if (owner != null && !owner.equals(entered)) {
                    ((Player) entered).sendMessage(Lang.of("factions.protection.steal_horse").replace("{player}", owner.getName()));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVehicleDamage(VehicleDamageEvent event) {
        Player damager = null;
        Entity attacker = event.getAttacker();
        if (attacker instanceof Player) damager = (Player) attacker;
		else if (attacker instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) attacker).getShooter();
            if (shooter instanceof Player) damager = (Player) shooter;
        }

        if (damager != null && !attemptBuild(attacker, damager.getLocation(), Lang.of("factions.protection.cannot_build")))
			event.setCancelled(true);
    }

    // Prevents items that are in Item Frames OR hanging entities (PAINTINGS, etc) being removed.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Hanging) {
            Player attacker = BukkitUtils.getFinalAttacker(event, false);
            if (attacker != null && !attemptBuild(attacker, entity.getLocation(), Lang.of("factions.protection.cannot_build")))
				event.setCancelled(true);
        }
    }

    // Prevents items that are in Item Frames being rotated.
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onHangingInteractByPlayer(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity instanceof Hanging)
			if (!attemptBuild(event.getPlayer(), entity.getLocation(), Lang.of("factions.protection.cannot_build")))
				event.setCancelled(true);
    }

    /**
     * Checks if a entity is eligible to build at a given location, if not
     * it will send the deny message passed in the constructor.
     * <p>The deny message will be formatted using {@link String#format(String, Object...)}</p>
     * <p>The first formatted argument is the display name of the enemy faction to the player</p>
     *
     * @param entity      the entity to attempt for
     * @param location    the location to attempt at
     * @param denyMessage the deny message to send
     * @return true if the player can build at location
     */
    public static boolean attemptBuild(Entity entity, Location location, @Nullable String denyMessage) {
        return attemptBuild(entity, location, denyMessage, false);
    }

    /**
     * Checks if a entity is eligible to build at a given location, if not
     * it will send the deny message passed in the constructor.
     * <p>The deny message will be formatted using {@link String#format(String, Object...)}</p>
     * <p>The first formatted argument is the display name of the enemy faction to the player</p>
     *
     * @param entity        the entity to attempt for
     * @param location      the location to attempt at
     * @param denyMessage   the deny message to send
     * @param isInteraction if the entity is trying to interact
     * @return true if the player can build at location
     */
    public static boolean attemptBuild(Entity entity, Location location, @Nullable String denyMessage, boolean isInteraction) {
        return attemptBuild(entity, location, denyMessage, isInteraction, false);
    }

    public static boolean attemptBuild(Entity entity, Location location, @Nullable String denyMessage, boolean isInteraction, boolean place) {
        Player player = entity instanceof Player ? (Player) entity : null;
        HCFactions plugin = JavaPlugin.getPlugin(HCFactions.class);

        // Allow CREATIVE players with specified permission to bypass this protection.
        if (player != null && player.getGameMode() == GameMode.CREATIVE && player.hasPermission(PROTECTION_BYPASS_PERMISSION))
			return true;

        if (player != null && player.getWorld().getEnvironment() == World.Environment.THE_END) {
            if(denyMessage != null) player.sendMessage(Lang.of("factions.protection.cannot_build_end"));
            return false;
        }

        boolean result = false;
        Faction factionAt = plugin.getFactionManager().getFactionAt(location);
        if(factionAt.getName().equalsIgnoreCase("glowstone") && location.getBlock().getType() == CompMaterial.GLOWSTONE.getMaterial())
			return true;

        if (!(factionAt instanceof ClaimableFaction)) result = true;
		else if (factionAt instanceof Raidable && ((Raidable) factionAt).isRaidable()) result = true;

        if (player != null && factionAt instanceof PlayerFaction) try {
			if (plugin.getFactionManager().getPlayerFaction(player) == factionAt) result = true;
		} catch (NoFactionFoundException ignored) {
		}

        if (result) {
            // Show this message last as the other messages look cleaner.
            if (!isInteraction && factionAt instanceof WarzoneFaction) {
                if (denyMessage != null && player != null) {
                    if(Math.abs(location.getX()) > 300 | Math.abs(location.getZ()) > 300) return true;

                    player.sendMessage(Lang.of("factions.protection.cannot_build_system_faction")
                            .replace("{faction}", factionAt.getFormattedName(player)));
                }

                return false;
            }
        } else if (denyMessage != null && player != null)
			player.sendMessage(String.format(denyMessage, factionAt.getFormattedName(player)));

        return result;
    }

    /**
     * Checks if a {@link Location} is eligible to build into another {@link Location}.
     *
     * @param from the from {@link Location} to test
     * @param to   the to {@link Location} to test
     * @return true if the to {@link Faction} is the same or is not claimable
     */
    public static boolean canBuildAt(Location from, Location to) {
        HCFactions plugin = JavaPlugin.getPlugin(HCFactions.class);
        Faction toFactionAt = plugin.getFactionManager().getFactionAt(to);
        return !(toFactionAt instanceof Raidable && !((Raidable) toFactionAt).isRaidable() && toFactionAt != plugin.getFactionManager().getFactionAt(from));
    }
}
