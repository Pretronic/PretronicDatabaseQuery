package net.pretronic.databasequery.mongodb.query.utils;

import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BuildContext {

    protected final MongoDBDatabaseCollection collection;
    protected final List<Bson> findQuery;
    protected boolean negate;

    private BuildContext(MongoDBDatabaseCollection collection) {
        this.collection = collection;
        this.negate = false;
        this.findQuery = new ArrayList<>();
    }

    public List<Bson> getFindQuery() {
        return findQuery;
    }

    protected void add(Bson bson) {
        findQuery.add(bson);
    }

    protected void addAll(Collection<Bson> bsons) {
        findQuery.addAll(bsons);
    }

    public static BuildContext newContext(MongoDBDatabaseCollection collection) {
        return new BuildContext(collection);
    }
}
