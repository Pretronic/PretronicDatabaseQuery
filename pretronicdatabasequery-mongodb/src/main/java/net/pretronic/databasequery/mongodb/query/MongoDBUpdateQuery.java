package net.pretronic.databasequery.mongodb.query;

import com.mongodb.client.MongoCursor;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.common.query.type.AbstractChangeAndSearchQuery;
import net.pretronic.databasequery.common.query.type.AbstractUpdateQuery;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import net.pretronic.databasequery.mongodb.query.utils.BuildContext;
import net.pretronic.databasequery.mongodb.query.utils.MongoDBQueryUtil;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoDBUpdateQuery extends AbstractUpdateQuery<MongoDBDatabaseCollection> {

    public MongoDBUpdateQuery(MongoDBDatabaseCollection collection) {
        super(collection);
    }

    @Override
    public QueryResult execute(Object... values) {
        BuildContext context = BuildContext.newContext(this.collection);
        List<ChangeAndSearchEntry> setEntries = new ArrayList<>();
        for (Entry entry : entries) {
            if(entry instanceof AbstractChangeAndSearchQuery.ChangeAndSearchEntry) {
                setEntries.add((ChangeAndSearchEntry) entry);
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
            for (ChangeAndSearchEntry changeAndSearchEntry : setEntries) {
                update.append(changeAndSearchEntry.getField(), changeAndSearchEntry.getValue());
            }
            this.collection.getCollection().updateOne(document, new Document("$set", update));
        }
        return result;
    }
}
