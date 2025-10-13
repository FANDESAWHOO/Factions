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

package org.hcgames.hcfactions.manager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.hcgames.hcfactions.HCFactions;
import org.hcgames.hcfactions.faction.Faction;
import org.hcgames.hcfactions.faction.system.EndPortalFaction;
import org.hcgames.hcfactions.faction.system.RoadFaction;
import org.hcgames.hcfactions.faction.system.SpawnFaction;
import org.hcgames.hcfactions.faction.system.SystemFaction;
import org.hcgames.hcfactions.faction.system.SystemTeam;
import org.hcgames.hcfactions.util.configuration.Config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MongoFactionManager extends FlatFileFactionManager implements FactionManager{

    private MongoCollection<Document> collection;

    public MongoFactionManager(HCFactions plugin){
        super(plugin);
    }

    @Override
    public void init(){
        collection = plugin.getMongoManager().getMongoDatabase().getCollection("Hardcore");
        config = new Config(plugin, "factions.yml");
    }

    @Override
    public boolean removeFaction(Faction faction, CommandSender sender) {
        if(super.removeFaction(faction, sender)){
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () ->
                    collection.deleteOne(new Document("_id", faction.getUniqueID().toString())));
            return true;
        }

        return false;
    }

    @Override
    public void reloadFactionData() {
        this.factionNameMap.clear();
        final int[] factions = {0};

        collection.find().forEach((com.mongodb.Block<? super Document>) document -> {
            try{
                Class<?> clazz = Class.forName(document.getString("=="));
                Constructor<?> constructor = clazz.getConstructor(Document.class);
                cacheFaction((Faction) constructor.newInstance(document));
                factions[0]++;
            }catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e){
                e.printStackTrace();
            }
        });

        for(Class<? extends SystemFaction> systemFaction : FactionManager.systemFactions.getSystemFactions()){
            try{
                Method method = systemFaction.getDeclaredMethod("getUUID");
                UUID result = (UUID) method.invoke(null);

                if(!factionUUIDMap.containsKey(result)){
                    Constructor<?> constructor = systemFaction.getConstructor();

                    Faction faction = (Faction) constructor.newInstance();
                    cacheFaction(faction);

                    factions[0]++;
                    plugin.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Faction " + faction.getName() + " not found, created.");
                }
            }catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e){
                e.printStackTrace();
            }
        }
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Loaded " + factions[0] + " factions.");
        Set<Faction> adding = new HashSet<>();
		if (!factionNameMap.containsKey("NorthRoad")) { // TODO: more reliable
			adding.add(new RoadFaction.NorthRoadFaction());
			adding.add(new RoadFaction.EastRoadFaction());
			adding.add(new RoadFaction.SouthRoadFaction());
			adding.add(new RoadFaction.WestRoadFaction());
		}

		if (!factionNameMap.containsKey("Spawn")) { // TODO: more reliable
			adding.add(new SpawnFaction());
		}


		if (!this.factionNameMap.containsKey("EndPortal")) { // TODO: more reliable
			adding.add(new EndPortalFaction());
		}
		// Now load the Spawn, etc factions.
				for (Faction added : adding) {
					this.cacheFaction(added);
					Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Faction " + added.getName() + " not found, created.");
				}
    }
    

    void addSysFaction(Class<? extends SystemFaction> clazz){

    }

    @Override
    public void saveFactionData() {
        for(UUID uuid : factionUUIDMap.keySet()){
            Faction faction = factionUUIDMap.get(uuid);

            Document query = new Document();
            query.put("_id", faction.getUniqueID().toString());

            Document values = faction.toDocument();
            values.put("_id", faction.getUniqueID().toString());
            values.put("==", faction.getClass().getName());

            collection.updateOne(query, new Document("$set", values), new UpdateOptions().upsert(true));
        }

        super.saveFactionData(); //Also save to flatfile
    }
}
