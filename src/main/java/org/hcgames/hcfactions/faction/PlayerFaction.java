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

package org.hcgames.hcfactions.faction;


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.hcgames.hcfactions.Configuration;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.event.playerfaction.*;
import org.hcgames.hcfactions.exception.NoFactionFoundException;
import org.hcgames.hcfactions.focus.FocusTarget;
import org.hcgames.hcfactions.structure.*;
import org.hcgames.hcfactions.user.FactionUser;
import org.hcgames.hcfactions.util.DurationFormatter;
import org.hcgames.hcfactions.util.GenericUtils;
import org.hcgames.hcfactions.util.JavaUtils;
import org.hcgames.hcfactions.util.PersistableLocation;
import org.hcgames.hcfactions.util.collect.ConcurrentSet;
import org.hcgames.hcfactions.util.text.CC;
import org.mineacademy.fo.settings.Lang;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//TODO: Optionals
public class PlayerFaction extends ClaimableFaction implements Raidable {

    private static final Joiner INFO_JOINER = Joiner.on(ChatColor.GRAY + ", ");

    // The UUID is the Faction unique ID.
    @Getter
	private final Map<UUID, Relation> requestedRelations = new ConcurrentHashMap<>();
    @Getter
	private final Map<UUID, FactionRelation> relations = new ConcurrentHashMap<>();

    private final Map<UUID, FactionMember> members = new ConcurrentHashMap<>();
    private final Set<String> invitedPlayers = new ConcurrentSet<>();

    private final Map<UUID, FocusTarget> focusTargets = new ConcurrentHashMap<>();
    private final Map<UUID, Long> previousMembers = new ConcurrentHashMap<>();

    private PersistableLocation home;
    private String announcement;
    @Setter
	@Getter
	private boolean open;
    @Setter
	@Getter
	private int balance;
    @Setter
	@Getter
	private int lives = 0;
    private double deathsUntilRaidable = 1.0D;
    private long regenCooldownTimestamp;
    @Getter @Setter private boolean friendly_fire = false;
    @Getter private long lastDtrUpdateTimestamp;

    public static String parsePapi(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
    public PlayerFaction(String name) {
        super(name);
    }

    public PlayerFaction(Map<String, Object> map) {
        super(map);

        GenericUtils.castMap(map.get("members"), String.class, FactionMember.class).entrySet().stream().filter(entry
                -> entry.getValue() != null).forEach(entry -> members.put(UUID.fromString(entry.getKey()), entry.getValue()));
        //invitedPlayers.addAll(GenericUtils.createList(map.get("invitedPlayers"), String.class).stream().map(UUID::fromString).collect(Collectors.toList()));
        invitedPlayers.addAll(GenericUtils.createList(map.get("invitedPlayerNames"), String.class));

        if(map.containsKey("home")) home = (PersistableLocation) map.get("home");

        if(map.containsKey("announcement")) announcement = (String) map.get("announcement");

        GenericUtils.castMap(map.get("relations"), String.class, FactionRelation.class).entrySet().forEach(entry ->
                relations.put(UUID.fromString(entry.getKey()), entry.getValue()));

        for (Map.Entry<String, String> entry : GenericUtils.castMap(map.get("requestedRelations"), String.class, String.class).entrySet())
			requestedRelations.put(UUID.fromString(entry.getKey()), Relation.valueOf(entry.getValue()));

        for (Map.Entry<String, String> entry : GenericUtils.castMap(map.get("previousMembers"), String.class, String.class).entrySet())
			previousMembers.put(UUID.fromString(entry.getKey()), Long.valueOf(entry.getValue()));

        open = (Boolean) map.get("open");
        balance = (Integer) map.get("balance");
        deathsUntilRaidable = (Double) map.get("deathsUntilRaidable");
        regenCooldownTimestamp = Long.parseLong((String) map.get("regenCooldownTimestamp"));
        lastDtrUpdateTimestamp = Long.parseLong((String) map.get("lastDtrUpdateTimestamp"));
        lives = (int) map.get("lives");
    }

    public PlayerFaction(Document document){
        super(document);

        Document relations = (Document) document.get("relations");
        for(Map.Entry<String, Object> entry : relations.entrySet())
			this.relations.put(UUID.fromString(entry.getKey()), new FactionRelation((Document) entry.getValue()));

        Document requestedRelations = (Document) document.get("requestedRelations");
        for(Map.Entry<String, Object> entry : requestedRelations.entrySet())
			this.requestedRelations.put(UUID.fromString(entry.getKey()), Relation.valueOf((String) entry.getValue()));

        Document members = (Document) document.get("members");
        for(Map.Entry<String, Object> entry : members.entrySet())
			this.members.put(UUID.fromString(entry.getKey()), new FactionMember((Document) entry.getValue()));

        invitedPlayers.addAll(GenericUtils.createList(document.get("invitedPlayerNames"), Document.class)
                .stream().map(invites -> invites.getString("entry")).collect(Collectors.toList()));

        if(document.containsKey("home")) home = new PersistableLocation(document.get("home", Document.class));

        for(Map.Entry<String, Object> entry : document.get("previousMembers", Document.class).entrySet())
			previousMembers.put(UUID.fromString(entry.getKey()), Long.valueOf(String.valueOf(entry.getValue())));
        
        announcement = (String) document.getOrDefault("announcement", null);
        open = document.getBoolean("open");
        balance = document.getInteger("balance");
        deathsUntilRaidable = document.getDouble("deathsUntilRaidable");
        regenCooldownTimestamp = document.getLong("regenCooldownTimestamp");
        lastDtrUpdateTimestamp = document.getLong("lastDtrUpdateTimestamp");
        lives = document.getInteger("lives");
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();

        Map<String, FactionRelation> relationsSaveMap = new LinkedHashMap<>(relations.size());
        for (Map.Entry<UUID, FactionRelation> entry : relations.entrySet())
			relationsSaveMap.put(entry.getKey().toString(), entry.getValue());
        map.put("relations", relationsSaveMap);

        Map<String, String> requestedRelationsSaveMap = new HashMap<>(requestedRelations.size());
        for (Map.Entry<UUID, Relation> entry : requestedRelations.entrySet())
			requestedRelationsSaveMap.put(entry.getKey().toString(), entry.getValue().name());
        map.put("requestedRelations", requestedRelationsSaveMap);

        Map<String, FactionMember> saveMap = new LinkedHashMap<>(members.size());
        for (Map.Entry<UUID, FactionMember> entry : members.entrySet())
			saveMap.put(entry.getKey().toString(), entry.getValue());
        map.put("members", saveMap);

        Map<String, String> previousMembersSaveMap = new LinkedHashMap<>(previousMembers.size());
        for(Map.Entry<UUID, Long> entry : previousMembers.entrySet())
			previousMembersSaveMap.put(entry.getKey().toString(), Long.toString(entry.getValue()));
        map.put("previousMembers", previousMembersSaveMap);

        if (home != null) map.put("home", home);
        if (announcement != null) map.put("announcement", announcement);

        if(home != null) map.put("home", home);

        if(announcement != null) map.put("announcement", announcement);

        map.put("invitedPlayerNames", new ArrayList<>(invitedPlayers));
        map.put("open", open);
        map.put("balance", balance);
        map.put("deathsUntilRaidable", deathsUntilRaidable);
        map.put("regenCooldownTimestamp", Long.toString(regenCooldownTimestamp));
        map.put("lastDtrUpdateTimestamp", Long.toString(lastDtrUpdateTimestamp));
        map.put("lives", lives);
        return map;
    }

    @Override
    public Document toDocument(){
        Document document = super.toDocument();

        Document relations = new Document();
        for (Map.Entry<UUID, FactionRelation> entry : this.relations.entrySet())
			relations.put(entry.getKey().toString(), entry.getValue().toDocument());
        document.put("relations", relations);

        Document requestedRelations = new Document();
        for (Map.Entry<UUID, Relation> entry : this.requestedRelations.entrySet())
			requestedRelations.put(entry.getKey().toString(), entry.getValue().name());
        document.put("requestedRelations", requestedRelations);

        Document members = new Document();
        for (Map.Entry<UUID, FactionMember> entry : this.members.entrySet())
			members.put(entry.getKey().toString(), entry.getValue().toDocument());
        document.put("members", members);


        Document previousMembers = new Document();
        for(Map.Entry<UUID, Long> entry : this.previousMembers.entrySet())
			previousMembers.put(entry.getKey().toString(), Long.toString(entry.getValue()));
        document.put("previousMembers", previousMembers);

        if(home != null) document.put("home", home.toDocument());

        if(announcement != null) document.put("announcement", announcement);

        document.put("invitedPlayerNames", invitedPlayers.stream().map(invited -> new Document("entry", invited)).collect(Collectors.toList()));
        document.put("open", open);
        document.put("balance", balance);
        document.put("deathsUntilRaidable", deathsUntilRaidable);
        document.put("regenCooldownTimestamp", regenCooldownTimestamp);
        document.put("lastDtrUpdateTimestamp", lastDtrUpdateTimestamp);
        document.put("lives", lives);

        return document;
    }

    public boolean addMember(CommandSender sender, @Nullable Player player, UUID playerUUID, FactionMember factionMember, boolean force) {
        if (members.containsKey(playerUUID)) return false;

        HCFactions factions = JavaPlugin.getPlugin(HCFactions.class);
        if(previousMembers.containsKey(playerUUID) && !force && Configuration.antiRotationEnabled){
            Long lastRemovedWithDelay = previousMembers.get(playerUUID) + Configuration.antiRotationDelay;
            if(lastRemovedWithDelay > System.currentTimeMillis()){
                sender.sendMessage(Lang.of("factions.antirotation").replace("{time}",
                        DurationFormatter.getRemaining(lastRemovedWithDelay - System.currentTimeMillis(), true)));
                return false;
            }

            previousMembers.remove(playerUUID);
        }

        PlayerJoinFactionEvent eventPre = new PlayerJoinFactionEvent(sender, player, playerUUID, this);
        Bukkit.getServer().getPluginManager().callEvent(eventPre);
        if (eventPre.isCancelled()) return false;

        // Set the player as a member before calling the
        // event so we can change the scoreboard.
        lastDtrUpdateTimestamp = System.currentTimeMillis();
        invitedPlayers.remove(factionMember.getCachedName().toLowerCase());
        members.put(playerUUID, factionMember);
        Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinedFactionEvent(sender, player, playerUUID, this));

        return true;
    }

    public boolean removeMember(CommandSender sender, @Nullable Player player, UUID playerUUID, boolean kick, boolean force) {
        if (!members.containsKey(playerUUID)) return true;

        // Call pre event.
        PlayerLeaveFactionEvent preEvent = new PlayerLeaveFactionEvent(sender, player, playerUUID, this, PlayerLeaveFactionEvent.FactionLeaveCause.LEAVE, kick, false);
        Bukkit.getServer().getPluginManager().callEvent(preEvent);
        if (preEvent.isCancelled()) return false;

        members.remove(playerUUID);
        previousMembers.put(playerUUID, System.currentTimeMillis());
        setDeathsUntilRaidable(Math.min(deathsUntilRaidable, getMaximumDeathsUntilRaidable()));

        // Call after event.
        PlayerLeftFactionEvent event = new PlayerLeftFactionEvent(sender, player, playerUUID, this, PlayerLeaveFactionEvent.FactionLeaveCause.LEAVE, kick, false);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return true;
    }

    /**
     * Gets a list of faction UUIDs that are allied to this {@link PlayerFaction}.
     *
     * @return mutable list of UUIDs
     */
    public List<UUID> getAllied() {
        return relations.entrySet().stream().filter(relation -> relation.getValue().getRelation() == Relation.ALLY)
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }

    /**
     * Gets a list of {@link PlayerFaction}s that are allied to this {@link PlayerFaction}.
     * Please note this method loops through every faction and shouldn't be called often
     *
     * @return mutable list of {@link PlayerFaction}s
     */

    //TODO: Cache
    public List<PlayerFaction> getAlliedFactions() {
        Collection<UUID> allied = getAllied();
        Iterator<UUID> iterator = allied.iterator();
        List<PlayerFaction> results = new ArrayList<>(allied.size());
        HCFactions factions = JavaPlugin.getPlugin(HCFactions.class);

        while (iterator.hasNext()) {
            Faction faction;

            try {
                faction = factions.getFactionManager().getFaction(iterator.next());
            } catch (NoFactionFoundException e) {
                iterator.remove();
                continue;
            }

            if(faction instanceof PlayerFaction) results.add((PlayerFaction) faction);
			else iterator.remove();
        }

        return results;
    }

	public void removeRequestedRelation(UUID uuid){
        requestedRelations.remove(uuid);
    }

    public void removeRelation(UUID uuid){
        relations.remove(uuid);
    }

    /**
     * Gets the members in this {@link PlayerFaction}.
     * <p>The key is the {@link UUID} of the member</p>
     * <p>The value is the {@link FactionMember}</p>
     *
     * @return map of members.
     */
    public Map<UUID, FactionMember> getMembers() {
        return ImmutableMap.copyOf(members);
    }

    /**
     * Gets the online {@link Player}s in this {@link Faction}.
     *
     * @return set of online {@link Player}s
     */
    public List<Player> getOnlinePlayers() {
        return getOnlinePlayers(null);
    }

    /**
     * Gets the online {@link Player}s in this {@link Faction} that are
     * visible to a {@link CommandSender}.
     *
     * @param sender the {@link CommandSender} to get for
     * @return a set of online players visible to sender
     */
    public List<Player> getOnlinePlayers(CommandSender sender) {
        return getOnlineMembers(sender).entrySet().stream().map(entry -> Bukkit.getServer().getPlayer(entry.getKey())).collect(Collectors.toList());
    }
    public List<String> getOnlineNames(){
    	List<String> names = new ArrayList<>();
    	for(Player p : getOnlinePlayers(null)) names.add(p.getName());
    	return names;
    }
    
    public List<OfflinePlayer> getOfflinePlayers(CommandSender sender) {
        return getOfflineMembers(sender).keySet().stream()
                .map(Bukkit::getOfflinePlayer)
                .collect(Collectors.toList());
    }
    public List<String> getOfflinePlayers() {
    	
    	List<String> names = new ArrayList<>();
    	for(OfflinePlayer name : getOfflinePlayers(null)) names.add(name.getName());
        return names;
    }


    /**
     * Gets the online members in this {@link Faction}.
     * <p>The key is the {@link UUID} of the member</p>
     * <p>The value is the {@link FactionMember}</p>
     *
     * @return an immutable set of online members
     */
    public Map<UUID, FactionMember> getOnlineMembers() {
        return getOnlineMembers(null);
    }

    /**
     * Gets the online members in this {@link Faction} that are visible to a {@link CommandSender}.
     * <p>The key is the {@link UUID} of the member</p>
     * <p>The value is the {@link FactionMember}</p>
     *
     * @param sender the {@link CommandSender} to get for
     * @return a set of online members visible to sender
     */
    public Map<UUID, FactionMember> getOnlineMembers(CommandSender sender) {
        Player senderPlayer = sender instanceof Player ? ((Player) sender) : null;
        Map<UUID, FactionMember> results = new HashMap<>();

        for (Map.Entry<UUID, FactionMember> entry : members.entrySet()) {
            Player target = Bukkit.getServer().getPlayer(entry.getKey());
            if (target == null || (senderPlayer != null && !senderPlayer.canSee(target))) continue;

            results.put(entry.getKey(), entry.getValue());
        }

        return results;
    }
    public Map<UUID, FactionMember> getOfflineMembers(CommandSender sender) {
        Player senderPlayer = sender instanceof Player ? (Player) sender : null;
        Map<UUID, FactionMember> results = new HashMap<>();

        for (Map.Entry<UUID, FactionMember> entry : members.entrySet()) {
            Player target = Bukkit.getServer().getPlayer(entry.getKey());
            if (target != null && (senderPlayer == null || senderPlayer.canSee(target))) continue;

            results.put(entry.getKey(), entry.getValue());
        }

        return results;
    }


    /**
     * Gets the leading {@link FactionMember} of this {@link Faction}.
     *
     * @return the leading {@link FactionMember}
     */
    public Optional<FactionMember> getLeader() {//TODO: Cache
        Map<UUID, FactionMember> members = this.members;
        for (Map.Entry<UUID, FactionMember> entry : members.entrySet())
			if (entry.getValue().getRole() == Role.LEADER) return Optional.of(entry.getValue());

        return Optional.empty();
    }

    /**
     * Gets the {@link FactionMember} of a {@link Player}.
     *
     * @param player the {@link Player} to get for
     * @return the {@link FactionMember} or null if is not a member
     */
    public FactionMember getMember(Player player) {
        return getMember(player.getUniqueId());
    }

    /**
     * Gets the {@link FactionMember} with a specific {@link UUID}.
     *
     * @param memberUUID the {@link UUID} to get for
     * @return the {@link FactionMember} or null if is not a member
     */
    public FactionMember getMember(UUID memberUUID) {
        return members.get(memberUUID);
    }

    public FactionMember findMember(String name){
        for(FactionMember member : members.values()) if (member.getCachedName().equalsIgnoreCase(name)) return member;

        return null;
    }

    /**
     * Gets the names of the players that have been
     * invited to join this {@link PlayerFaction}.
     *
     * @return set of invited player names
     */
    public Set<String> getInvitedPlayerNames() {
        return invitedPlayers;
    }

    public Optional<Location> getHome() {
        return home == null ? Optional.empty() : Optional.of(home.getLocation());
    }

    public void setHome(@Nullable Location home) {
        //TODO: Account for in the CORE !!!!!!!!!!!!!!!!!!!!
        /*if (home == null && this.home != null) {
            TeleportTimer timer = HCF.getPlugin().getTimerManager().getTeleportTimer();
            for (Player player : getOnlinePlayers()) {
                Location destination = timer.getDestination(player);
                if (Objects.equals(destination, this.home.getLocation())) {
                    timer.clearCooldown(player);
                    player.sendMessage(ChatColor.RED + "Your home was unset, so your " + timer.getName() + ChatColor.RED + " timer has been cancelled");
                }
            }
        }*/

        PlayerFactionHomeSetEvent event = new PlayerFactionHomeSetEvent(this, this.home == null ? null : this.home.getLocation(), home, !Bukkit.isPrimaryThread());
        Bukkit.getServer().getPluginManager().callEvent(event);

        this.home = home == null ? null : new PersistableLocation(home);
    }

    public Optional<String> getAnnouncement() {
        return Optional.ofNullable(announcement);
    }

    public void setAnnouncement(@Nullable String announcement) {
        this.announcement = announcement;
    }

	@Override
    public boolean isRaidable() {
        return deathsUntilRaidable <= 0;
    }

    @Override
    public double getDeathsUntilRaidable() {
        return getDeathsUntilRaidable(true);
    }

    @Override
    public double getMaximumDeathsUntilRaidable() {
        int membersSize = members.size();

        if(membersSize >= 25) return 7.5;
        if(membersSize >= 20) return 6.5;
        if(membersSize >= 10) return 5.5;
        if(membersSize >= 5) return 4.5;
        if(membersSize >= 3) return 2.5;
        if(membersSize == 2) return 2.2;
        if(membersSize == 1) return 1.1;

        return Math.min(Configuration.factionMaximumDtr, membersSize * 0.9);
    }

    public double getDeathsUntilRaidable(boolean updateLastCheck) {
        if (updateLastCheck) updateDeathsUntilRaidable();

        return deathsUntilRaidable;
    }

    public ChatColor getDtrColour() {
        updateDeathsUntilRaidable();
        if (deathsUntilRaidable < 0) return ChatColor.RED;
		else if (deathsUntilRaidable < 1) return ChatColor.YELLOW;
		else return ChatColor.GREEN;
    }

    /**
     * Updates the deaths until raidable value depending
     * how much is gained every x seconds as set in configuration.
     */
    private void updateDeathsUntilRaidable() {
        if (getRegenStatus() == RegenStatus.REGENERATING) {
            long now = System.currentTimeMillis();
            long millisPassed = now - lastDtrUpdateTimestamp;
            long millisBetweenUpdates = Configuration.factionDtrUpdateMillis;

            if (millisPassed >= millisBetweenUpdates) {
                long remainder = millisPassed % millisBetweenUpdates;  // the remaining time until the next update
                int multiplier = (int) (((double) millisPassed + remainder) / millisBetweenUpdates);
                lastDtrUpdateTimestamp = now - remainder;
                setDeathsUntilRaidable(deathsUntilRaidable + (multiplier * millisBetweenUpdates));
            }
        }
    }

    @Override
    public double setDeathsUntilRaidable(double deathsUntilRaidable) {
        return setDeathsUntilRaidable(deathsUntilRaidable, true);
    }

    private double setDeathsUntilRaidable(double deathsUntilRaidable, boolean limit) {
        deathsUntilRaidable = Math.round(deathsUntilRaidable * 100.0) / 100.0; // remove trailing numbers after decimal
        if (limit) deathsUntilRaidable = Math.min(deathsUntilRaidable, getMaximumDeathsUntilRaidable());

        // the DTR is the same, don't call an event
        if (Math.abs(deathsUntilRaidable - this.deathsUntilRaidable) != 0) {
            FactionDtrChangeEvent event = new FactionDtrChangeEvent(FactionDtrChangeEvent.DtrUpdateCause.REGENERATION, this, this.deathsUntilRaidable, deathsUntilRaidable);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                deathsUntilRaidable = Math.round(event.getNewDtr() * 100.0) / 100.0;
				// Inform the server for easier log lookups for 'insiding' etc.
				if (deathsUntilRaidable > 0 && this.deathsUntilRaidable <= 0)
					JavaPlugin.getPlugin(HCFactions.class).getLogger().info("Faction " + getName() + " is now raidable.");

                lastDtrUpdateTimestamp = System.currentTimeMillis();
                return this.deathsUntilRaidable = deathsUntilRaidable;
            }
        }

        return this.deathsUntilRaidable;
    }

    protected long getRegenCooldownTimestamp() {
        return regenCooldownTimestamp;
    }

    @Override
    public long getRemainingRegenerationTime() {
        return regenCooldownTimestamp == 0L ? 0L : regenCooldownTimestamp - System.currentTimeMillis();
    }

    @Override
    public void setRemainingRegenerationTime(long millis) {
        long systemMillis = System.currentTimeMillis();
        regenCooldownTimestamp = systemMillis + millis;

        // needs to be multiplied by 2 because as soon as they lose regeneration delay, the timestamp will update
        lastDtrUpdateTimestamp = systemMillis + (Configuration.factionDtrUpdateMillis * 2);
    }

    @Override
    public RegenStatus getRegenStatus() {
        if (getRemainingRegenerationTime() > 0L) return RegenStatus.PAUSED;
		else if (getMaximumDeathsUntilRaidable() > deathsUntilRaidable) return RegenStatus.REGENERATING;
		else return RegenStatus.FULL;
    }
     /*
      * Soon can be configurable
      * Like DTRShock configuration file
      * Especially thanks to DoctorDark 
      * For let me do this.
      * And will be using PlaceHolder!
      */
    @Override
    public void sendInformation(CommandSender sender) {
        String leaderName = null;
        Set<String> allyNames = new HashSet<>();
        
        for (Map.Entry<UUID, FactionRelation> entry : relations.entrySet()) {
            Faction faction = HCFactions.getInstance().getFactionManager().getFaction(entry.getKey());
            if (faction instanceof PlayerFaction) {
                PlayerFaction ally = (PlayerFaction) faction;
                allyNames.add(ally.getFormattedName(ally) + ChatColor.GOLD +
                        " [" + ChatColor.WHITE + ally.getOnlinePlayers(sender).size() + ChatColor.GRAY + '/' + ChatColor.WHITE + ally.members.size() + ChatColor.GOLD + "]");
            }
        }

        int combinedKills = 0;
        Set<String> memberNames = new HashSet<>();
        Set<String> captainNames = new HashSet<>();
        
        for (Map.Entry<UUID, FactionMember> entry : members.entrySet()) {
            FactionMember factionMember = entry.getValue();
            Optional<Player> target = factionMember.toOnlinePlayer();

       FactionUser user = HCFactions.getInstance().getUserManager().getUser(entry.getKey());
            int kills = user.getKills();
            combinedKills += kills;

            ChatColor color;
           /* Deathban deathban = user.getDeathban();
            if (deathban != null && deathban.isActive()) {
                color = ChatColor.RED;
            } else if (!target.isPresent() || (sender instanceof Player && !((Player) sender).canSee(target.get()))) {
                color = ChatColor.GRAY;
            } else {
                color = ChatColor.GREEN;
            }*/
            String memberName = factionMember.getCachedName() + ChatColor.GRAY + " [" + ChatColor.RED  +kills + ChatColor.GRAY + "]";
           //  String memberName = color + factionMember.getCachedName() + ChatColor.GRAY + " [" + ChatColor.RED + kills + ChatColor.GRAY + "]";
            switch (factionMember.getRole()) {
                case LEADER:
                    leaderName = memberName;
                    break;
                case CAPTAIN:
                    captainNames.add(memberName);
                    break;
                case MEMBER:
                    memberNames.add(memberName);
                    break;
            }
        }

        sender.sendMessage(CC.translate("&6&m----------&7["+getFormattedName(sender)+"&7]&6&m----------"));
       //   sender.sendMessage(getFormattedName(sender) + ChatColor.GRAY + " (" + getOnlinePlayers(sender).size() + "/" + members.size() + " online) " +
       //         ChatColor.RED + getDtrColour() + JavaUtils.format(getDeathsUntilRaidable(false)) + ChatColor.GRAY + " DTR");
        sender.sendMessage(CC.translate("&6DTR: &e" + JavaUtils.format(getDeathsUntilRaidable(false))));
        sender.sendMessage(CC.translate("&6Faction Home: &e"+ (home == null ? "None" : String.valueOf(home.getLocation().getBlockX()) + " "+String.valueOf(home.getLocation().getBlockZ()))));

        if (!allyNames.isEmpty())
			sender.sendMessage(CC.translate("&6Allies: &c" + String.join(ChatColor.GRAY + ", " + "&c", allyNames)));
		else sender.sendMessage(CC.translate("&6Allies: &cNone"));
            sender.sendMessage(CC.translate("&6Balance: &f$"  + balance));
            sender.sendMessage(CC.translate("&6Members: &e"+ getOnlinePlayers(sender).size() + " / "+members.size()));
            if(!getOnlinePlayers().isEmpty()) {
            	List<String> players = getOnlineNames();
            	// falta agregar las kills
            	sender.sendMessage(CC.translate("&6Members online("+getOnlinePlayers(sender).size()+"): &e"+String.join(getColorRelation(sender)+", ", players)));
            }
        if(!getOfflinePlayers(sender).isEmpty()) {
        	List<String> players = getOfflinePlayers(); 
           	sender.sendMessage(CC.translate("&6Members offline("+getOfflinePlayers(sender).size()+"): &e" + String.join(getColorRelation(sender)+", ", players)));
        }
       /* if (leaderName != null) {
            sender.sendMessage(ChatColor.YELLOW + "Leader: " + ChatColor.RED + leaderName);
        }

        if (!captainNames.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Captains: " + ChatColor.RED + String.join(ChatColor.GRAY + ", " + ChatColor.RED, captainNames));
        }

        if (!memberNames.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Members: " + ChatColor.RED + String.join(ChatColor.GRAY + ", " + ChatColor.RED, memberNames));
        }*/

       
        sender.sendMessage(ChatColor.GOLD+ "Total Kills: " + ChatColor.RED + combinedKills);
        sender.sendMessage(ChatColor.GOLD + "Deaths till Raidable: " +
                getRegenStatus().getSymbol() + getDtrColour() + JavaUtils.format(getDeathsUntilRaidable(false)) +
                ChatColor.GRAY + "/" + JavaUtils.format(getMaximumDeathsUntilRaidable()));

        long dtrRegenRemaining = getRemainingRegenerationTime();
        if (dtrRegenRemaining > 0L)
			sender.sendMessage(ChatColor.GOLD + "Time Until Regen: " + ChatColor.RED + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true));

     //   sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    private String formatPlayers(Collection<?> players) {
        return players.stream()
                .map(player -> {
                  if (player instanceof Player) return ((Player) player).getName();
				  else if (player instanceof OfflinePlayer) return ((OfflinePlayer) player).getName();
				  else return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
      }
   /* @Override
    public void sendInformation(CommandSender sender) {
        HCFactions plugin = JavaPlugin.getPlugin(HCFactions.class);
        Set<String> allyNames = new HashSet<>();
        String leaderName = null;

        List<UUID> deadRelations = new ArrayList<>();
        for (Map.Entry<UUID, FactionRelation> entry : relations.entrySet()) {//TODO: Implement name based caching
            Faction faction;
            try {
                faction = plugin.getFactionManager().getFaction(entry.getKey());
                if (faction instanceof PlayerFaction) {
                    PlayerFaction ally = (PlayerFaction) faction;
                    allyNames.add(plugin.getMessages().getString("factions.show.playerfaction.parts.ally_format")
                            .replace("{allyName}", ally.getFormattedName(sender))
                            .replace("{allyMembersOnline}", String.valueOf(ally.getOnlinePlayers(sender).size()))
                            .replace("{allyMembersTotal}", String.valueOf(ally.members.size())));
                }
            } catch (NoFactionFoundException e){
                deadRelations.add(entry.getKey());
            }
        }

        if(!deadRelations.isEmpty()){
            deadRelations.forEach(relations::remove);
            deadRelations.clear();
        }

        int combinedKills = 0;
        Set<String> memberNames = new HashSet<>();
        Set<String> captainNames = new HashSet<>();
        Set<String> coleaders = new HashSet<>();
        for (Map.Entry<UUID, FactionMember> entry : members.entrySet()) {
            FactionMember factionMember = entry.getValue();
            Optional<Player> target = factionMember.toOnlinePlayer();

            int kills = plugin.getStats().getKills(factionMember.getUniqueId());
            combinedKills += kills;

          FactionUser coreMember = plugin.getServer().isPrimaryThread() ? HCF.getPlugin().getUserManager().getUser(factionMember.getUniqueId()) : HCF.getPlugin().getUserManager().getUserAsync(factionMember.getUniqueId());
          Deathban deathban = coreMember.getDeathban();

           ChatColor colour;
            if (deathban != null && deathban.isActive()) {
                colour = ChatColor.RED;
            } else if (!target.isPresent() || (sender instanceof Player && !((Player) sender).canSee(target.get()))) {
                colour = ChatColor.GRAY;
            } else {
                colour = ChatColor.GREEN;
            }


            String memberName = plugin.getMessages().getString("factions.show.playerfaction.parts.member_format")
               .replace("{player}", colour + factionMember.getCachedName())
                    .replace("{playerKills}", String.valueOf(kills));
            switch (factionMember.getRole()) {
                case LEADER:
                    leaderName = memberName;
                    break;
                case COLEADER:
                    coleaders.add(memberName);
                    break;
                case CAPTAIN:
                    captainNames.add(memberName);
                    break;
                case MEMBER:
                    memberNames.add(memberName);
                    break;
            }
        }

        sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);

        // Show the banner with the Home location.
        sender.sendMessage(plugin.getMessages().getString("factions.show.playerfaction.top")
                .replace("{factionName}", getFormattedName(sender))
                .replace("{factionMembersOnline}", String.valueOf(getOnlinePlayers(sender).size()))
                .replace("{factionMembersTotal}", String.valueOf(members.size()))
                .replace("{home}", (home == null ?
                        plugin.getMessages().getString("factions.show.playerfaction.parts.home_format.none") :
                        plugin.getMessages().getString("factions.show.playerfaction.parts.home_format.set")
                                .replace("{factionHomeX}", String.valueOf(home.getLocation().getBlockX()))
                                .replace("{factionHomeZ}", String.valueOf(home.getLocation().getBlockZ()))))
                .replace("{factionOpenStatus}", (open ?
                        plugin.getMessages().getString("factions.show.playerfaction.parts.openstatus.open") :
                        plugin.getMessages().getString("factions.show.playerfaction.parts.openstatus.closed"))));

        if (!allyNames.isEmpty()) {
            sender.sendMessage(plugin.getMessages().getString("factions.show.playerfaction.allies")
                    .replace("{allies}", INFO_JOINER.join(allyNames)));
        }

        if (leaderName != null) {
            sender.sendMessage(plugin.getMessages().getString("factions.show.playerfaction.leader")
                    .replace("{leader}", leaderName));
        }

        if(!coleaders.isEmpty()){
            //Commands-Factions-Show-CoLeadersListFormat: "  Co-Leaders: {coLeadersList}"
            sender.sendMessage(plugin.getMessages().getString("factions.show.playerfaction.coleaders")
                    .replace("{coleaders}", INFO_JOINER.join(coleaders)));
        }

        if (!captainNames.isEmpty()) {
            sender.sendMessage(plugin.getMessages().getString("factions.show.playerfaction.captains")
                    .replace("{captains}", INFO_JOINER.join(captainNames)));
        }

        if (!memberNames.isEmpty()) {
            sender.sendMessage(plugin.getMessages().getString("factions.show.playerfaction.members")
                    .replace("{members}", INFO_JOINER.join(memberNames)));
        }

        // Show announcement if the sender is in this faction.
        if (sender instanceof Player) {
            try{
                Faction playerFaction = plugin.getFactionManager().getPlayerFaction((Player) sender);
                if (playerFaction != null && playerFaction.equals(this)) {
                    if(announcement != null){
                        sender.sendMessage(plugin.getMessages().getString("factions.show.playerfaction.announcement").replace("{announcement}", announcement));
                    }
                    sender.sendMessage(plugin.getMessages().getString("factions.show.playerfaction.lives").replace("{lives}", String.valueOf(lives)));
                }
            }catch (NoFactionFoundException ignored){}
        }

        sender.sendMessage(plugin.getMessages().getString("factions.show.playerfaction.bottom")
                .replace("{balance}", String.valueOf( EconomyManager.ECONOMY_SYMBOL + balance))
                .replace("{kills}", String.valueOf(combinedKills))
                .replace("{founded}", DateTimeFormats.DAY_MTH_YR_HR_MIN_AMPM.format(creationMillis))
                .replace("{factionDeathsUntilRaidable}", getRegenStatus().getSymbol() + getDtrColour() + JavaUtils.format(getDeathsUntilRaidable(false)))
                .replace("{maximumDeathsUntilRaidable}", JavaUtils.format(getMaximumDeathsUntilRaidable())));

        long dtrRegenRemaining = getRemainingRegenerationTime();
        if (dtrRegenRemaining > 0L) {
            sender.sendMessage(plugin.getMessages().getString("factions.show.playerfaction.regen")
                    .replace("{factionRegenTime}", String.valueOf(DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true))));
        }

        sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }*/

    private static final UUID[] EMPTY_UUID_ARRAY = {};

    /**
     * Sends a message to all online {@link FactionMember}s.
     *
     * @param message the message to send
     */
    public void broadcast(String message) {
        broadcast(message, EMPTY_UUID_ARRAY);
    }

    /**
     * Sends an array of messages to all online {@link FactionMember}s.
     *
     * @param messages the messages to send.
     */
    public void broadcast(String[] messages) {
        broadcast(messages, EMPTY_UUID_ARRAY);
    }

    /**
     * Sends a message to all online {@link FactionMember}s ignoring those selected in the var-args.
     *
     * @param message the message to send.
     * @param ignore  the {@link FactionMember} with {@link UUID}s not to send message to
     */
    public void broadcast(String message, @Nullable UUID... ignore) {
        broadcast(new String[]{message}, ignore);
    }

    /**
     * Sends an array of messages to all online {@link FactionMember}s ignoring those selected in the var-args.
     *
     * @param messages the message to send
     * @param ignore   the {@link FactionMember} with {@link UUID}s not to send message to
     */
    public void broadcast(String[] messages, UUID... ignore) {
        Objects.requireNonNull(messages, "Messages cannot be null");
        Objects.requireNonNull(messages.length > 0, "Message array cannot be empty");
        Collection<Player> players = getOnlinePlayers();
        Collection<UUID> ignores = ignore.length == 0 ? Collections.emptySet() : Sets.newHashSet(ignore);
        players.stream().filter(player -> !ignores.contains(player.getUniqueId())).forEach(player -> player.sendMessage(messages));
    }

    public boolean hasCooldown(UUID user){
        return previousMembers.containsKey(user);
    }

    public void removeCooldown(UUID user){
        previousMembers.remove(user);
    }

    /**
     * This method should only be used internally. If you wish to utilise focus, use the focus handler from the faction manager
     */
    @Deprecated
    public void af(FocusTarget target){
        focusTargets.put(target.getTarget(), target);
    }

    /**
     * This method should only be used internally. If you wish to utilise focus, use the focus handler from the faction manager
     */
    @Deprecated
    public void fr(FocusTarget target){
        focusTargets.remove(target.getTarget());
    }

    /**
     * This method should only be used internally. If you wish to utilise focus, use the focus handler from the faction manager
     */
    @Deprecated
    public UUID fmk(UUID target){
        return focusTargets.get(target).getMapKey();
    }

    /*
    Accepts faction or player UUID.
     */
    public boolean isFocused(UUID targetUUID){
        return focusTargets.containsKey(targetUUID);
    }
}
