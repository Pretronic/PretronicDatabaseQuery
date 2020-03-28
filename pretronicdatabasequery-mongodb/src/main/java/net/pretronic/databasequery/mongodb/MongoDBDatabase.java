package net.pretronic.databasequery.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.DatabaseCollectionType;
import net.pretronic.databasequery.api.query.Query;
import net.pretronic.databasequery.api.query.QueryGroup;
import net.pretronic.databasequery.api.query.QueryTransaction;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.type.CreateQuery;
import net.pretronic.databasequery.common.AbstractDatabase;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import net.pretronic.databasequery.mongodb.driver.MongoDBDatabaseDriver;
import net.pretronic.databasequery.mongodb.query.MongoDBCreateQuery;
import org.bson.Document;

public class MongoDBDatabase extends AbstractDatabase<MongoDBDatabaseDriver> {

    private final MongoDatabase database;
    private final MongoCollection<Document> counters;

    public MongoDBDatabase(String name, MongoDBDatabaseDriver driver) {
        super(name, driver);
        this.database = driver.getClient().getDatabase(name);
        this.counters = initCountersCollection();
    }

    @Override
    public DatabaseCollection getCollection(String name) {
        return new MongoDBDatabaseCollection(name, this, DatabaseCollectionType.NORMAL);
    }

    @Override
    public CreateQuery createCollection(String name) {
        return new MongoDBCreateQuery(name, this);
    }

    @Override
    public void dropCollection(String name) {

    }

    @Override
    public void drop() {

    }

    @Override
    public QueryTransaction transact() {
        return null;
    }

    @Override
    public QueryGroup group() {
        return null;
    }

    @Override
    public QueryResult execute(Query... queries) {
        return null;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoCollection<Document> getCounters() {
        return counters;
    }

    public void addCounter(String collectionName, String field) {
        this.counters.insertOne(new Document("collectionName", collectionName).append("field", field).append("nextId", 1));
    }

    private MongoCollection<Document> initCountersCollection() {
        MongoCollection<Document> counters;
        if(!existCollection("counters")) {
            this.database.createCollection("counters");

        }
        counters = this.database.getCollection("counters");
        return counters;
    }

    public boolean existCollection(String name) {
        for (String name0 : this.database.listCollectionNames()) {
            if(name0.equals(name)) return true;
        }
        return false;
    }
}
