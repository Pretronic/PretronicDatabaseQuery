package net.pretronic.databasequery.mongodb.query;

import com.mongodb.client.MongoCursor;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.common.query.type.AbstractReplaceQuery;
import net.pretronic.databasequery.common.query.type.AbstractUpdateQuery;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import net.pretronic.databasequery.mongodb.query.utils.BuildContext;
import net.pretronic.databasequery.mongodb.query.utils.MongoDBQueryUtil;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDBReplaceQuery extends AbstractReplaceQuery<MongoDBDatabaseCollection> {

    public MongoDBReplaceQuery(MongoDBDatabaseCollection collection) {
        super(collection);
    }

    @Override
    public QueryResult execute(Object... values) {
        BuildContext context = BuildContext.newContext(this.collection);
        List<AbstractUpdateQuery.SetEntry> setEntries = new ArrayList<>();
        for (Entry entry : entries) {
            if(entry instanceof AbstractUpdateQuery.SetEntry) {
                setEntries.add((AbstractUpdateQuery.SetEntry) entry);
            } else {
                MongoDBQueryUtil.buildEntry(context, entry);
            }
        }

        MongoDBQueryUtil.printQuery(context);

        DefaultQueryResult result = new DefaultQueryResult();
        MongoCursor<Document> cursor = collection.getCollection().aggregate(context.getFindQuery()).cursor();
        while(cursor.hasNext()) {
            Document document = cursor.next();
            DefaultQueryResultEntry entry = new DefaultQueryResultEntry(collection.getDatabase().getDriver());
            document.forEach(entry::addEntry);
            result.addEntry(entry);
            Document update = new Document();
            for (AbstractUpdateQuery.SetEntry setEntry : setEntries) {
                update.append(setEntry.getField(), setEntry.getValue());
            }
            this.collection.getCollection().replaceOne(document, update);
        }
        return result;
    }
}
