package net.uniquepixels.playerqueue;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.val;
import org.bson.Document;

public class DatabaseHandler {

    private final MongoClient client;
    @Getter
    private final MongoCollection<Document> httpAuthenticationDatabase;

    public DatabaseHandler(String connectionString) {

        this.client = MongoClients.create(connectionString);
        val database = this.client.getDatabase("uniquepixels");
        this.httpAuthenticationDatabase = database.getCollection("httpAuthentication");

    }

    public String getHttpToken() {
        return this.httpAuthenticationDatabase.find(Filters.eq("platform", "playerqueuemodule")).first().getString("token");
    }

    public void disConnect() {
        this.client.close();
    }
}
