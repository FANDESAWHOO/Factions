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

package org.hcgames.hcfactions;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;
import lombok.Getter;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@Getter
public class MongoManager {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> serverCollection;
    private MongoCollection<Document> factionMongoCollection;

    public void connect() {
        try {
            String host = HCFactions.getInstance().getConfig().getString("mongo.host");
            String databaseName = HCFactions.getInstance().getConfig().getString("mongo.database");
            String username = HCFactions.getInstance().getConfig().getString("mongo.username");
            String password = HCFactions.getInstance().getConfig().getString("mongo.password");
            String replicaSet = HCFactions.getInstance().getConfig().getString("mongo.replicaSet");

            if (host == null || databaseName == null || username == null || password == null || replicaSet == null) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "MongoDB configuration is missing or incomplete in config.yml!");
                return;
            }

            String uri = String.format("mongodb://%s:%s@%s/%s?replicaSet=%s", username, password, host, databaseName, replicaSet);


            mongoClient = MongoClients.create(uri);
            mongoDatabase = mongoClient.getDatabase(databaseName);


            serverCollection = mongoDatabase.getCollection("server");
            factionMongoCollection = mongoDatabase.getCollection("factions");

            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Connected to MongoDB successfully.");
        } catch (MongoException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error connecting to MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public MongoCollection getMongoCollection(String dat){
        return mongoDatabase.getCollection(dat);
    }

    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Disconnected from MongoDB.");
        }
    }
}
