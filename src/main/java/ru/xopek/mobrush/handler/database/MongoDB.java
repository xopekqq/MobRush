package ru.xopek.mobrush.handler.database;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import ru.xopek.mobrush.MobRush;


public class MongoDB {
    private static final String databaseName = "mobrush";
    private static final String collectionName = "players";

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    /**
     * Подключение к MongoDB для хранения игроков
     * и сохранения их прогресса в мини-игре
     */

    public MongoDB(MobRush inst) {
        String uri = inst.getConfig().getString("mongodb.uri");

        try {
            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase(databaseName);
            collection = database.getCollection(collectionName);

            collection.createIndex(Indexes.ascending("uuid"));
            System.out.println("MongoDB success connection.");
        } catch (Exception e) {
            System.out.println("Failed to connect to MongoDB: " + e.getMessage());
            throw new RuntimeException("MongoDB init failed", e);
        }
    }

    public void savePlayer(RushPlayer player) {
        Document playerDoc = new Document("uuid", player.getUuid())
                .append("money", player.getMoney())
                .append("xp", player.getXp())
                .append("rebirth", player.getRebirth());

        collection.replaceOne(
                Filters.eq("uuid", player.getUuid()),
                playerDoc,
                new ReplaceOptions().upsert(true)
        );
    }

    public RushPlayer getPlayer(String uuid) {
        Document playerDoc = collection.find(Filters.eq("uuid", uuid)).first();

        if (playerDoc == null) return new RushPlayer(uuid, 0, 0, 1);

        int money = playerDoc.getInteger("money", 0);
        int xp = playerDoc.getInteger("xp", 0);
        int rebirth = playerDoc.getInteger("rebirth", 1);

        return new RushPlayer(uuid, money, xp, rebirth);
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}