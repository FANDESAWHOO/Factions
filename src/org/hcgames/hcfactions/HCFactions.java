package org.hcgames.hcfactions;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import technology.brk.util.file.Messages;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.hcgames.hcfactions.claim.Claim;
import org.hcgames.hcfactions.claim.ClaimHandler;
import org.hcgames.hcfactions.command.FactionExecutor;
import org.hcgames.hcfactions.command.LocationCommand;
import org.hcgames.hcfactions.command.RegenCommand;
import org.hcgames.hcfactions.faction.ClaimableFaction;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.PlayerFaction;
import org.hcgames.hcfactions.faction.system.EndPortalFaction;
import org.hcgames.hcfactions.faction.system.RoadFaction;
import org.hcgames.hcfactions.faction.system.SpawnFaction;
import org.hcgames.hcfactions.faction.system.SystemTeam;
import org.hcgames.hcfactions.faction.system.WarzoneFaction;
import org.hcgames.hcfactions.faction.system.WildernessFaction;
import org.hcgames.hcfactions.listener.*;
import org.hcgames.hcfactions.manager.FactionManager;
import org.hcgames.hcfactions.manager.FlatFileFactionManager;
import org.hcgames.hcfactions.manager.MongoFactionManager;
import org.hcgames.hcfactions.structure.FactionMember;
import org.hcgames.stats.Stats;


@Getter
public class HCFactions extends JavaPlugin {

    public static final Joiner SPACE_JOINER = Joiner.on(' ');
    public static final Joiner COMMA_JOINER = Joiner.on(", ");

    private static HCFactions instance;
    private MongoManager mongoManager;
    private WorldEditPlugin worldEdit;
    private boolean configLoaded;

    private FactionManager factionManager;
    private ClaimHandler claimHandler;
    private Stats stats;
    private Configuration configuration;
    private Messages messages;
    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) Claim.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) ClaimableFaction.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) EndPortalFaction.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) Faction.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) FactionMember.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) PlayerFaction.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) RoadFaction.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) SpawnFaction.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) RoadFaction.NorthRoadFaction.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) RoadFaction.EastRoadFaction.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) RoadFaction.SouthRoadFaction.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) RoadFaction.WestRoadFaction.class);
        ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) SystemTeam.class);

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
    public void onEnable() {
        instance = this;

       // Asegurar que la carpeta del plugin exista
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Verificar y regenerar config.yml si no existe
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getLogger().warning("config.yml not found, generating a new one...");
            saveResource("config.yml", false);
        }

        // Cargar configuración
        reloadConfig();
          

       /* if (config == null) {
            getLogger().severe("Failed to load config.yml!");
            return;
        }*/
        configuration = new Configuration();
        configuration.load(this);

        messages = new Messages(this, "messages.yml", configuration.isMessageDebug());

        // Registrar Managers y Listeners
        registerManagers();
        registerListeners();
        registerCommands();

        getLogger().info("HCFactions has been enabled successfully!");

        getServer().getScheduler().runTaskTimerAsynchronously(this, this::saveData, (60 * 20) * 5, (60 * 20) * 5);
    }
    @Override
    public void onDisable() {
        if (!configLoaded) return;
        saveData();
        if (mongoManager != null) mongoManager.disconnect();;
        saveConfig();
    }

    private void saveData() {
        factionManager.saveFactionData();
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new ClaimWandListener(this), this);
        manager.registerEvents(new NameCacheListener(this), this);
        manager.registerEvents(new SignSubclaimListener(this), this);
        manager.registerEvents(new ProtectionListener(this), this);
        manager.registerEvents(new FactionChatListener(this), this);
    }

    private void registerCommands() {
        getCommand("factions").setExecutor(new FactionExecutor(this));
        getCommand("location").setExecutor(new LocationCommand(this));
        getCommand("regen").setExecutor(new RegenCommand(this));
    }

    private void registerManagers() {
        if (getConfig().getBoolean("mongo.use", false)) {
            mongoManager = new MongoManager();
            mongoManager.connect();
            factionManager = new MongoFactionManager(this);
        } else {
            factionManager = new FlatFileFactionManager(this);
        }

        if (factionManager == null) {
            getLogger().severe("FactionManager failed to initialize!");
        } else {
            getLogger().info("FactionManager initialized successfully.");
        }

        claimHandler = new ClaimHandler(this);
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        Plugin statsPlugin = getServer().getPluginManager().getPlugin("Stats");
        if (statsPlugin instanceof Stats) {
            stats = (Stats) statsPlugin;
        }
    }

    public static HCFactions getInstance() {
        return instance;
    }
}
