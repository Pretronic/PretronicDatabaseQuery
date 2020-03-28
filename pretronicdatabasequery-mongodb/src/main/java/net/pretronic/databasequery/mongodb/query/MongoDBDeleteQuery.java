package net.pretronic.databasequery.mongodb.query;

import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.type.DeleteQuery;
import net.pretronic.databasequery.common.query.type.AbstractDeleteQuery;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;

public class MongoDBDeleteQuery extends AbstractDeleteQuery<MongoDBDatabaseCollection> {

    public MongoDBDeleteQuery(MongoDBDatabaseCollection collection) {
        super(collection);
    }

    @Override
    public QueryResult execute(Object... values) {
        return null;
    }
}
