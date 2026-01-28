package org.hcgames.hcfactions.manager;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.event.claim.ClaimChangeEvent;
import org.hcgames.hcfactions.event.claim.FactionClaimChangedEvent;
import org.hcgames.hcfactions.event.faction.FactionCreateEvent;
import org.hcgames.hcfactions.event.faction.FactionRemoveEvent;
import org.hcgames.hcfactions.event.faction.FactionRenameEvent;
import org.hcgames.hcfactions.event.playerfaction.PlayerJoinedFactionEvent;
import org.hcgames.hcfactions.event.playerfaction.PlayerLeftFactionEvent;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.faction.system.SystemFaction;
import org.hcgames.hcfactions.faction.system.WarzoneFaction;
import org.hcgames.hcfactions.faction.system.WildernessFaction;
import org.hcgames.hcfactions.focus.FocusHandler;
import org.hcgames.hcfactions.lib.LongHash;
import org.hcgames.hcfactions.structure.ChatChannel;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.structure.Relation;
import org.hcgames.hcfactions.structure.Role;
import org.hcgames.hcfactions.util.configuration.Config;
import org.hcgames.hcfactions.util.uuid.UUIDHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class FlatFileFactionManager implements FactionManager, Listener{

    // The default claimless factions.
    private final WarzoneFaction warzone;
    private final WildernessFaction wilderness;
    private final FocusHandler focusHandler;

    // Cached for faster lookup for factions. Potentially usage Guava Cache for
    // future implementations (database).
    private final Table<String, Long, Claim> claimPositionMap = HashBasedTable.create();
    private final Map<UUID, UUID> factionPlayerUuidMap = new ConcurrentHashMap<>();
    final ConcurrentMap<UUID, Faction> factionUUIDMap = new ConcurrentHashMap<>();
    final Map<String, UUID> factionNameMap = new CaseInsensitiveMap<>();

    Config config;
    final HCFactions plugin;

    public FlatFileFactionManager(HCFactions plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.warzone = new WarzoneFaction();
        this.wilderness = new WildernessFaction();
        focusHandler = new FocusHandler(plugin);

        init();
        this.reloadFactionData();
    }

    void init(){}
    
    public Set<Claim> getAllClaims() {
        return claimPositionMap.values()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(PlayerJoinedFactionEvent event) {
        this.factionPlayerUuidMap.put(event.getPlayerUUID(), event.getFaction().getUniqueID());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
        this.factionPlayerUuidMap.remove(event.getUniqueID());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRename(FactionRenameEvent event) {
        if(!event.isDisplayName()){
            this.factionNameMap.remove(event.getOldName());
            this.factionNameMap.put(event.getNewName(), event.getFaction().getUniqueID());
        }
    }

    // Cache the claimed land locations
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionClaim(FactionClaimChangedEvent event) {
        for (Claim claim : event.getClaims()) {
            this.cacheClaim(claim, event.getReason());
        }
    }

    @Override
    @Deprecated
    public Map<String, UUID> getFactionNameMap() {
        return this.factionNameMap;
    }

    @Override
    public ImmutableList<Faction> getFactions() {
        return ImmutableList.copyOf(this.factionUUIDMap.values());
    }

    @Override
    public Claim getClaimAt(World world, int x, int z) {
        return this.claimPositionMap.get(world.getName(), LongHash.toLong(x, z));
    }

    @Override
    public Faction getFactionAt(World world, int x, int z) {
        World.Environment environment = world.getEnvironment();

        Claim claim = this.getClaimAt(world, x, z);
        if (claim != null) {
            try {
                return claim.getFaction();
            }catch (NoFactionFoundException ignored){}
        }

        if (environment == World.Environment.THE_END) { // the End doesn't have a Wilderness.
            return this.warzone;
        }

        int warzoneRadius = environment == World.Environment.NETHER ?
                Configuration.warzoneRadiusNether :
                Configuration.warzoneRadiusOverworld;

        return Math.abs(x) > warzoneRadius || Math.abs(z) > warzoneRadius ? this.wilderness : this.warzone;
    }

    @Override
    public <T extends Faction> T getFaction(UUID factionUUID, Class<T> clazz) throws NoFactionFoundException, ClassCastException {
        return clazz.cast(getFaction(factionUUID));
    }

    @Override
    public Faction getFaction(String factionName) throws NoFactionFoundException{
        if(!factionNameMap.containsKey(factionName)){
            throw new NoFactionFoundException(factionName);
        }

        UUID uuid = factionNameMap.get(factionName);
        if(!factionUUIDMap.containsKey(uuid)){
            throw new NoFactionFoundException(factionName);
        }

        return factionUUIDMap.get(uuid);
    }

    @Override
    public Faction getFaction(UUID factionUUID) throws NoFactionFoundException{
        if(!factionUUIDMap.containsKey(factionUUID)){
            throw new NoFactionFoundException(factionUUID.toString());
        }

        return factionUUIDMap.get(factionUUID);
    }

    @Override
    public <T extends Faction> T getFaction(String factionName, Class<T> clazz) throws NoFactionFoundException, ClassCastException {
        return clazz.cast(getFaction(factionName));
    }

    @Override
    public PlayerFaction getPlayerFaction(UUID playerUUID) throws NoFactionFoundException{
        if(!factionPlayerUuidMap.containsKey(playerUUID)){
            throw new NoFactionFoundException(playerUUID.toString());
        }

        UUID uuid = factionPlayerUuidMap.get(playerUUID);

        if(!factionUUIDMap.containsKey(uuid)){
            throw new NoFactionFoundException(playerUUID.toString() + "(Found in player uuid map but not faction uuid map)");
        }

        Faction faction = factionUUIDMap.get(uuid);
        if(!(faction instanceof PlayerFaction)){
           throw new NoFactionFoundException("Of wrong type");
        }

        return (PlayerFaction) faction;
    }
    @Override
    public PlayerFaction getPlayerFactionIfPresent(UUID playerUUID) {
        UUID factionUUID = factionPlayerUuidMap.get(playerUUID);
        if (factionUUID == null)
            return null;

        Faction faction = factionUUIDMap.get(factionUUID);
        if (faction instanceof PlayerFaction)
            return (PlayerFaction) faction;

        return null;
    }

    
    @Override
    public boolean containsFaction(Faction faction) {
        return factionNameMap.containsKey(faction.getName());
    }

    @Override
    public FocusHandler getFocusHandler() {
        return focusHandler;
    }

    @Override
    public boolean createFaction(Faction faction) {
        return createFaction(faction, Bukkit.getConsoleSender());
    }

    @Override
    public boolean createFaction(Faction faction, CommandSender sender) {
        // Automatically attempt to make the sender as the leader.
        if (faction instanceof PlayerFaction && sender instanceof Player) {
            Player player = (Player) sender;
            PlayerFaction playerFaction = (PlayerFaction) faction;
            if (!playerFaction.addMember(sender, player, player.getUniqueId(), new FactionMember(player, ChatChannel.PUBLIC, Role.LEADER), false)) {
                return false;
            }
        }

        if (this.factionUUIDMap.putIfAbsent(faction.getUniqueID(), faction) != null) {
            return false;  // faction already exists.
        }

        this.factionNameMap.put(faction.getName(), faction.getUniqueID());

        FactionCreateEvent createEvent = new FactionCreateEvent(faction, sender);
        Bukkit.getPluginManager().callEvent(createEvent);
        return !createEvent.isCancelled();
    }

    @Override
    public boolean removeFaction(Faction faction, CommandSender sender) {
        if (!this.factionUUIDMap.containsKey(faction.getUniqueID())) {
            return false;
        }

        FactionRemoveEvent removeEvent = new FactionRemoveEvent(faction, sender);
        Bukkit.getPluginManager().callEvent(removeEvent);
        if (removeEvent.isCancelled()) {
            return false;
        }

        this.factionUUIDMap.remove(faction.getUniqueID());
        this.factionNameMap.remove(faction.getName());

        // Let the plugin know the claims should be lost.
        if (faction instanceof ClaimableFaction) {
            ClaimableFaction claimableFaction = (ClaimableFaction) faction;

            if(!claimableFaction.getClaims().isEmpty()){
                plugin.getServer().getPluginManager().callEvent(new FactionClaimChangedEvent(sender, claimableFaction, claimableFaction.getClaims(), ClaimChangeEvent.ClaimChangeReason.UNCLAIM));
            }
        }

        // Let the plugin know these players should have left.
        if (faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) faction;
            for (PlayerFaction ally : playerFaction.getAlliedFactions()) {
                ally.getRelations().remove(faction.getUniqueID());
            }

            for (UUID uuid : playerFaction.getMembers().keySet()) {
                playerFaction.removeMember(sender, null, uuid, true, true);
            }

            for (UUID uuid : playerFaction.getMembers().keySet()) {
                playerFaction.removeMember(sender, null, uuid, true, true);
            }
        }

        return true;
    }

    @Override
    public <T extends Faction> void advancedSearch(String query, Class<T> classType, SearchCallback<T> callback, boolean forcePlayer) {
        if (!forcePlayer && factionNameMap.containsKey(query)) {
            UUID factionUUID = factionNameMap.get(query);
            Faction faction = factionUUIDMap.get(factionUUID);

            if (faction == null) {
                callback.onFail(SearchCallback.FailReason.NOT_FOUND);
                return;
            }

            try {
                callback.onSuccess(classType.cast(faction));
            } catch (ClassCastException e) {
                callback.onFail(SearchCallback.FailReason.CLASS_CAST);
            }
            return;
        }


        if (!classType.isAssignableFrom(PlayerFaction.class)) {
            callback.onFail(SearchCallback.FailReason.NOT_FOUND);
            return;
        }

        Player player = Bukkit.getPlayer(query);
        if (player != null) {
            handleFactionFromUUID(player.getUniqueId(), classType, callback);
            return;
        }

        Runnable lookupTask = () -> {
            try {
                UUID uuid = UUIDHandler.getUUID(query);
                if (uuid == null) {
                    callback.onFail(SearchCallback.FailReason.NOT_FOUND);
                    return;
                }

                handleFactionFromUUID(uuid, classType, callback);
            } catch (Exception ex) {
                callback.onFail(SearchCallback.FailReason.DATA_ERROR);
            }
        };


        boolean async = callback.forceAsync() || (!plugin.getServer().isPrimaryThread() && callback.isAsync());

        if (async) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, lookupTask);
        } else {
            lookupTask.run();
        }
    }

    /**
     * Intenta encontrar una facción basada en un UUID de jugador y manejar la respuesta.
     */
    private <T extends Faction> void handleFactionFromUUID(UUID playerUUID, Class<T> classType, SearchCallback<T> callback) {
        UUID factionUUID = factionPlayerUuidMap.get(playerUUID);
        if (factionUUID == null) {
            callback.onFail(SearchCallback.FailReason.NOT_FOUND);
            return;
        }

        Faction faction = factionUUIDMap.get(factionUUID);
        if (faction == null) {
            callback.onFail(SearchCallback.FailReason.NOT_FOUND);
            return;
        }

        try {
            callback.onSuccess(classType.cast(faction));
        } catch (ClassCastException e) {
            callback.onFail(SearchCallback.FailReason.CLASS_CAST);
        }
    }


    private void cacheClaim(Claim claim, ClaimChangeEvent.ClaimChangeReason cause) {
        Objects.requireNonNull(claim, "Claim cannot be null");
        Objects.requireNonNull(cause, "Cause cannot be null");
        Objects.requireNonNull(cause !=  ClaimChangeEvent.ClaimChangeReason.RESIZE, "Cannot cache claims of resize type");

        World world = claim.getWorld();
        // FILTRO SEGURIDAD
        if (claim.getWorldName() == null) {
            plugin.getLogger().warning("Skipping invalid claim " + claim.getClaimUniqueID());
            return;
        }
        if (world == null) {
            return; // safe-guard if Nether or End is disabled for example
        }

        int minX = Math.min(claim.getX1(), claim.getX2());
        int maxX = Math.max(claim.getX1(), claim.getX2());
        int minZ = Math.min(claim.getZ1(), claim.getZ2());
        int maxZ = Math.max(claim.getZ1(), claim.getZ2());
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (cause ==  ClaimChangeEvent.ClaimChangeReason.CLAIM) {
                    this.claimPositionMap.put(world.getName(), LongHash.toLong(x, z), claim);
                } else if (cause ==  ClaimChangeEvent.ClaimChangeReason.UNCLAIM) {
                    this.claimPositionMap.remove(world.getName(), LongHash.toLong(x, z));
                }
            }
        }
    }

    void cacheFaction(Faction faction) {
        factionNameMap.put(faction.getName(), faction.getUniqueID());
        factionUUIDMap.put(faction.getUniqueID(), faction);

        // Put the claims in the cache.
        if (faction instanceof ClaimableFaction) {
            for (Claim claim : ((ClaimableFaction) faction).getClaims()) {
                this.cacheClaim(claim,  ClaimChangeEvent.ClaimChangeReason.CLAIM);
            }
        }

        // Put the members in the cache too.
        if (faction instanceof PlayerFaction) {
            PlayerFaction playerFaction = (PlayerFaction) faction;

            for (FactionMember factionMember : playerFaction.getMembers().values()) {
                this.factionPlayerUuidMap.put(factionMember.getUniqueId(), faction.getUniqueID());
            }

            if(Configuration.factionMaxAllies == 0){
                List<UUID> toRemove = Maps.filterValues(playerFaction.getRequestedRelations(), Relation::isAlly).keySet().stream().collect(Collectors.toList());
                toRemove.forEach(playerFaction::removeRequestedRelation);

                toRemove = playerFaction.getAllied();
                toRemove.forEach(playerFaction::removeRelation);
            }
        }
    }

    @Override
    public void reloadFactionData() {
        this.factionNameMap.clear();
        this.config = new Config(plugin, "factions");
        int factions = 0;

        Object object = config.get("factions");
        if (object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for (String factionName : section.getKeys(false)) {
                Object next = config.get(section.getCurrentPath() + '.' + factionName);
                if (next instanceof Faction) {
                    cacheFaction((Faction) next);
                    factions++;
                }
            }
        } else if (object instanceof List<?>) {
            List<?> list = (List<?>) object;
            for (Object next : list) {
                if (next instanceof Faction) {
                    cacheFaction((Faction) next);
                    factions++;
                }
            }
        }

        for(Class<? extends SystemFaction> systemFaction : FactionManager.systemFactions.getSystemFactions()){
            try{
                Method method = systemFaction.getDeclaredMethod("getUUID");
                UUID result = (UUID) method.invoke(null);

                if(!factionUUIDMap.containsKey(result)){
                    Constructor<?> constructor = systemFaction.getConstructor();

                    Faction faction = (Faction) constructor.newInstance();
                    cacheFaction(faction);

                    factions++;
                    plugin.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Faction " + faction.getName() + " not found, created.");
                }
            }catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e){
                e.printStackTrace();
            }
        }

        plugin.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Loaded " + factions + " factions.");
    }

    @Override
    public void saveFactionData() {
        this.config.set("factions", new ArrayList<>(factionUUIDMap.values()));
        this.config.save();
    }

}
