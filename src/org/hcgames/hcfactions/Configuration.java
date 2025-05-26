package org.hcgames.hcfactions;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public class Configuration {

    private int factionNameMinCharacters;
    private int factionNameMaxCharacters;
    private int factionMaxMembers;
    private int factionMaxClaims;
    private int factionMaxAllies;
    
    private int factionDtrRegenFreezeBaseMinutes;
    private long factionDtrRegenFreezeBaseMilliseconds;
    private int factionDtrRegenFreezeMinutesPerMember;
    private long factionDtrRegenFreezeMillisecondsPerMember;
    private int factionMinimumDtr;
    private float factionMaximumDtr;
    private int factionDtrUpdateMillis;
    private float factionDtrUpdateIncrement;
    
    private ChatColor relationColourWarzone;
    private ChatColor relationColourWilderness;
    private ChatColor relationColourTeammate;
    private ChatColor relationColourAlly;
    private ChatColor relationColourEnemy;
    private ChatColor relationColourRoad;
    private ChatColor relationColourSafezone;
    
    private int antiRotationDelay;
    private boolean antiRotationEnabled;
    
    private boolean factionEndPortalEnabled;
    private int endPortalCenter;
    
    private int spawnRadiusOverworld;
    private int spawnRadiusNether;
    
    private int roadWidthLeft;
    private int roadWidthRight;
    private int roadLength;
    private int endPortalRadius;
    private boolean allowClaimsBesidesRoads;
    
    private int warzoneRadiusOverworld;
    private int warzoneRadiusNether;
    
    private List<String> factionDisallowedNames;
    
    private int factionHomeTeleportDelayNormal;
    private int factionHomeTeleportDelayNether;
    private int factionHomeTeleportDelayEnd;
    private boolean allowTeleportingInEnemyTerritory;
    private int maxHeightFactionHome;
    
    private boolean subclaimSignPrivate;
    private boolean subclaimSignCaptain;
    private boolean subclaimSignLeader;
    private boolean subclaimHopperCheck;
    
    private boolean preventAllyAttackDamage;
    private boolean messageDebug;

    public void load(HCFactions plugin) {
        FileConfiguration config = plugin.getConfig();
        
        factionNameMinCharacters = config.getInt("factions.NameMinCharacters");
        factionNameMaxCharacters = config.getInt("factions.NameMaxCharacters");
        factionMaxMembers = config.getInt("factions.maxMembers");
        factionMaxClaims = config.getInt("factions.maxClaims");
        factionMaxAllies = config.getInt("factions.maxAllies");
        factionDtrRegenFreezeBaseMinutes = config.getInt("factions.dtr.RegenFreezeBaseMinutes");
        factionDtrRegenFreezeBaseMilliseconds = TimeUnit.MINUTES.toMillis(factionDtrRegenFreezeBaseMinutes);
        factionDtrRegenFreezeMinutesPerMember = config.getInt("factions.dtr.minutesPerMember");
        factionDtrRegenFreezeMillisecondsPerMember = TimeUnit.MINUTES.toMillis(factionDtrRegenFreezeMinutesPerMember);
        factionMinimumDtr = config.getInt("factions.dtr.minimum");
        factionMaximumDtr = (float) config.getDouble("factions.dtr.maximum");
        factionDtrUpdateMillis = config.getInt("factions.dtr.millisecondsBetweenUpdates");
        factionDtrUpdateIncrement = (float) config.getDouble("factions.dtr.incrementBetweenUpdates");
        
        relationColourWarzone = ChatColor.valueOf(config.getString("factions.relationColours.warzone"));
        relationColourWilderness = ChatColor.valueOf(config.getString("factions.relationColours.wilderness"));
        relationColourTeammate = ChatColor.valueOf(config.getString("factions.relationColours.teammate"));
        relationColourAlly = ChatColor.valueOf(config.getString("factions.relationColours.ally"));
        relationColourEnemy = ChatColor.valueOf(config.getString("factions.relationColours.enemy"));
        relationColourRoad = ChatColor.valueOf(config.getString("factions.relationColours.road"));
        relationColourSafezone = ChatColor.valueOf(config.getString("factions.relationColours.safezone"));
        
        antiRotationDelay = config.getInt("factions.antirotation.delay");
        antiRotationEnabled = config.getBoolean("factions.antirotation.enabled");
        
        factionEndPortalEnabled = config.getBoolean("factions.endportal.enabled");
        endPortalCenter = config.getInt("factions.endportal.center");
        endPortalRadius = config.getInt("factions.endportal.radius");
        
        spawnRadiusOverworld = config.getInt("factions.spawn.radiusOverworld");
        spawnRadiusNether = config.getInt("factions.spawn.radiusNether");
        
        roadWidthLeft = config.getInt("factions.roads.widthLeft");
        roadWidthRight = config.getInt("factions.roads.widthRight");
        roadLength = config.getInt("factions.roads.length");
        allowClaimsBesidesRoads = config.getBoolean("factions.roads.allowClaimsBesides");
        
        warzoneRadiusOverworld = config.getInt("factions.warzone.radiusOverworld");
        warzoneRadiusNether = config.getInt("factions.warzone.radiusNether");
        
        factionDisallowedNames = config.getStringList("factions.disallowednames");
        
        factionHomeTeleportDelayNormal = config.getInt("factions.home.teleportdelay.normal");
        factionHomeTeleportDelayNether = config.getInt("factions.home.teleportdelay.nether");
        factionHomeTeleportDelayEnd = config.getInt("factions.home.teleportdelay.end");
        allowTeleportingInEnemyTerritory = config.getBoolean("factions.home.teleportdelay.allowTeleportingInEnemyTerritory");
        maxHeightFactionHome = config.getInt("factions.home.teleportdelay.maxHeight");
        
        subclaimSignPrivate = config.getBoolean("subclaimsigns.private");
        subclaimSignCaptain = config.getBoolean("subclaimsigns.Captain");
        subclaimSignLeader = config.getBoolean("subclaimsigns.leader");
        subclaimHopperCheck = config.getBoolean("subclaimsigns.hoppercheck");
        
        preventAllyAttackDamage = config.getBoolean("preventAllyAttackDamage");
        messageDebug = config.getBoolean("messageDebug");
    }
}
