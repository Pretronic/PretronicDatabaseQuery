package net.pretronic.databasequery.mongodb.query;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.type.DeleteQuery;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.common.query.type.AbstractDeleteQuery;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import net.pretronic.databasequery.mongodb.query.utils.BuildContext;
import net.pretronic.databasequery.mongodb.query.utils.MongoDBQueryUtil;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MongoDBDeleteQuery extends AbstractDeleteQuery<MongoDBDatabaseCollection> {

    public MongoDBDeleteQuery(MongoDBDatabaseCollection collection) {
        super(collection);
    }

    @Override
    public QueryResult execute(Object... values) {
        BuildContext context = BuildContext.newContext(this.collection);
        MongoDBQueryUtil.buildEntries(context, this.entries);

        MongoDBQueryUtil.printQuery(context);

        DefaultQueryResult result = new DefaultQueryResult();
        MongoCursor<Document> cursor = collection.getCollection().aggregate(context.getFindQuery()).cursor();
        while(cursor.hasNext()) {
            Document document = cursor.next();
            DefaultQueryResultEntry entry = new DefaultQueryResultEntry(collection.getDatabase().getDriver());
            document.forEach(entry::addEntry);
            result.addEntry(entry);

            this.collection.getCollection().deleteOne(document);
        }
        return result;
    }
}
