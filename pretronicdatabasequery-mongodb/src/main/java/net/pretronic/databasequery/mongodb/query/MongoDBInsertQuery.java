package net.pretronic.databasequery.mongodb.query;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.common.query.EntryOption;
import net.pretronic.databasequery.common.query.result.DefaultQueryResult;
import net.pretronic.databasequery.common.query.result.DefaultQueryResultEntry;
import net.pretronic.databasequery.common.query.type.AbstractInsertQuery;
import net.pretronic.databasequery.mongodb.collection.MongoDBDatabaseCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoDBInsertQuery extends AbstractInsertQuery<MongoDBDatabaseCollection> {

    private final MongoCollection<Document> mongoCollection;

    public MongoDBInsertQuery(MongoDBDatabaseCollection collection) {
        super(collection);
        this.mongoCollection = collection.getCollection();
    }

    @Override
    public QueryResult executeAndGetGeneratedKeys(String[] keyColumns, Object... values) {
        List<Object> keys = new ArrayList<>();
        List<Document> documents = new ArrayList<>();

        boolean first = true;
        int preparedCount = 0;
        for (Entry entry : this.entries) {
            int count = 0;

            for (Object value : entry.getValues()) {
                if(EntryOption.PREPARED == value) {
                    value = values[preparedCount++];
                }
                Document document;
                if(first) {
                    first = false;
                    document = new Document();
                    for (Map.Entry<String, Integer> nextIdEntry : getNextIds().entrySet()) {
                        long id = nextIdEntry.getValue();
                        document.append(nextIdEntry.getKey(), id);
                        keys.add(id);
                    }
                    documents.add(document.append(entry.getField(), value));
                } else {
                    documents.get(count).append(entry.getField(), value);
                }
                count++;
            }
        }
        mongoCollection.insertMany(documents);

        DefaultQueryResult result = new DefaultQueryResult();
        for (int i = 0; i < keyColumns.length; i++) {
            result.addEntry(new DefaultQueryResultEntry(this.collection.getDatabase().getDriver()).addEntry(keyColumns[i], keys.get(i)));
        }
        return result;
    }

    public Map<String, Integer> getNextIds() {
        MongoCollection<Document> counters = this.collection.getDatabase().getCounters();
        Map<String, Integer> nextIds = new HashMap<>();
        for (Document document : counters.find(new Document("collectionName", this.collection.getName()))) {
            Document update = new Document().append("$inc", new Document("nextId", 1));
            Document result = counters.findOneAndUpdate(document, update);
            nextIds.put(document.getString("field"), result.getInteger("nextId"));
        }
        return nextIds;
    }
}
