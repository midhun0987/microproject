package db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    private static final MongoClient client = MongoClients.create("mongodb://localhost:27017");
    private static final MongoDatabase database = client.getDatabase("inventory");

    public static MongoDatabase getDatabase() {
        return database;
    }
}
