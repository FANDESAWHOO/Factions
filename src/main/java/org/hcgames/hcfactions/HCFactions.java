package org.hcgames.hcfactions;


import com.google.common.base.Joiner;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.claim.ClaimHandler;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.faction.system.*;
import org.hcgames.hcfactions.listener.*;
import org.hcgames.hcfactions.manager.FactionManager;
import org.hcgames.hcfactions.manager.FlatFileFactionManager;
import org.hcgames.hcfactions.manager.MongoFactionManager;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.hcfactions.timer.TimerManager;
import org.hcgames.hcfactions.util.PersistableLocation;
import org.hcgames.hcfactions.util.cuboid.Cuboid;
import org.hcgames.hcfactions.util.cuboid.NamedCuboid;
import org.hcgames.hcfactions.util.itemdb.ItemDb;
import org.hcgames.hcfactions.util.itemdb.SimpleItemDb;
import org.hcgames.hcfactions.visualise.VisualiseHandler;
import org.hcgames.stats.Stats;
import org.mineacademy.fo.plugin.SimplePlugin;
/**
 * FORKED FROM HCGames (System Update) FACTIONS
 * DON'T SELL THIS PLUGIN
 * PLEASE DON'T TALK SHIT
 */
@Getter
public class HCFactions extends SimplePlugin {

    private ItemDb itemDb;
    public static final Joiner SPACE_JOINER = Joiner.on(' ');
  //  public static final Joiner COMMA_JOINER = Joiner.on(", ");
    private MongoManager mongoManager;
    private WorldEditPlugin worldEdit;
    private FactionManager factionManager;
    private ClaimHandler claimHandler;
    private Stats stats;
    private VisualiseHandler visualiseHandler;
    private TimerManager timerManager;


   public void register(){
       ConfigurationSerialization.registerClass(PersistableLocation.class);
       ConfigurationSerialization.registerClass(Cuboid.class);
       ConfigurationSerialization.registerClass(NamedCuboid.class);
       ConfigurationSerialization.registerClass(Claim.class);
       ConfigurationSerialization.registerClass(ClaimableFaction.class);
       ConfigurationSerialization.registerClass(EndPortalFaction.class);
       ConfigurationSerialization.registerClass(Faction.class);
       ConfigurationSerialization.registerClass(FactionMember.class);
       ConfigurationSerialization.registerClass(PlayerFaction.class);
       ConfigurationSerialization.registerClass(RoadFaction.class);
       ConfigurationSerialization.registerClass(SpawnFaction.class);
       ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
       ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
       ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
       ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
       ConfigurationSerialization.registerClass(SystemTeam.class);

       FactionManager.registerSystemFaction(EndPortalFaction.class);
       FactionManager.registerSystemFaction(RoadFaction.EastRoadFaction.class);
       FactionManager.registerSystemFaction(RoadFaction.NorthRoadFaction.class);
       FactionManager.registerSystemFaction(RoadFaction.SouthRoadFaction.class);
       FactionManager.registerSystemFaction(RoadFaction.WestRoadFaction.class);
       FactionManager.registerSystemFaction(SpawnFaction.class);
       FactionManager.registerSystemFaction(WarzoneFaction.class);
       FactionManager.registerSystemFaction(WildernessFaction.class);
   }
    @Override
    public void onPluginLoad() {
    register();
    }
   
    @Override
    public void onPluginStart() {
        registerManagers();
        registerListeners();

        getLogger().info("HCFactions has been enabled successfully!");

        getServer().getScheduler().runTaskTimerAsynchronously(this, this::saveData, (60 * 20) * 5, (60 * 20) * 5);
    }
    @Override
    public void onPluginStop() {

        saveData();
        if (mongoManager != null) mongoManager.disconnect();
		saveConfig();
    }

    @Override
    protected void onReloadablesStart() {
        super.onReloadablesStart();
        // FOR TASKS.
    }

    private void saveData() {
        factionManager.saveFactionData();
    }

    private void registerListeners() {
        registerEvents(new ClaimWandListener(this));
        registerEvents(new NameCacheListener(this));
        registerEvents(new SignSubclaimListener(this));
        registerEvents(new ProtectionListener(this));
        registerEvents(new FactionChatListener(this));
    }



    private void registerManagers() {

        itemDb = new SimpleItemDb(this);
        visualiseHandler = new VisualiseHandler();
        if (Configuration.mongo) {
            mongoManager = new MongoManager();
            mongoManager.connect();
            factionManager = new MongoFactionManager(this);
        } else factionManager = new FlatFileFactionManager(this);

		getLogger().info("FactionManager initialized successfully.");
    	timerManager = new TimerManager(this);
		claimHandler = new ClaimHandler(this);
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        Plugin statsPlugin = getServer().getPluginManager().getPlugin("Stats");
        if (statsPlugin instanceof Stats) stats = (Stats) statsPlugin;
    }

    public static HCFactions getInstance() {
        return (HCFactions) SimplePlugin.getInstance();
    }
}
